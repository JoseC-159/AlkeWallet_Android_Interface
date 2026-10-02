package com.mcu.alkewalletinterface.model

class Cuenta(
    var user: User,
    var balance: Double = 0.0,
    var tipoMoneda: String = "CLP"
) {
    private val historialTransacciones: MutableList<Transaccion> = mutableListOf()

    fun depositar(cantidad: Double) {
        this.balance += cantidad
    }

    fun retirar(cantidad: Double) {
        if (this.balance >= cantidad) {
            this.balance -= cantidad
        } else {
            println("Saldo insuficiente.")
        }
    }

    fun obtenerUsuario(): User = user
    fun obtenerBalance(): Double = balance
    fun obtenerTipoMoneda(): String = tipoMoneda

    fun agregarTransaccion(transaccion: Transaccion) {
        this.historialTransacciones.add(transaccion)
    }

    fun obtenerHistorial(): List<Transaccion> = historialTransacciones

    fun obtenerSaldo(): Double = this.balance
}
