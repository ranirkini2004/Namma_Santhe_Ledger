package com.nammasanthe.ledger.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammasanthe.ledger.domain.model.Customer
import com.nammasanthe.ledger.domain.usecase.DeleteCustomerUseCase
import com.nammasanthe.ledger.domain.usecase.ObserveCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CustomerSortOption(val label: String) {
    HIGHEST_DUE("Highest dues"),
    ALPHABETICAL("A-Z"),
    RECENT("Recently added")
}

data class CustomersUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val query: String = "",
    val duesOnly: Boolean = false,
    val sortOption: CustomerSortOption = CustomerSortOption.HIGHEST_DUE,
    val customers: List<Customer> = emptyList(),
    val message: String? = null
)

@HiltViewModel
class CustomersViewModel @Inject constructor(
    observeCustomersUseCase: ObserveCustomersUseCase,
    private val deleteCustomerUseCase: DeleteCustomerUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val duesOnly = MutableStateFlow(false)
    private val sortOption = MutableStateFlow(CustomerSortOption.HIGHEST_DUE)
    private val refreshing = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    private val filters = combine(
        query,
        duesOnly,
        sortOption,
        refreshing,
        message
    ) { query, duesOnly, sort, refreshing, message ->
        FiltersState(query, duesOnly, sort, refreshing, message)
    }

    val uiState = observeCustomersUseCase()
        .flatMapLatest { customers ->
            filters.combine(flowOf(customers)) { filterState, customerList ->
                CustomersUiState(
                    isLoading = false,
                    isRefreshing = filterState.isRefreshing,
                    query = filterState.query,
                    duesOnly = filterState.duesOnly,
                    sortOption = filterState.sortOption,
                    customers = customerList
                        .filter { customer ->
                            val matchesQuery = customer.name.contains(filterState.query, ignoreCase = true) ||
                                customer.phone.contains(filterState.query, ignoreCase = true)
                            val matchesDueFilter = !filterState.duesOnly || customer.pendingBalance > 0.0
                            matchesQuery && matchesDueFilter
                        }
                        .let { filtered ->
                            when (filterState.sortOption) {
                                CustomerSortOption.HIGHEST_DUE -> filtered.sortedByDescending { it.pendingBalance }
                                CustomerSortOption.ALPHABETICAL -> filtered.sortedBy { it.name.lowercase() }
                                CustomerSortOption.RECENT -> filtered.sortedByDescending { it.createdAt }
                            }
                        },
                    message = filterState.message
                )
            }
        }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CustomersUiState()
    )

    private data class FiltersState(
        val query: String,
        val duesOnly: Boolean,
        val sortOption: CustomerSortOption,
        val isRefreshing: Boolean,
        val message: String?
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun setDuesOnly(enabled: Boolean) {
        duesOnly.value = enabled
    }

    fun setSortOption(option: CustomerSortOption) {
        sortOption.value = option
    }

    fun refresh() {
        viewModelScope.launch {
            refreshing.value = true
            delay(400)
            refreshing.value = false
        }
    }

    fun deleteCustomer(customerId: Long) {
        viewModelScope.launch {
            runCatching {
                deleteCustomerUseCase(customerId)
            }.onSuccess {
                message.value = "Customer deleted."
            }.onFailure {
                message.value = it.message ?: "Unable to delete customer."
            }
        }
    }

    fun clearMessage() {
        message.value = null
    }
}
