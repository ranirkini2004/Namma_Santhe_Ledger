package com.nammasanthe.ledger.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object AppDateTimeFormatter {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM, hh:mm a", Locale("en", "IN"))
    private val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

    private val zoneId: ZoneId
        get() = ZoneId.systemDefault()

    fun formatDateTime(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).format(dateTimeFormatter)
    }

    fun formatDate(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).format(dateFormatter)
    }

    fun dayShortLabel(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).format(dayFormatter)
    }
}
