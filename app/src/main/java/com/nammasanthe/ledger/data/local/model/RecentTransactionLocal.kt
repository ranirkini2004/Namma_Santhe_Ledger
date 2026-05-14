package com.nammasanthe.ledger.data.local.model

import com.nammasanthe.ledger.domain.model.TransactionType

data class RecentTransactionLocal(
    val id: Long,
    val customerId: Long,
    val customerName: String,
    val amount: Double,
    val type: TransactionType,
    val note: String,
    val createdAt: Long
)
