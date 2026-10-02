package com.mcu.alkewalletinterface.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mcu.alkewalletinterface.R
import com.mcu.alkewalletinterface.controller.WalletController

class RequestMoneyActivity : AppCompatActivity() {

    private lateinit var walletController: WalletController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_money)

        walletController = WalletController()

        val txtRequestUserName = findViewById<TextView>(R.id.txtRequestUserName)
        val txtRequestUserEmail = findViewById<TextView>(R.id.txtRequestUserEmail)
        val editRequestAmount = findViewById<EditText>(R.id.editRequestAmount)
        val editRequestNotes = findViewById<EditText>(R.id.editRequestNotes)
        val btnSubmitRequestMoney = findViewById<Button>(R.id.btnSubmitRequestMoney)
        val btnBackRequest = findViewById<TextView>(R.id.btnBackRequest)

        val usuarioActivo = walletController.obtenerUsuarioActivo()
        val cuentaActiva = WalletController.obtenerCuentaActiva()

        if (usuarioActivo != null) {
            txtRequestUserName.text = usuarioActivo.obtenerNombre()
            val emailGenerado = "${usuarioActivo.obtenerNombre().lowercase().replace(" ", ".")}@mail.com"
            txtRequestUserEmail.text = emailGenerado
        }

        val monedaActual = cuentaActiva?.obtenerTipoMoneda() ?: "CLP"
        editRequestAmount.hint = "$ 0.00 $monedaActual"

        btnBackRequest?.setOnClickListener { finish() }

        btnSubmitRequestMoney?.setOnClickListener {
            val amountStr = editRequestAmount.text?.toString()?.trim() ?: ""
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa un monto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val monto = amountStr.toDouble()
                val notas = editRequestNotes.text?.toString()?.trim() ?: ""

                if (walletController.recibirDinero(monto, notas)) {
                    Toast.makeText(this, "¡Ingreso exitoso! +$monto $monedaActual", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_LONG).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "El monto ingresado no es válido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
