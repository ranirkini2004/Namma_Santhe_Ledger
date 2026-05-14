package com.nammasanthe.ledger.domain.model

data class DashboardStats(
    val totalOutstandingAmount: Double = 0.0,
    val todaySales: Double = 0.0,
    val amountCollected: Double = 0.0,
    val totalCustomers: Int = 0,
    val recentTransactions: List<LedgerTransaction> = emptyList()
)
