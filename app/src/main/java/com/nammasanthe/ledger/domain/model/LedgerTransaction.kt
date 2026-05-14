package com.nammasanthe.ledger.domain.model

data class LedgerTransaction(
    val id: Long = 0L,
    val customerId: Long,
    val customerName: String? = null,
    val amount: Double,
    val type: TransactionType,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
