package com.nammasanthe.ledger.domain.usecase

import com.nammasanthe.ledger.domain.model.Customer
import com.nammasanthe.ledger.domain.repository.CustomerRepository
import javax.inject.Inject

class SaveCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer): Long {
        require(customer.name.isNotBlank()) { "Customer name is required." }
        require(customer.phone.isNotBlank()) { "Phone number is required." }
        return customerRepository.saveCustomer(customer)
    }
}
