package com.nammasanthe.ledger.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NammaSantheDestination(
    val route: String,
    val label: String,
    val icon: ImageVector? = null
) {
    data object Splash : NammaSantheDestination("splash", "Splash")
    data object Home : NammaSantheDestination("home", "Home", Icons.Outlined.Home)
    data object Customers : NammaSantheDestination("customers", "Customers", Icons.Outlined.Group)
    data object Summary : NammaSantheDestination("summary", "Summary", Icons.Outlined.Assessment)
    data object TransactionEntry : NammaSantheDestination("transaction/{customerId}", "Transaction")
    data object CustomerForm : NammaSantheDestination("customer-form/{customerId}", "Customer")
    data object Ledger : NammaSantheDestination("ledger/{customerId}", "Ledger")
}

fun customerFormRoute(customerId: Long? = null): String = "customer-form/${customerId ?: 0L}"

fun transactionRoute(customerId: Long? = null): String = "transaction/${customerId ?: 0L}"

fun ledgerRoute(customerId: Long): String = "ledger/$customerId"

val bottomNavDestinations = listOf(
    NammaSantheDestination.Home,
    NammaSantheDestination.Customers,
    NammaSantheDestination.Summary
)
