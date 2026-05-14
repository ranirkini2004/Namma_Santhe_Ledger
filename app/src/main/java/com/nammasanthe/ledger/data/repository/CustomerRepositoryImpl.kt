package com.nammasanthe.ledger.data.repository

import com.nammasanthe.ledger.data.local.dao.CustomerDao
import com.nammasanthe.ledger.data.mapper.toDomain
import com.nammasanthe.ledger.data.mapper.toEntity
import com.nammasanthe.ledger.domain.model.Customer
import com.nammasanthe.ledger.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao
) : CustomerRepository {

    override fun observeCustomers(): Flow<List<Customer>> {
        return customerDao.observeCustomersWithBalance().map { customers -> customers.map { it.toDomain() } }
    }

    override fun observeCustomer(customerId: Long): Flow<Customer?> {
        return customerDao.observeCustomerWithBalance(customerId).map { it?.toDomain() }
    }

    override suspend fun saveCustomer(customer: Customer): Long {
        return if (customer.id == 0L) {
            customerDao.insert(customer.toEntity())
        } else {
            customerDao.update(customer.toEntity())
            customer.id
        }
    }

    override suspend fun deleteCustomer(customerId: Long) {
        customerDao.deleteById(customerId)
    }
}
