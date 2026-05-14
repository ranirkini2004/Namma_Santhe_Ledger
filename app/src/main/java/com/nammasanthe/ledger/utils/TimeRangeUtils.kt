package com.nammasanthe.ledger.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object TimeRangeUtils {
    private val zoneId: ZoneId
        get() = ZoneId.systemDefault()

    fun startOfToday(): Long = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()

    fun endOfToday(): Long = LocalDate.now(zoneId)
        .atTime(LocalTime.MAX)
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()

    fun startOfCurrentMonth(): Long = LocalDate.now(zoneId)
        .withDayOfMonth(1)
        .atStartOfDay(zoneId)
        .toInstant()
        .toEpochMilli()

    fun endOfCurrentMonth(): Long = LocalDate.now(zoneId)
        .withDayOfMonth(LocalDate.now(zoneId).lengthOfMonth())
        .atTime(LocalTime.MAX)
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()

    fun lastSevenDays(): List<LocalDate> {
        val today = LocalDate.now(zoneId)
        return (6 downTo 0).map { today.minusDays(it.toLong()) }
    }

    fun localDateToEpochMillis(date: LocalDate): Long {
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun localDateKeyToLabel(dateKey: String): String {
        val date = LocalDate.parse(dateKey)
        return date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.titlecase() }
    }

    fun nowMinusDays(days: Long): Long {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), zoneId)
            .minusDays(days)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
    }
}
