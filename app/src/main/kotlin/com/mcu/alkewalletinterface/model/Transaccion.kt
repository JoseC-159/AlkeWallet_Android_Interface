package com.mcu.alkewalletinterface.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Transaccion(
    val id: Int,
    val tipo: String, // Ej: "INGRESO", "ENVIO", "RECEPCION"
    val monto: Double,
    val concepto: String,
    val nombreContraparte: String // A quién se envía o de quién se recibe
) {
    private val fecha: Date = Date()

    fun getFechaFormateada(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(fecha)
    }
}
