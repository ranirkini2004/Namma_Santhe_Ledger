package com.nammasanthe.ledger.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammasanthe.ledger.domain.model.DailySummary
import com.nammasanthe.ledger.domain.usecase.ObserveDailySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SummaryUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val summary: DailySummary = DailySummary()
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    observeDailySummaryUseCase: ObserveDailySummaryUseCase
) : ViewModel() {

    private val refreshing = MutableStateFlow(false)

    val uiState = combine(
        observeDailySummaryUseCase(),
        refreshing
    ) { summary, isRefreshing ->
        SummaryUiState(
            isLoading = false,
            isRefreshing = isRefreshing,
            summary = summary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SummaryUiState()
    )

    fun refresh() {
        viewModelScope.launch {
            refreshing.value = true
            delay(500)
            refreshing.value = false
        }
    }
}
