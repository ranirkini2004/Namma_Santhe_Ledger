package com.nammasanthe.ledger.domain.usecase

import com.nammasanthe.ledger.domain.repository.CustomerRepository
import javax.inject.Inject

class ObserveCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(customerId: Long) = customerRepository.observeCustomer(customerId)
}
