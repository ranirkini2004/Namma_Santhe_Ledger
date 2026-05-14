package com.nammasanthe.ledger.domain.usecase

import com.nammasanthe.ledger.domain.model.CustomerLedger
import com.nammasanthe.ledger.domain.model.TransactionType
import com.nammasanthe.ledger.domain.repository.CustomerRepository
import com.nammasanthe.ledger.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveCustomerLedgerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(customerId: Long): Flow<CustomerLedger?> {
        return combine(
            customerRepository.observeCustomer(customerId),
            transactionRepository.observeTransactionsForCustomer(customerId)
        ) { customer, transactions ->
            customer?.let {
                CustomerLedger(
                    customer = it,
                    transactions = transactions,
                    outstandingAmount = transactions.sumOf { txn ->
                        if (txn.type == TransactionType.CREDIT) txn.amount else -txn.amount
                    }
                )
            }
        }
    }
}
