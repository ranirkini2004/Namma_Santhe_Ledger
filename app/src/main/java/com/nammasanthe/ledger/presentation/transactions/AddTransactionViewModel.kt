package com.nammasanthe.ledger.presentation.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammasanthe.ledger.domain.model.Customer
import com.nammasanthe.ledger.domain.model.LedgerTransaction
import com.nammasanthe.ledger.domain.model.TransactionType
import com.nammasanthe.ledger.domain.usecase.AddTransactionUseCase
import com.nammasanthe.ledger.domain.usecase.ObserveCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val customers: List<Customer> = emptyList(),
    val selectedCustomerId: Long = 0L,
    val amountInput: String = "",
    val transactionType: TransactionType = TransactionType.CREDIT,
    val note: String = "",
    val message: String? = null,
    val saveCompleted: Boolean = false
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCustomersUseCase: ObserveCustomersUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val preselectedCustomerId = savedStateHandle.get<Long>("customerId") ?: 0L
    private val selectedCustomerId = MutableStateFlow(preselectedCustomerId)
    private val amountInput = MutableStateFlow("")
    private val transactionType = MutableStateFlow(TransactionType.CREDIT)
    private val note = MutableStateFlow("")
    private val isSaving = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)
    private val saved = MutableStateFlow(false)

    private val transactionDraft = combine(
        selectedCustomerId,
        amountInput,
        transactionType,
        note
    ) { selectedCustomerId, amountInput, transactionType, note ->
        TransactionDraft(selectedCustomerId, amountInput, transactionType, note)
    }

    private val localState = combine(
        transactionDraft,
        isSaving,
        message,
        saved
    ) { draft, isSaving, message, saved ->
        LocalTransactionState(
            selectedCustomerId = draft.selectedCustomerId,
            amountInput = draft.amountInput,
            transactionType = draft.transactionType,
            note = draft.note,
            isSaving = isSaving,
            message = message,
            saved = saved
        )
    }

    val uiState = observeCustomersUseCase()
        .flatMapLatest { customers ->
            localState.combine(flowOf(customers)) { local, customerList ->
                AddTransactionUiState(
                    isLoading = false,
                    isSaving = local.isSaving,
                    customers = customerList.sortedBy { it.name.lowercase() },
                    selectedCustomerId = local.selectedCustomerId,
                    amountInput = local.amountInput,
                    transactionType = local.transactionType,
                    note = local.note,
                    message = local.message,
                    saveCompleted = local.saved
                )
            }
        }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AddTransactionUiState(selectedCustomerId = preselectedCustomerId)
    )

    private data class LocalTransactionState(
        val selectedCustomerId: Long,
        val amountInput: String,
        val transactionType: TransactionType,
        val note: String,
        val isSaving: Boolean,
        val message: String?,
        val saved: Boolean
    )

    private data class TransactionDraft(
        val selectedCustomerId: Long,
        val amountInput: String,
        val transactionType: TransactionType,
        val note: String
    )

    fun selectCustomer(customerId: Long) {
        selectedCustomerId.value = customerId
    }

    fun setTransactionType(type: TransactionType) {
        transactionType.value = type
    }

    fun setNote(value: String) {
        note.value = value
    }

    fun addDigit(digit: String) {
        val current = amountInput.value
        if (digit == "." && current.contains(".")) return
        amountInput.value = if (current == "0" && digit != ".") digit else current + digit
    }

    fun backspace() {
        amountInput.value = amountInput.value.dropLast(1)
    }

    fun clearAmount() {
        amountInput.value = ""
    }

    fun clearMessage() {
        message.value = null
    }

    fun saveTransaction() {
        viewModelScope.launch {
            isSaving.value = true
            runCatching {
                addTransactionUseCase(
                    LedgerTransaction(
                        customerId = selectedCustomerId.value,
                        amount = amountInput.value.toDoubleOrNull() ?: 0.0,
                        type = transactionType.value,
                        note = note.value
                    )
                )
            }.onSuccess {
                saved.value = true
                isSaving.value = false
                message.value = "Transaction saved."
            }.onFailure {
                isSaving.value = false
                message.value = it.message ?: "Unable to save transaction."
            }
        }
    }
}
