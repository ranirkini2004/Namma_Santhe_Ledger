package com.nammasanthe.ledger.domain.repository

import com.nammasanthe.ledger.domain.model.DailySummary
import com.nammasanthe.ledger.domain.model.DashboardStats
import com.nammasanthe.ledger.domain.model.LedgerTransaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactionsForCustomer(customerId: Long): Flow<List<LedgerTransaction>>
    fun observeDashboard(): Flow<DashboardStats>
    fun observeDailySummary(): Flow<DailySummary>
    suspend fun addTransaction(transaction: LedgerTransaction): Long
}
