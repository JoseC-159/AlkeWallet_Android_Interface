package com.mcu.alkewalletinterface.controller

import com.mcu.alkewalletinterface.model.Cuenta
import com.mcu.alkewalletinterface.model.Transaccion
import com.mcu.alkewalletinterface.model.User
import com.mcu.alkewalletinterface.data.remote.RetrofitClient
import com.mcu.alkewalletinterface.data.local.WalletDbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class WalletNotification(
    val title: String,
    val message: String
)

class WalletController {

    companion object {
        private val usuarios = mutableListOf<User>()
        var cuentas = mutableListOf<Cuenta>()
        val notifications = mutableListOf<WalletNotification>()

        private var contadorUsuarios = 0
        private var transaccionIdCounter = 1
        private var verifUsuario = false
        var usuarioActivo: User? = null
        var cuentaActiva: Cuenta? = null

        fun obtenerCuentaActiva(): Cuenta? = cuentaActiva

        fun obtenerCuentasDisponiblesParaEnvio(): List<Cuenta> {
            val disponibles = mutableListOf<Cuenta>()
            val activeAccount = obtenerCuentaActiva()
            for (c in cuentas) {
                if (activeAccount == null || c.obtenerUsuario().obtenerId() != activeAccount.obtenerUsuario().obtenerId()) {
                    disponibles.add(c)
                }
            }
            return disponibles
        }
    }

    fun generarId(): Int {
        contadorUsuarios += 1
        return contadorUsuarios
    }

    fun obtenerUsuarioActivo(): User? = usuarioActivo

    fun cerrarSesion() {
        verifUsuario = false
        usuarioActivo = null
        cuentaActiva = null
        WalletDbHelper.clearSession()
    }

    fun mostrarBalance(): Double {
        return cuentaActiva?.obtenerBalance() ?: 0.0
    }

    fun enviarDinero(monto: Double, cuentaDestino: Cuenta?, concepto: String): Boolean {
        val activeAccount = obtenerCuentaActiva()
        if (activeAccount != null && cuentaDestino != null && activeAccount.obtenerBalance() >= monto) {
            activeAccount.balance = activeAccount.obtenerBalance() - monto
            val txEnvio = Transaccion(
                transaccionIdCounter++,
                "ENVIO",
                monto,
                concepto,
                cuentaDestino.obtenerUsuario().obtenerNombre()
            )
            activeAccount.agregarTransaccion(txEnvio)

            cuentaDestino.balance = cuentaDestino.obtenerBalance() + monto
            val txRecepcion = Transaccion(
                transaccionIdCounter++,
                "RECEPCION",
                monto,
                concepto,
                activeAccount.obtenerUsuario().obtenerNombre()
            )
            cuentaDestino.agregarTransaccion(txRecepcion)

            notifications.add(
                WalletNotification("Transferencia enviada", "Has enviado $$monto CLP a ${cuentaDestino.obtenerUsuario().obtenerNombre()}")
            )

            // Persistir en Room
            CoroutineScope(Dispatchers.IO).launch {
                val email = activeAccount.obtenerUsuario().email
                WalletDbHelper.saveAccount(email, activeAccount.obtenerBalance(), activeAccount.obtenerTipoMoneda())
                WalletDbHelper.saveTransaction(email, "ENVIO", monto, concepto, cuentaDestino.obtenerUsuario().obtenerNombre())

                val destEmail = cuentaDestino.obtenerUsuario().email
                WalletDbHelper.saveAccount(destEmail, cuentaDestino.obtenerBalance(), cuentaDestino.obtenerTipoMoneda())
                WalletDbHelper.saveTransaction(destEmail, "RECEPCION", monto, concepto, activeAccount.obtenerUsuario().name)
            }

            return true
        }
        return false
    }

    fun recibirDinero(monto: Double, concepto: String?): Boolean {
        val activeAccount = obtenerCuentaActiva()
        if (activeAccount != null) {
            activeAccount.balance = activeAccount.obtenerBalance() + monto
            val notaFinal = if (!concepto.isNullOrEmpty()) concepto else "Ingreso a billetera"
            val nuevaTx = Transaccion(
                transaccionIdCounter++,
                "INGRESO",
                monto,
                notaFinal,
                "Cuenta Propia"
            )
            activeAccount.agregarTransaccion(nuevaTx)

            notifications.add(
                WalletNotification("Ingreso recibido", "Has recibido un depósito de $$monto CLP.")
            )

            // Persistir en Room
            CoroutineScope(Dispatchers.IO).launch {
                val email = activeAccount.obtenerUsuario().email
                WalletDbHelper.saveAccount(email, activeAccount.obtenerBalance(), activeAccount.obtenerTipoMoneda())
                WalletDbHelper.saveTransaction(email, "INGRESO", monto, notaFinal, "Cuenta Propia")
            }

            return true
        }
        return false
    }

