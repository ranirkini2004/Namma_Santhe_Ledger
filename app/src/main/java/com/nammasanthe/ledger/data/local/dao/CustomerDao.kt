package com.nammasanthe.ledger.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nammasanthe.ledger.data.local.entity.CustomerEntity
import com.nammasanthe.ledger.data.local.model.CustomerBalanceLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query(
        """
        SELECT c.id, c.name, c.phone, c.imageUri, c.createdAt,
        COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE -t.amount END), 0) AS pendingBalance
        FROM customers c
        LEFT JOIN ledger_transactions t ON c.id = t.customerId
        GROUP BY c.id
        ORDER BY c.createdAt DESC
        """
    )
    fun observeCustomersWithBalance(): Flow<List<CustomerBalanceLocal>>

    @Query(
        """
        SELECT c.id, c.name, c.phone, c.imageUri, c.createdAt,
        COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE -t.amount END), 0) AS pendingBalance
        FROM customers c
        LEFT JOIN ledger_transactions t ON c.id = t.customerId
        WHERE c.id = :customerId
        GROUP BY c.id
        """
    )
    fun observeCustomerWithBalance(customerId: Long): Flow<CustomerBalanceLocal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: CustomerEntity): Long

    @Update
    suspend fun update(customer: CustomerEntity)

    @Query("DELETE FROM customers WHERE id = :customerId")
    suspend fun deleteById(customerId: Long)

    @Query("SELECT COUNT(*) FROM customers")
    fun observeCustomerCount(): Flow<Int>
}
