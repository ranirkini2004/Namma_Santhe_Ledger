package com.nammasanthe.ledger.presentation.customers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammasanthe.ledger.domain.model.Customer
import com.nammasanthe.ledger.domain.usecase.ObserveCustomerUseCase
import com.nammasanthe.ledger.domain.usecase.SaveCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CustomerFormUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val customerId: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val name: String = "",
    val phone: String = "",
    val imageUri: String? = null,
    val message: String? = null,
    val saveCompleted: Boolean = false
)

@HiltViewModel
class CustomerFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeCustomerUseCase: ObserveCustomerUseCase,
    private val saveCustomerUseCase: SaveCustomerUseCase
) : ViewModel() {

    private val customerId = savedStateHandle.get<Long>("customerId") ?: 0L
    private val _uiState = MutableStateFlow(CustomerFormUiState(customerId = customerId, isEditMode = customerId > 0L))
    val uiState: StateFlow<CustomerFormUiState> = _uiState.asStateFlow()

    init {
        if (customerId > 0L) {
            viewModelScope.launch {
                observeCustomerUseCase(customerId)
                    .collect { customer ->
                        customer?.let {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                createdAt = it.createdAt,
                                name = it.name,
                                phone = it.phone,
                                imageUri = it.imageUri
                            )
                        }
                    }
            }
        } else {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun onImageSelected(uri: String?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun saveCustomer() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            runCatching {
                saveCustomerUseCase(
                    Customer(
                        id = _uiState.value.customerId,
                        createdAt = _uiState.value.createdAt,
                        name = _uiState.value.name,
                        phone = _uiState.value.phone,
                        imageUri = _uiState.value.imageUri
                    )
                )
            }.onSuccess { id ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveCompleted = true,
                    customerId = id,
                    message = "Customer saved successfully."
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = it.message ?: "Unable to save customer."
                )
            }
        }
    }
}
