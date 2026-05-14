package com.nammasanthe.ledger.domain.repository

import com.nammasanthe.ledger.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun observeCustomers(): Flow<List<Customer>>
    fun observeCustomer(customerId: Long): Flow<Customer?>
    suspend fun saveCustomer(customer: Customer): Long
    suspend fun deleteCustomer(customerId: Long)
}
