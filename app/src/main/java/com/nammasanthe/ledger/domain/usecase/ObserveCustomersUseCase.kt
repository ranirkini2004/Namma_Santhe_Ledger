package com.nammasanthe.ledger.domain.usecase

import com.nammasanthe.ledger.domain.repository.CustomerRepository
import javax.inject.Inject

class ObserveCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke() = customerRepository.observeCustomers()
}
