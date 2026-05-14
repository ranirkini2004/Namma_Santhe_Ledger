package com.nammasanthe.ledger.presentation.ledger

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammasanthe.ledger.domain.model.CustomerLedger
import com.nammasanthe.ledger.domain.usecase.ObserveCustomerLedgerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LedgerUiState(
    val isLoading: Boolean = true,
    val customerLedger: CustomerLedger? = null
)

@HiltViewModel
class LedgerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCustomerLedgerUseCase: ObserveCustomerLedgerUseCase
) : ViewModel() {

    private val customerId = savedStateHandle.get<Long>("customerId") ?: 0L

    val uiState = observeCustomerLedgerUseCase(customerId)
        .map { ledger ->
            LedgerUiState(
                isLoading = false,
                customerLedger = ledger
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LedgerUiState()
        )
}
