package com.nammasanthe.ledger.presentation.ledger

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammasanthe.ledger.domain.model.CustomerLedger
import com.nammasanthe.ledger.domain.model.LedgerTransaction
import com.nammasanthe.ledger.domain.model.TransactionType
import com.nammasanthe.ledger.presentation.components.CustomerAvatar
import com.nammasanthe.ledger.presentation.components.EmptyStateView
import com.nammasanthe.ledger.presentation.components.TransactionTypeTag
import com.nammasanthe.ledger.ui.theme.CreditOrange
import com.nammasanthe.ledger.ui.theme.ForestGreen
import com.nammasanthe.ledger.utils.AppDateTimeFormatter
import com.nammasanthe.ledger.utils.CurrencyFormatter
import com.nammasanthe.ledger.utils.WhatsAppHelper
import kotlinx.coroutines.launch

private data class LedgerEntryUi(
    val transaction: LedgerTransaction,
    val runningBalance: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    onBack: () -> Unit,
    onAddTransaction: (Long) -> Unit,
    onEditCustomer: (Long) -> Unit,
    viewModel: LedgerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Customer Ledger") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val ledger = uiState.customerLedger
        if (ledger == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    title = "Ledger unavailable",
                    description = "This customer could not be loaded."
                )
            }
            return@Scaffold
        }

        val ledgerEntries = ledger.toLedgerEntries()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp, 12.dp, 20.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CustomerAvatar(
                                name = ledger.customer.name,
                                imageUri = ledger.customer.imageUri,
                                modifier = Modifier.size(64.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ledger.customer.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(ledger.customer.phone, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { onEditCustomer(ledger.customer.id) }) {
                                Icon(Icons.Outlined.Edit, contentDescription = null)
                            }
                        }
                        Text(
                            text = CurrencyFormatter.format(ledger.outstandingAmount),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (ledger.outstandingAmount > 0.0) CreditOrange else ForestGreen
                        )
                        Text(
                            text = if (ledger.outstandingAmount > 0.0) "Current outstanding amount" else "Customer account is settled",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { onAddTransaction(ledger.customer.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Outlined.SwapHoriz, contentDescription = null)
                                Text(" Add")
                            }
                            Button(
                                onClick = {
                                    if (!openReminder(context, ledger)) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("WhatsApp is not installed on this device.")
                                        }
                                    }
                                },
                                enabled = ledger.outstandingAmount > 0.0,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Outlined.Send, contentDescription = null)
                                Text(" Reminder")
                            }
                        }
                    }
                }
            }
            item {
                Text("Transaction history", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            if (ledgerEntries.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No ledger entries yet",
                        description = "Use the add button to record a credit or a payment."
                    )
                }
            } else {
                items(ledgerEntries, key = { it.transaction.id }) { entry ->
                    LedgerRow(entry = entry)
                }
            }
        }
    }
}

@Composable
private fun LedgerRow(entry: LedgerEntryUi) {
    val isCredit = entry.transaction.type == TransactionType.CREDIT
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TransactionTypeTag(type = entry.transaction.type)
                Text(
                    text = AppDateTimeFormatter.formatDateTime(entry.transaction.createdAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = CurrencyFormatter.format(entry.transaction.amount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isCredit) CreditOrange else ForestGreen
                    )
                    if (entry.transaction.note.isNotBlank()) {
                        Text(
                            text = entry.transaction.note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Balance", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = CurrencyFormatter.format(entry.runningBalance),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun CustomerLedger.toLedgerEntries(): List<LedgerEntryUi> {
    var runningBalance = 0.0
    return transactions
        .sortedBy { it.createdAt }
        .map { transaction ->
            runningBalance += if (transaction.type == TransactionType.CREDIT) transaction.amount else -transaction.amount
            LedgerEntryUi(transaction = transaction, runningBalance = runningBalance)
        }
        .reversed()
}

private fun openReminder(
    context: Context,
    ledger: CustomerLedger
): Boolean {
    try {
        val intent = WhatsAppHelper.buildReminderIntent(
            customerName = ledger.customer.name,
            phone = ledger.customer.phone,
            amount = ledger.outstandingAmount
        )
        if (WhatsAppHelper.canHandle(context, intent)) {
            context.startActivity(intent)
            return true
        } else {
            throw ActivityNotFoundException("WhatsApp not available.")
        }
    } catch (_: Exception) {
        return false
    }
}
