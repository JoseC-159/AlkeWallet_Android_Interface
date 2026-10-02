package com.mcu.alkewalletinterface.controller

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WalletControllerTest {

    private lateinit var walletController: WalletController

    @Before
    fun setUp() {
        walletController = WalletController()
        walletController.crearSesionDePrueba()
    }

    @Test
    fun testInitialBalance() {
        val balance = walletController.mostrarBalance()
        assertEquals(50000.0, balance, 0.001)
    }

    @Test
    fun testRecibirDinero() {
        val initialBalance = walletController.mostrarBalance()
        val success = walletController.recibirDinero(10000.0, "Bono")
        assertTrue(success)
        assertEquals(initialBalance + 10000.0, walletController.mostrarBalance(), 0.001)
    }

    @Test
    fun testCambiarMoneda() {
        val success = walletController.cambiarMoneda("USD")
        assertTrue(success)
        val cuentaActiva = WalletController.obtenerCuentaActiva()
        assertEquals("USD", cuentaActiva?.obtenerTipoMoneda())
    }
}
