package com.nammasanthe.ledger.data.repository

import com.nammasanthe.ledger.data.local.dao.CustomerDao
import com.nammasanthe.ledger.data.local.dao.TransactionDao
import com.nammasanthe.ledger.data.local.model.PeriodAggregateLocal
import com.nammasanthe.ledger.data.mapper.toDomain
import com.nammasanthe.ledger.data.mapper.toEntity
import com.nammasanthe.ledger.domain.model.DailyChartPoint
import com.nammasanthe.ledger.domain.model.DailySummary
import com.nammasanthe.ledger.domain.model.DashboardStats
import com.nammasanthe.ledger.domain.model.LedgerTransaction
import com.nammasanthe.ledger.domain.model.MonthlyOverview
import com.nammasanthe.ledger.domain.repository.TransactionRepository
import com.nammasanthe.ledger.utils.TimeRangeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun observeTransactionsForCustomer(customerId: Long): Flow<List<LedgerTransaction>> {
        return transactionDao.observeTransactionsForCustomer(customerId)
            .map { transactions -> transactions.map { it.toDomain() } }
    }

    override fun observeDashboard(): Flow<DashboardStats> {
        val startOfToday = TimeRangeUtils.startOfToday()
        val endOfToday = TimeRangeUtils.endOfToday()
        return combine(
            transactionDao.observeTotalOutstanding(),
            transactionDao.observeTodayCreditTotal(startOfToday, endOfToday),
            transactionDao.observeTodayPaymentTotal(startOfToday, endOfToday),
            customerDao.observeCustomerCount(),
            transactionDao.observeRecentTransactions(limit = 6)
        ) { outstanding, todaySales, collected, totalCustomers, recentTransactions ->
            DashboardStats(
                totalOutstandingAmount = outstanding,
                todaySales = todaySales,
                amountCollected = collected,
                totalCustomers = totalCustomers,
                recentTransactions = recentTransactions.map { it.toDomain() }
            )
        }
    }

    override fun observeDailySummary(): Flow<DailySummary> {
        val startOfToday = TimeRangeUtils.startOfToday()
        val endOfToday = TimeRangeUtils.endOfToday()
        val startOfMonth = TimeRangeUtils.startOfCurrentMonth()
        val endOfMonth = TimeRangeUtils.endOfCurrentMonth()
        val weeklyStart = TimeRangeUtils.localDateToEpochMillis(TimeRangeUtils.lastSevenDays().first())

        val coreSummary = combine(
            transactionDao.observeTodayCreditTotal(startOfToday, endOfToday),
            transactionDao.observeTodayPaymentTotal(startOfToday, endOfToday),
            transactionDao.observeTotalOutstanding(),
            transactionDao.observePeriodCreditTotal(startOfMonth, endOfMonth),
            transactionDao.observePeriodPaymentTotal(startOfMonth, endOfMonth)
        ) { todaySales, todayCollected, outstanding, monthSales, monthCollections ->
            DailySummary(
                todaySales = todaySales,
                pendingDues = outstanding,
                amountCollected = todayCollected,
                weeklyChart = emptyList(),
                monthlyOverview = MonthlyOverview(
                    currentMonthSales = monthSales,
                    currentMonthCollections = monthCollections,
                    currentMonthOutstanding = outstanding
                )
            )
        }

        return combine(
            coreSummary,
            transactionDao.observeAggregatesByPeriod(weeklyStart, endOfToday)
        ) { summary, weeklyAggregates ->
            summary.copy(weeklyChart = weeklyChartData(weeklyAggregates))
        }
    }

    override suspend fun addTransaction(transaction: LedgerTransaction): Long {
        return transactionDao.insert(transaction.toEntity())
    }

    private fun weeklyChartData(aggregates: List<PeriodAggregateLocal>): List<DailyChartPoint> {
        val aggregateMap = aggregates.associateBy { it.period }
        return TimeRangeUtils.lastSevenDays().map { date ->
            val key = date.toString()
            val aggregate = aggregateMap[key]
            DailyChartPoint(
                label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                creditTotal = aggregate?.creditTotal ?: 0.0,
                paymentTotal = aggregate?.paymentTotal ?: 0.0
            )
        }
    }
}
