package com.nammasanthe.ledger.presentation.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammasanthe.ledger.domain.model.Customer
import com.nammasanthe.ledger.domain.model.TransactionType
import com.nammasanthe.ledger.presentation.components.CustomerAvatar
import com.nammasanthe.ledger.presentation.components.EmptyStateView
import com.nammasanthe.ledger.presentation.components.NumericKeyButton
import com.nammasanthe.ledger.presentation.components.SearchTextField
import com.nammasanthe.ledger.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCustomerDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val selectedCustomer = uiState.customers.firstOrNull { it.id == uiState.selectedCustomerId }
    val filteredCustomers = uiState.customers.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.saveCompleted) {
        if (uiState.saveCompleted) {
            onSaved(uiState.selectedCustomerId)
        }
    }

    if (showCustomerDialog) {
        AlertDialog(
            onDismissRequest = { showCustomerDialog = false },
            title = { Text("Choose customer") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SearchTextField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Search customer"
                    )
                    if (filteredCustomers.isEmpty()) {
                        EmptyStateView(
                            title = "No matching customers",
                            description = "Try another search term."
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(filteredCustomers, key = { it.id }) { customer ->
                                CustomerPickerRow(
                                    customer = customer,
                                    onClick = {
                                        viewModel.selectCustomer(customer.id)
                                        showCustomerDialog = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCustomerDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quick transaction") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp, 12.dp, 20.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                OutlinedCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Step 1: select customer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        if (selectedCustomer == null) {
                            Button(onClick = { showCustomerDialog = true }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Outlined.Person, contentDescription = null)
                                Spacer(Modifier.size(8.dp))
                                Text("Choose customer")
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCustomerDialog = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CustomerAvatar(
                                    name = selectedCustomer.name,
                                    imageUri = selectedCustomer.imageUri,
                                    modifier = Modifier.size(56.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(selectedCustomer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Text(selectedCustomer.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                TextButton(onClick = { showCustomerDialog = true }) {
                                    Text("Change")
                                }
                            }
                        }
                    }
                }
            }
            item {
                Text("Step 2: enter amount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    TransactionType.entries.forEachIndexed { index, type ->
                        SegmentedButton(
                            selected = uiState.transactionType == type,
                            onClick = { viewModel.setTransactionType(type) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = TransactionType.entries.size),
                            label = {
                                Text(if (type == TransactionType.CREDIT) "Credit" else "Payment")
                            }
                        )
                    }
                }
            }
            item {
                OutlinedCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = CurrencyFormatter.format(uiState.amountInput.toDoubleOrNull() ?: 0.0),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            items(
                listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf(".", "0", "⌫")
                )
            ) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { label ->
                        NumericKeyButton(
                            label = label,
                            modifier = Modifier.weight(1f)
                        ) {
                            when (label) {
                                "⌫" -> viewModel.backspace()
                                else -> viewModel.addDigit(label)
                            }
                        }
                    }
                }
            }
            item {
                TextButton(onClick = viewModel::clearAmount, modifier = Modifier.fillMaxWidth()) {
                    Text("Clear amount")
                }
            }
            item {
                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = viewModel::setNote,
                    label = { Text("Optional note") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                )
            }
            item {
                Button(
                    onClick = viewModel::saveTransaction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    enabled = !uiState.isSaving
                ) {
                    Text("Save transaction")
                }
            }
        }
    }
}

@Composable
private fun CustomerPickerRow(
    customer: Customer,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CustomerAvatar(
            name = customer.name,
            imageUri = customer.imageUri,
            modifier = Modifier.size(48.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text = customer.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = CurrencyFormatter.format(customer.pendingBalance),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
