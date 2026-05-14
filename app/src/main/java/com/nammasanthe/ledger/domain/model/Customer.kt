package com.nammasanthe.ledger.domain.model

data class Customer(
    val id: Long = 0L,
    val name: String = "",
    val phone: String = "",
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val pendingBalance: Double = 0.0
)
