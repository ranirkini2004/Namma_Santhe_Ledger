package com.nammasanthe.ledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nammasanthe.ledger.data.local.entity.TransactionEntity
import com.nammasanthe.ledger.data.local.model.PeriodAggregateLocal
import com.nammasanthe.ledger.data.local.model.RecentTransactionLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Query(
        """
        SELECT * FROM ledger_transactions
        WHERE customerId = :customerId
        ORDER BY createdAt DESC
        """
    )
    fun observeTransactionsForCustomer(customerId: Long): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT t.id, t.customerId, c.name AS customerName, t.amount, t.type, t.note, t.createdAt
        FROM ledger_transactions t
        INNER JOIN customers c ON c.id = t.customerId
        ORDER BY t.createdAt DESC
        LIMIT :limit
        """
    )
    fun observeRecentTransactions(limit: Int): Flow<List<RecentTransactionLocal>>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE -amount END), 0)
        FROM ledger_transactions
        """
    )
    fun observeTotalOutstanding(): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0)
        FROM ledger_transactions
        WHERE type = 'CREDIT' AND createdAt BETWEEN :startTime AND :endTime
        """
    )
    fun observeTodayCreditTotal(startTime: Long, endTime: Long): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0)
        FROM ledger_transactions
        WHERE type = 'PAYMENT' AND createdAt BETWEEN :startTime AND :endTime
        """
    )
    fun observeTodayPaymentTotal(startTime: Long, endTime: Long): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0)
        FROM ledger_transactions
        WHERE type = 'CREDIT' AND createdAt BETWEEN :startTime AND :endTime
        """
    )
    fun observePeriodCreditTotal(startTime: Long, endTime: Long): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0)
        FROM ledger_transactions
        WHERE type = 'PAYMENT' AND createdAt BETWEEN :startTime AND :endTime
        """
    )
    fun observePeriodPaymentTotal(startTime: Long, endTime: Long): Flow<Double>

    @Query(
        """
        SELECT
            strftime('%Y-%m-%d', createdAt / 1000, 'unixepoch', 'localtime') AS period,
            COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0) AS creditTotal,
            COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0) AS paymentTotal
        FROM ledger_transactions
        WHERE createdAt BETWEEN :startTime AND :endTime
        GROUP BY period
        ORDER BY period ASC
        """
    )
    fun observeAggregatesByPeriod(startTime: Long, endTime: Long): Flow<List<PeriodAggregateLocal>>
}