    fun crearSesionDePrueba() {
        usuarios.clear()
        cuentas.clear()

        val usuarioPrueba = User(1, "José", "jose@ejemplo.cl", "123456", 25)
        val cuentaPrueba = Cuenta(usuarioPrueba, 50000.0, "CLP")
        usuarioActivo = usuarioPrueba
        cuentaActiva = cuentaPrueba
        usuarios.add(usuarioPrueba)
        cuentas.add(cuentaPrueba)

        val amigo = User(2, "Carlos", "carlos@mail.com", "123456", 42)
        val cuentaAmigo = Cuenta(amigo, 10000.0, "CLP")
        usuarios.add(amigo)
        cuentas.add(cuentaAmigo)

        val amiga = User(3, "María", "maria@mail.com", "123456", 30)
        val cuentaAmiga = Cuenta(amiga, 25000.0, "CLP")
        usuarios.add(amiga)
        cuentas.add(cuentaAmiga)
    }

    fun cambiarMoneda(nuevaMoneda: String): Boolean {
        val activeAccount = cuentaActiva ?: return false
        val monedaActual = activeAccount.obtenerTipoMoneda()
        if (monedaActual.equals(nuevaMoneda, ignoreCase = true)) return false

        val saldoActual = activeAccount.obtenerBalance()
        var saldoEnCLP = saldoActual
        val tasaUSD = 955.0
        val tasaEUR = 1000.0

        if (monedaActual.equals("USD", ignoreCase = true)) {
            saldoEnCLP = saldoActual * tasaUSD
        } else if (monedaActual.equals("EUR", ignoreCase = true)) {
            saldoEnCLP = saldoActual * tasaEUR
        }

        val nuevoSaldo = when {
            nuevaMoneda.equals("CLP", ignoreCase = true) -> saldoEnCLP
            nuevaMoneda.equals("USD", ignoreCase = true) -> saldoEnCLP / tasaUSD
            nuevaMoneda.equals("EUR", ignoreCase = true) -> saldoEnCLP / tasaEUR
            else -> saldoEnCLP
        }

        activeAccount.balance = nuevoSaldo
        activeAccount.tipoMoneda = nuevaMoneda

        notifications.add(
            WalletNotification("Conversión de moneda", "Moneda cambiada a $nuevaMoneda.")
        )

        // Persistir cambio de moneda en Room
        CoroutineScope(Dispatchers.IO).launch {
            val email = activeAccount.obtenerUsuario().email
            WalletDbHelper.saveAccount(email, activeAccount.obtenerBalance(), activeAccount.obtenerTipoMoneda())
        }

        return true
    }

    suspend fun cambiarMonedaConApi(nuevaMoneda: String): Boolean {
        val activeAccount = cuentaActiva ?: return false
        val monedaActual = activeAccount.obtenerTipoMoneda()
        if (monedaActual.equals(nuevaMoneda, ignoreCase = true)) return false

        val saldoActual = activeAccount.obtenerBalance()

        return try {
            val response = RetrofitClient.apiService.getExchangeRates("CLP")
            val rates = response.rates

            val tasaActualAClp = if (monedaActual.equals("CLP", ignoreCase = true)) 1.0 else {
                val rate = rates[monedaActual] ?: 1.0
                if (rate != 0.0) 1.0 / rate else 1.0
            }
            val saldoEnCLP = saldoActual * tasaActualAClp

            val tasaClpADestino = if (nuevaMoneda.equals("CLP", ignoreCase = true)) 1.0 else {
                rates[nuevaMoneda] ?: 1.0
            }
            val nuevoSaldo = saldoEnCLP * tasaClpADestino

            activeAccount.balance = nuevoSaldo
            activeAccount.tipoMoneda = nuevaMoneda

            notifications.add(
                WalletNotification("Conversión de moneda", "Moneda cambiada a $nuevaMoneda vía API.")
            )

            // Persistir cambio de moneda en Room
            CoroutineScope(Dispatchers.IO).launch {
                val email = activeAccount.obtenerUsuario().email
                WalletDbHelper.saveAccount(email, activeAccount.obtenerBalance(), activeAccount.obtenerTipoMoneda())
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback a conversión local
            cambiarMoneda(nuevaMoneda)
        }
    }
}
