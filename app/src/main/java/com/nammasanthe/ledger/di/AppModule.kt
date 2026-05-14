package com.nammasanthe.ledger.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nammasanthe.ledger.data.local.dao.CustomerDao
import com.nammasanthe.ledger.data.local.dao.TransactionDao
import com.nammasanthe.ledger.data.local.database.LedgerDatabase
import com.nammasanthe.ledger.data.repository.CustomerRepositoryImpl
import com.nammasanthe.ledger.data.repository.TransactionRepositoryImpl
import com.nammasanthe.ledger.domain.repository.CustomerRepository
import com.nammasanthe.ledger.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LedgerDatabase {
        return Room.databaseBuilder(context, LedgerDatabase::class.java, "namma_santhe_ledger.db")
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                            seedSampleData(db)
                        }
                    }
                }
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCustomerDao(database: LedgerDatabase): CustomerDao = database.customerDao()

    @Provides
    fun provideTransactionDao(database: LedgerDatabase): TransactionDao = database.transactionDao()

    private fun seedSampleData(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()
        val customers = listOf(
            arrayOf("Lakshmi Traders", "919845612300", null, now - 5_184_000L),
            arrayOf("Shivu Vegetables", "919740011223", null, now - 3_456_000L),
            arrayOf("Rekha Stores", "919845600111", null, now - 2_592_000L),
            arrayOf("Mahadev Flowers", "919845655544", null, now - 1_728_000L)
        )

        customers.forEach { args ->
            db.execSQL(
                "INSERT INTO customers(name, phone, imageUri, createdAt) VALUES(?, ?, ?, ?)",
                args
            )
        }

        val transactions = listOf(
            arrayOf(1L, 650.0, "CREDIT", "Tomato crates", now - 172_800_000L),
            arrayOf(1L, 200.0, "PAYMENT", "Paid in cash", now - 86_400_000L),
            arrayOf(2L, 1200.0, "CREDIT", "Weekly greens supply", now - 43_200_000L),
            arrayOf(3L, 450.0, "CREDIT", "Rice bags", now - 21_600_000L),
            arrayOf(3L, 250.0, "PAYMENT", "UPI settlement", now - 12_600_000L),
            arrayOf(4L, 900.0, "CREDIT", "Flower bundles", now - 7_200_000L),
            arrayOf(2L, 300.0, "PAYMENT", "Part payment", now - 3_600_000L),
            arrayOf(1L, 180.0, "CREDIT", "Morning greens", now - 1_800_000L)
        )

        transactions.forEach { args ->
            db.execSQL(
                "INSERT INTO ledger_transactions(customerId, amount, type, note, createdAt) VALUES(?, ?, ?, ?, ?)",
                args
            )
        }
    }
}
