package com.nammasanthe.ledger.domain.usecase

import com.nammasanthe.ledger.domain.repository.CustomerRepository
import javax.inject.Inject

class DeleteCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerId: Long) {
        customerRepository.deleteCustomer(customerId)
    }
}
