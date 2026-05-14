package com.nammasanthe.ledger.domain.model

data class DailyChartPoint(
    val label: String,
    val creditTotal: Double,
    val paymentTotal: Double
)

data class MonthlyOverview(
    val currentMonthSales: Double = 0.0,
    val currentMonthCollections: Double = 0.0,
    val currentMonthOutstanding: Double = 0.0
)

data class DailySummary(
    val todaySales: Double = 0.0,
    val pendingDues: Double = 0.0,
    val amountCollected: Double = 0.0,
    val weeklyChart: List<DailyChartPoint> = emptyList(),
    val monthlyOverview: MonthlyOverview = MonthlyOverview()
)
