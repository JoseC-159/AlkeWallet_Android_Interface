package com.mcu.alkewalletinterface.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val userEmail: String,
    val balance: Double,
    val tipoMoneda: String
)
