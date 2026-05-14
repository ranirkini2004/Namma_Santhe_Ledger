package com.nammasanthe.ledger.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    fun format(amount: Double): String = formatter.format(amount)
}
