package com.mcu.alkewalletinterface.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String,
    val tipo: String, // "INGRESO", "ENVIO", "RECEPCION"
    val monto: Double,
    val concepto: String,
    val nombreContraparte: String,
    val fecha: String
)
