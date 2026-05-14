package com.nammasanthe.ledger.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.nammasanthe.ledger.navigation.NammaSantheDestination
import com.nammasanthe.ledger.navigation.bottomNavDestinations
import com.nammasanthe.ledger.navigation.customerFormRoute
import com.nammasanthe.ledger.navigation.ledgerRoute
import com.nammasanthe.ledger.navigation.transactionRoute
import com.nammasanthe.ledger.presentation.customers.CustomerFormScreen
import com.nammasanthe.ledger.presentation.customers.CustomersScreen
import com.nammasanthe.ledger.presentation.home.HomeDashboardScreen
import com.nammasanthe.ledger.presentation.ledger.LedgerScreen
import com.nammasanthe.ledger.presentation.summary.DailySummaryScreen
import com.nammasanthe.ledger.presentation.splash.SplashScreen
import com.nammasanthe.ledger.presentation.transactions.AddTransactionScreen

@Composable
fun NammaSantheLedgerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = currentRoute in bottomNavDestinations.map { it.route }
    val showFab = showBottomBar

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                destination.icon?.let { Icon(it, contentDescription = destination.label) }
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = { navController.navigate(transactionRoute()) }
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add transaction")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NammaSantheDestination.Splash.route,
            modifier = Modifier
        ) {
            composable(NammaSantheDestination.Splash.route) {
                SplashScreen(
                    onFinished = {
                        navController.navigate(NammaSantheDestination.Home.route) {
                            popUpTo(NammaSantheDestination.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(NammaSantheDestination.Home.route) {
                HomeDashboardScreen(
                    innerPadding = innerPadding,
                    onCustomersClick = { navController.navigate(NammaSantheDestination.Customers.route) },
                    onSummaryClick = { navController.navigate(NammaSantheDestination.Summary.route) },
                    onAddTransactionClick = { navController.navigate(transactionRoute()) },
                    onCustomerOpen = { customerId -> navController.navigate(ledgerRoute(customerId)) }
                )
            }
            composable(NammaSantheDestination.Customers.route) {
                CustomersScreen(
                    innerPadding = innerPadding,
                    onAddCustomer = { navController.navigate(customerFormRoute()) },
                    onEditCustomer = { customerId -> navController.navigate(customerFormRoute(customerId)) },
                    onOpenLedger = { customerId -> navController.navigate(ledgerRoute(customerId)) }
                )
            }
            composable(NammaSantheDestination.Summary.route) {
                DailySummaryScreen(innerPadding = innerPadding)
            }
            composable(
                route = NammaSantheDestination.CustomerForm.route,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType })
            ) {
                CustomerFormScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { customerId ->
                        val previousRoute = navController.previousBackStackEntry?.destination?.route
                        navController.popBackStack()
                        if (customerId > 0L && previousRoute != NammaSantheDestination.Ledger.route) {
                            navController.navigate(ledgerRoute(customerId))
                        }
                    }
                )
            }
            composable(
                route = NammaSantheDestination.TransactionEntry.route,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType })
            ) {
                AddTransactionScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { customerId ->
                        val previousRoute = navController.previousBackStackEntry?.destination?.route
                        navController.popBackStack()
                        if (previousRoute != NammaSantheDestination.Ledger.route) {
                            navController.navigate(ledgerRoute(customerId))
                        }
                    }
                )
            }
            composable(
                route = NammaSantheDestination.Ledger.route,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType })
            ) {
                LedgerScreen(
                    onBack = { navController.popBackStack() },
                    onAddTransaction = { customerId ->
                        navController.navigate(transactionRoute(customerId))
                    },
                    onEditCustomer = { customerId ->
                        navController.navigate(customerFormRoute(customerId))
                    }
                )
            }
        }
    }
}
