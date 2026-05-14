package com.nammasanthe.ledger.domain.usecase

import com.nammasanthe.ledger.domain.model.LedgerTransaction
import com.nammasanthe.ledger.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: LedgerTransaction): Long {
        require(transaction.customerId > 0L) { "Choose a customer before saving." }
        require(transaction.amount > 0.0) { "Enter a valid amount." }
        return transactionRepository.addTransaction(transaction)
    }
}
