package com.nammasanthe.ledger.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammasanthe.ledger.domain.model.Customer
import com.nammasanthe.ledger.presentation.components.BalanceChip
import com.nammasanthe.ledger.presentation.components.CustomerAvatar
import com.nammasanthe.ledger.presentation.components.EmptyStateView
import com.nammasanthe.ledger.presentation.components.SearchTextField
import com.nammasanthe.ledger.presentation.components.SectionTitle
import com.nammasanthe.ledger.ui.theme.ClayBrown
import com.nammasanthe.ledger.ui.theme.CreditOrange
import com.nammasanthe.ledger.ui.theme.ForestGreen
import com.nammasanthe.ledger.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    innerPadding: PaddingValues,
    onAddCustomer: () -> Unit,
    onEditCustomer: (Long) -> Unit,
    onOpenLedger: (Long) -> Unit,
    viewModel: CustomersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var sortMenuExpanded by remember { mutableStateOf(false) }

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
            state = rememberPullToRefreshState(),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Customers",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Track balances, search instantly, and jump into a ledger in one tap.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilledIconButton(onClick = onAddCustomer) {
                            Icon(Icons.Outlined.Add, contentDescription = "Add customer")
                        }
                    }
                }
                item {
                    SearchTextField(
                        query = uiState.query,
                        onQueryChange = viewModel::onQueryChange,
                        placeholder = "Search by name or phone"
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = !uiState.duesOnly,
                            onClick = { viewModel.setDuesOnly(false) },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = uiState.duesOnly,
                            onClick = { viewModel.setDuesOnly(true) },
                            label = { Text("With dues") }
                        )
                        Box {
                            FilterChip(
                                selected = false,
                                onClick = { sortMenuExpanded = true },
                                label = { Text(uiState.sortOption.label) },
                                trailingIcon = {
                                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                                }
                            )
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                CustomerSortOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label) },
                                        onClick = {
                                            sortMenuExpanded = false
                                            viewModel.setSortOption(option)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    SectionTitle("Customer list")
                }
                if (uiState.customers.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = "No customers found",
                            description = "Add your first customer or change the filters to see saved ledgers."
                        )
                    }
                } else {
                    items(uiState.customers, key = { it.id }) { customer ->
                        CustomerSwipeItem(
                            customer = customer,
                            onEdit = { onEditCustomer(customer.id) },
                            onDelete = { viewModel.deleteCustomer(customer.id) },
                            onClick = { onOpenLedger(customer.id) }
                        )
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerSwipeItem(
    customer: Customer,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val value = dismissState.dismissDirection
            val isEdit = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = when {
                            dismissState.targetValue == SwipeToDismissBoxValue.EndToStart -> CreditOrange.copy(alpha = 0.18f)
                            else -> ForestGreen.copy(alpha = 0.18f)
                        },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isEdit) Arrangement.Start else Arrangement.End
            ) {
                Icon(
                    imageVector = if (isEdit) Icons.Outlined.Edit else Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = if (isEdit) ForestGreen else CreditOrange
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CustomerAvatar(
                    name = customer.name,
                    imageUri = customer.imageUri,
                    modifier = Modifier.size(58.dp)
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = customer.phone, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (customer.pendingBalance > 0.0) {
                            "Pending ${CurrencyFormatter.format(customer.pendingBalance)}"
                        } else {
                            "No pending dues"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (customer.pendingBalance > 0.0) CreditOrange else ClayBrown
                    )
                }
                BalanceChip(amount = customer.pendingBalance)
            }
        }
    }
}
