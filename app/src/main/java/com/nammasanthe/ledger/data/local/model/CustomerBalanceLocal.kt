package com.nammasanthe.ledger.data.local.model

data class CustomerBalanceLocal(
    val id: Long,
    val name: String,
    val phone: String,
    val imageUri: String?,
    val createdAt: Long,
    val pendingBalance: Double
)
