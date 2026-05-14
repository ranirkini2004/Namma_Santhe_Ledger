package com.nammasanthe.ledger.data.mapper

import com.nammasanthe.ledger.data.local.entity.CustomerEntity
import com.nammasanthe.ledger.data.local.entity.TransactionEntity
import com.nammasanthe.ledger.data.local.model.CustomerBalanceLocal
import com.nammasanthe.ledger.data.local.model.RecentTransactionLocal
import com.nammasanthe.ledger.domain.model.Customer
import com.nammasanthe.ledger.domain.model.LedgerTransaction

fun CustomerBalanceLocal.toDomain(): Customer {
    return Customer(
        id = id,
        name = name,
        phone = phone,
        imageUri = imageUri,
        createdAt = createdAt,
        pendingBalance = pendingBalance
    )
}

fun Customer.toEntity(): CustomerEntity {
    return CustomerEntity(
        id = id,
        name = name.trim(),
        phone = phone.trim(),
        imageUri = imageUri,
        createdAt = createdAt
    )
}

fun TransactionEntity.toDomain(customerName: String? = null): LedgerTransaction {
    return LedgerTransaction(
        id = id,
        customerId = customerId,
        customerName = customerName,
        amount = amount,
        type = type,
        note = note,
        createdAt = createdAt
    )
}

fun LedgerTransaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        customerId = customerId,
        amount = amount,
        type = type,
        note = note.trim(),
        createdAt = createdAt
    )
}

fun RecentTransactionLocal.toDomain(): LedgerTransaction {
    return LedgerTransaction(
        id = id,
        customerId = customerId,
        customerName = customerName,
        amount = amount,
        type = type,
        note = note,
        createdAt = createdAt
    )
}
