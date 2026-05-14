package com.nammasanthe.ledger.domain.model

data class CustomerLedger(
    val customer: Customer,
    val transactions: List<LedgerTransaction>,
    val outstandingAmount: Double
)
