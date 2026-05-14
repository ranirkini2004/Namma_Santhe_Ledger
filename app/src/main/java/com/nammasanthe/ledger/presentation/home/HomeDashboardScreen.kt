package com.nammasanthe.ledger.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.nammasanthe.ledger.presentation.components.EmptyStateView
import com.nammasanthe.ledger.presentation.components.MetricCard
import com.nammasanthe.ledger.presentation.components.SectionTitle
import com.nammasanthe.ledger.presentation.components.TransactionTypeTag
import com.nammasanthe.ledger.ui.theme.ClayBrown
import com.nammasanthe.ledger.ui.theme.ForestGreen
import com.nammasanthe.ledger.ui.theme.SandBeige
import com.nammasanthe.ledger.ui.theme.SunsetTerracotta
import com.nammasanthe.ledger.utils.AppDateTimeFormatter
import com.nammasanthe.ledger.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    innerPadding: PaddingValues,
    onCustomersClick: () -> Unit,
    onSummaryClick: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onCustomerOpen: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 96.dp)
            ) {
                item {
                    Card(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(ForestGreen, ClayBrown, SandBeige)
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Today at a glance",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = CurrencyFormatter.format(uiState.dashboardStats.totalOutstandingAmount),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Total outstanding across all customers",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Today Sales",
                            value = CurrencyFormatter.format(uiState.dashboardStats.todaySales),
                            icon = Icons.Outlined.CurrencyRupee,
                            accent = SunsetTerracotta
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Collected",
                            value = CurrencyFormatter.format(uiState.dashboardStats.amountCollected),
                            icon = Icons.Outlined.Payments,
                            accent = ForestGreen
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Customers",
                            value = uiState.dashboardStats.totalCustomers.toString(),
                            icon = Icons.Outlined.Groups,
                            accent = ClayBrown
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Open Dues",
                            value = CurrencyFormatter.format(uiState.dashboardStats.totalOutstandingAmount),
                            icon = Icons.Outlined.AccountBalanceWallet,
                            accent = ForestGreen
                        )
                    }
                }
                item {
                    SectionTitle("Quick actions")
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onAddTransactionClick
                        ) {
                            Text("Quick Add")
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onCustomersClick
                        ) {
                            Text("Customers")
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onSummaryClick
                        ) {
                            Text("Reports")
                        }
                    }
                }
                item {
                    SectionTitle("Recent transactions")
                }
                if (uiState.dashboardStats.recentTransactions.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = "No transactions yet",
                            description = "Start by recording a credit or payment from the quick add button."
                        )
                    }
                } else {
                    items(
                        items = uiState.dashboardStats.recentTransactions,
                        key = { it.id }
                    ) { transaction ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCustomerOpen(transaction.customerId) },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = transaction.customerName ?: "Customer",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = AppDateTimeFormatter.formatDateTime(transaction.createdAt),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    TransactionTypeTag(type = transaction.type)
                                }
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = CurrencyFormatter.format(transaction.amount),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (transaction.note.isNotBlank()) {
                                            Text(
                                                text = transaction.note,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
