package com.nammasanthe.ledger.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object WhatsAppHelper {
    fun buildReminderIntent(customerName: String, phone: String, amount: Double): Intent {
        val cleanPhone = phone.filter { it.isDigit() }
        val message = "Namaskara $customerName, your pending amount is ${CurrencyFormatter.format(amount)}. Kindly clear the dues. Thank you."
        val encodedMessage = Uri.encode(message)
        val url = if (cleanPhone.isNotBlank()) {
            "https://wa.me/$cleanPhone?text=$encodedMessage"
        } else {
            "https://wa.me/?text=$encodedMessage"
        }

        return Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.whatsapp")
        }
    }

    fun canHandle(context: Context, intent: Intent): Boolean {
        return intent.resolveActivity(context.packageManager) != null
    }
}
