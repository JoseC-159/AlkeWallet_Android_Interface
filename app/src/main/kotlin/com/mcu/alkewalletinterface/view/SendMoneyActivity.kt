package com.mcu.alkewalletinterface.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mcu.alkewalletinterface.R
import com.mcu.alkewalletinterface.controller.WalletController
import com.mcu.alkewalletinterface.model.Cuenta

class SendMoneyActivity : AppCompatActivity() {

    private var etAmount: EditText? = null
    private var editSendNotes: EditText? = null
    private var spinnerDestinatario: Spinner? = null
    private var btnSendMoney: Button? = null

    private lateinit var walletController: WalletController
    private var cuentasDisponibles: List<Cuenta>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_money)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.setPadding(0, statusBarHeight, 0, navigationBarHeight)
            insets
        }

        walletController = WalletController()

        val btnBackSend = findViewById<TextView>(R.id.btnBackSend)
        spinnerDestinatario = findViewById(R.id.spinnerDestinatario)
        etAmount = findViewById(R.id.etAmount)
        val txtAvailableBalance = findViewById<TextView>(R.id.txtAvailableBalance)
        editSendNotes = findViewById(R.id.editSendNotes)
        btnSendMoney = findViewById(R.id.btnSendMoney)

        btnBackSend?.setOnClickListener { finish() }
        btnSendMoney?.setOnClickListener { procesarEnvio() }

        val cuentaActiva = WalletController.obtenerCuentaActiva()
        if (cuentaActiva != null) {
            txtAvailableBalance.text = String.format("Saldo disponible: $%.2f %s",
                cuentaActiva.obtenerBalance(), cuentaActiva.obtenerTipoMoneda())
            etAmount?.hint = "$ 0.00 ${cuentaActiva.obtenerTipoMoneda()}"
        }

        cuentasDisponibles = WalletController.obtenerCuentasDisponiblesParaEnvio()
        val nombresDestinatarios = mutableListOf<String>()

        if (cuentasDisponibles.isNullOrEmpty()) {
            nombresDestinatarios.add("No hay otros usuarios disponibles")
            btnSendMoney?.isEnabled = false
        } else {
            for (c in cuentasDisponibles!!) {
                nombresDestinatarios.add(c.obtenerUsuario().obtenerNombre())
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresDestinatarios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDestinatario?.adapter = adapter
    }

    private fun procesarEnvio() {
        val amountStr = etAmount?.text?.toString()?.trim() ?: ""
        val notas = if (!editSendNotes?.text.isNullOrEmpty()) editSendNotes?.text.toString().trim() else "Transferencia"

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Ingresa un monto", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val monto = amountStr.toDouble()

            if (cuentasDisponibles.isNullOrEmpty()) {
                Toast.makeText(this, "No hay destinatarios disponibles", Toast.LENGTH_SHORT).show()
                return
            }

            val selectedIndex = spinnerDestinatario?.selectedItemPosition ?: 0
            val cuentaDestino = cuentasDisponibles!![selectedIndex]

            val exito = walletController.enviarDinero(monto, cuentaDestino, notas)

            if (exito) {
                Toast.makeText(this, "Envío exitoso a ${cuentaDestino.obtenerUsuario().obtenerNombre()}", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Fondos insuficientes o error en la operación", Toast.LENGTH_LONG).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
        }
    }
}
