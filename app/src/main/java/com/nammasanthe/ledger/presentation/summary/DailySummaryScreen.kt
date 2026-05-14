package com.nammasanthe.ledger.presentation.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammasanthe.ledger.presentation.components.MetricCard
import com.nammasanthe.ledger.presentation.components.SectionTitle
import com.nammasanthe.ledger.presentation.components.WeeklyFinanceChart
import com.nammasanthe.ledger.ui.theme.ClayBrown
import com.nammasanthe.ledger.ui.theme.ForestGreen
import com.nammasanthe.ledger.ui.theme.SunsetTerracotta
import com.nammasanthe.ledger.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySummaryScreen(
    innerPadding: PaddingValues,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            state = rememberPullToRefreshState(),
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Daily Summary",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track how much came in, how much is pending, and how the week is moving.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Today Sales",
                            value = CurrencyFormatter.format(uiState.summary.todaySales),
                            icon = Icons.Outlined.Storefront,
                            accent = SunsetTerracotta
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Collected",
                            value = CurrencyFormatter.format(uiState.summary.amountCollected),
                            icon = Icons.Outlined.Payments,
                            accent = ForestGreen
                        )
                    }
                }
                item {
                    MetricCard(
                        title = "Pending Dues",
                        value = CurrencyFormatter.format(uiState.summary.pendingDues),
                        icon = Icons.Outlined.AccountBalanceWallet,
                        accent = ClayBrown
                    )
                }
                item {
                    WeeklyFinanceChart(data = uiState.summary.weeklyChart)
                }
                item {
                    SectionTitle("Monthly overview")
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Sales",
                            value = CurrencyFormatter.format(uiState.summary.monthlyOverview.currentMonthSales),
                            icon = Icons.Outlined.Storefront,
                            accent = SunsetTerracotta
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Collections",
                            value = CurrencyFormatter.format(uiState.summary.monthlyOverview.currentMonthCollections),
                            icon = Icons.Outlined.Payments,
                            accent = ForestGreen
                        )
                    }
                }
                item {
                    MetricCard(
                        title = "Outstanding",
                        value = CurrencyFormatter.format(uiState.summary.monthlyOverview.currentMonthOutstanding),
                        icon = Icons.Outlined.AccountBalanceWallet,
                        accent = ClayBrown
                    )
                }
            }
        }
    }
}
