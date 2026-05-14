package com.nammasanthe.ledger.domain.usecase

import com.nammasanthe.ledger.domain.repository.TransactionRepository
import javax.inject.Inject

class ObserveDashboardUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke() = transactionRepository.observeDashboard()
}
