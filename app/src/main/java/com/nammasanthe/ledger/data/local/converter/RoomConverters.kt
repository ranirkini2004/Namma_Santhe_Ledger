package com.nammasanthe.ledger.data.local.converter

import androidx.room.TypeConverter
import com.nammasanthe.ledger.domain.model.TransactionType

class RoomConverters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)
}
