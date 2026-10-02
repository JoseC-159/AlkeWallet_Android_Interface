package com.mcu.alkewalletinterface.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mcu.alkewalletinterface.R
import com.mcu.alkewalletinterface.controller.WalletController
import com.mcu.alkewalletinterface.model.Cuenta
import com.squareup.picasso.Picasso
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private var btnBackProfile: TextView? = null
    private var txtProfileName: TextView? = null
    private var btnLogoutProfile: Button? = null
    private var spinnerCurrency: Spinner? = null

    private lateinit var walletController: WalletController
    private var cuentaActiva: Cuenta? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            v.setPadding(0, statusBarHeight, 0, navigationBarHeight)
            insets
        }

        walletController = WalletController()
        cuentaActiva = WalletController.obtenerCuentaActiva()

        btnBackProfile = findViewById(R.id.btnBackProfile)
        txtProfileName = findViewById(R.id.txtProfileName)
        btnLogoutProfile = findViewById(R.id.btnLogoutProfile)
        spinnerCurrency = findViewById(R.id.spinnerCurrency)

        val imgProfileLarge = findViewById<ImageView>(R.id.imgProfileLarge)
        if (imgProfileLarge != null) {
            Picasso.get()
                .load(R.drawable.alkewallet_user_icon)
                .placeholder(R.drawable.alkewallet_user_icon)
                .error(R.drawable.alkewallet_user_icon)
                .into(imgProfileLarge)
        }

        val usuarioActivo = walletController.obtenerUsuarioActivo()
        if (usuarioActivo != null && txtProfileName != null) {
            txtProfileName?.text = usuarioActivo.obtenerNombre()
        }

        val txtInfoFullName = findViewById<TextView>(R.id.txtInfoFullName)
        val txtInfoEmail = findViewById<TextView>(R.id.txtInfoEmail)

        txtInfoFullName.text = "Nombre: ${usuarioActivo?.obtenerNombre() ?: ""}"
        txtInfoEmail?.text = "Email: ${usuarioActivo?.obtenerEmail() ?: ""}"

        configurarSpinnerMoneda()
        configurarAcordeones()

        btnBackProfile?.setOnClickListener { finish() }

        btnLogoutProfile?.setOnClickListener {
            walletController.cerrarSesion()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginSignupActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun configurarAcordeones() {
        val btnMyInfo = findViewById<LinearLayout>(R.id.btnMyInfo)
        val layoutMyInfoDetails = findViewById<LinearLayout>(R.id.layoutMyInfoDetails)
        val arrowMyInfo = findViewById<TextView>(R.id.arrowMyInfo)
        btnMyInfo.setOnClickListener { toggleSeccion(layoutMyInfoDetails, arrowMyInfo) }

        val btnMyCards = findViewById<LinearLayout>(R.id.btnMyCards)
        val layoutMyCardsDetails = findViewById<LinearLayout>(R.id.layoutMyCardsDetails)
        val arrowMyCards = findViewById<TextView>(R.id.arrowMyCards)
        btnMyCards.setOnClickListener { toggleSeccion(layoutMyCardsDetails, arrowMyCards) }

        val btnHelpCenter = findViewById<LinearLayout>(R.id.btnHelpCenter)
        val layoutHelpCenterDetails = findViewById<LinearLayout>(R.id.layoutHelpCenterDetails)
        val arrowHelpCenter = findViewById<TextView>(R.id.arrowHelpCenter)
        btnHelpCenter.setOnClickListener { toggleSeccion(layoutHelpCenterDetails, arrowHelpCenter) }
    }

    private fun toggleSeccion(detalle: View, flecha: TextView) {
        if (detalle.visibility == View.VISIBLE) {
            detalle.visibility = View.GONE
            flecha.text = "▼"
        } else {
            detalle.visibility = View.VISIBLE
            flecha.text = "▲"
        }
    }

    private fun configurarSpinnerMoneda() {
        val account = cuentaActiva ?: return
        val spinner = spinnerCurrency ?: return

        val monedas = arrayOf("USD", "EUR", "CLP")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monedas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val monedaActual = account.obtenerTipoMoneda()
        for (i in monedas.indices) {
            if (monedas[i] == monedaActual) {
                spinner.setSelection(i)
                break
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val monedaSeleccionada = monedas[position]
                if (monedaSeleccionada != account.obtenerTipoMoneda()) {
                    lifecycleScope.launch {
                        val exito = walletController.cambiarMonedaConApi(monedaSeleccionada)
                        if (exito) {
                            Toast.makeText(this@ProfileActivity, "Moneda cambiada a $monedaSeleccionada (En línea)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
