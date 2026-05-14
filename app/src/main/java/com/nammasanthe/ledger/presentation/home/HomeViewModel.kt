package com.nammasanthe.ledger.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammasanthe.ledger.domain.model.DashboardStats
import com.nammasanthe.ledger.domain.usecase.ObserveDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val dashboardStats: DashboardStats = DashboardStats(),
    val message: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeDashboardUseCase: ObserveDashboardUseCase
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState = combine(
        observeDashboardUseCase(),
        isRefreshing,
        message
    ) { dashboard, refreshing, snackbar ->
        HomeUiState(
            isLoading = false,
            isRefreshing = refreshing,
            dashboardStats = dashboard,
            message = snackbar
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(500)
            isRefreshing.value = false
        }
    }

    fun clearMessage() {
        message.value = null
    }
}
