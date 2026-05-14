package com.nammasanthe.ledger.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nammasanthe.ledger.data.local.converter.RoomConverters
import com.nammasanthe.ledger.data.local.dao.CustomerDao
import com.nammasanthe.ledger.data.local.dao.TransactionDao
import com.nammasanthe.ledger.data.local.entity.CustomerEntity
import com.nammasanthe.ledger.data.local.entity.TransactionEntity

@Database(
    entities = [CustomerEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
}
