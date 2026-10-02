package com.mcu.alkewalletinterface.view

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcu.alkewalletinterface.R
import com.mcu.alkewalletinterface.controller.TransactionAdapter
import com.mcu.alkewalletinterface.controller.WalletController
import com.mcu.alkewalletinterface.model.Cuenta
import com.squareup.picasso.Picasso
import com.mcu.alkewalletinterface.model.Transaccion
import com.mcu.alkewalletinterface.model.User
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.mcu.alkewalletinterface.data.local.WalletDbHelper

class HomeActivity : AppCompatActivity() {

    private var txtUserNameHome: TextView? = null
    private var txtBalanceAmount: TextView? = null
    private var layoutTransactionsList: ScrollView? = null
    private var layoutEmptyState: LinearLayout? = null
    private lateinit var walletController: WalletController
    private var rvTransactions: RecyclerView? = null
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        walletController = WalletController()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.setPadding(0, statusBarHeight, 0, navigationBarHeight)
            insets
        }

        txtUserNameHome = findViewById(R.id.txtUserNameHome)
        txtBalanceAmount = findViewById(R.id.txtBalanceAmount)
        layoutTransactionsList = findViewById(R.id.layoutTransactionsList)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        rvTransactions = findViewById(R.id.rvTransactions)

        val btnSend = findViewById<Button>(R.id.btnSendMoneyHome)
        val btnRequest = findViewById<Button>(R.id.btnRequestMoneyHome)
        val imgProfile = findViewById<ImageView>(R.id.imgProfileHome)
        val btnViewProfileHeader = findViewById<Button>(R.id.btnViewProfileHeader)

        if (imgProfile != null) {
            Picasso.get()
                .load(R.drawable.alkewallet_user_icon)
                .into(imgProfile)
        }

        rvTransactions?.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(emptyList())
        rvTransactions?.adapter = transactionAdapter

        val btnCampanita = findViewById<ImageView>(R.id.icNotification)
        val updateBellIcon = {
            if (WalletController.notifications.isNotEmpty()) {
                btnCampanita?.setImageResource(R.drawable.notifications_icon)
            } else {
                btnCampanita?.setImageResource(R.drawable.notifications_icon_off)
            }
        }
        updateBellIcon()

        btnCampanita?.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.layout_notification, null)
            val container = popupView.findViewById<LinearLayout>(R.id.containerNotifications)
            container?.removeAllViews()

            if (WalletController.notifications.isEmpty()) {
                val emptyTv = TextView(this).apply {
                    text = "No hay notificaciones"
                    textSize = 13f
                    setTextColor(Color.parseColor("#6B7280"))
                    setPadding(0, 8, 0, 8)
                }
                container?.addView(emptyTv)
            } else {
                for (notif in WalletController.notifications) {
                    val itemLayout = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(0, 6, 0, 6)
                    }
                    val titleTv = TextView(this).apply {
                        text = notif.title
                        textSize = 13f
                        setTypeface(null, Typeface.BOLD)
                        setTextColor(Color.parseColor("#111827"))
                    }
                    val msgTv = TextView(this).apply {
                        text = notif.message
                        textSize = 12f
                        setTextColor(Color.parseColor("#6B7280"))
                    }
                    itemLayout.addView(titleTv)
                    itemLayout.addView(msgTv)
                    container?.addView(itemLayout)

                    val divider = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1).apply {
                            setMargins(0, 6, 0, 6)
                        }
                        setBackgroundColor(Color.parseColor("#E5E7EB"))
                    }
                    container?.addView(divider)
                }
            }

            val widthInPx = (260 * resources.displayMetrics.density).toInt()
            val popupWindow = PopupWindow(
                popupView,
                widthInPx,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popupWindow.elevation = 12f

            val btnDismiss = popupView.findViewById<Button>(R.id.btnDismissNotifications)
            btnDismiss?.setOnClickListener {
                WalletController.notifications.clear()
                updateBellIcon()
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(btnCampanita, -200, 10)
        }

        btnSend?.setOnClickListener { startActivity(Intent(this, SendMoneyActivity::class.java)) }
        btnRequest?.setOnClickListener { startActivity(Intent(this, RequestMoneyActivity::class.java)) }

        val goToProfile = View.OnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        imgProfile?.setOnClickListener(goToProfile)
        btnViewProfileHeader?.setOnClickListener(goToProfile)

        lifecycleScope.launch {
            WalletDbHelper.restoreSessionIfNeeded()
            actualizarDatosUsuario()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            WalletDbHelper.restoreSessionIfNeeded()
            actualizarDatosUsuario()
        }
    }

    private fun actualizarDatosUsuario() {
        val usuario = walletController.obtenerUsuarioActivo()
        val cuenta = WalletController.obtenerCuentaActiva()

        val btnCampanita = findViewById<ImageView>(R.id.icNotification)
        if (WalletController.notifications.isNotEmpty()) {
            btnCampanita?.setImageResource(R.drawable.notifications_icon)
        } else {
            btnCampanita?.setImageResource(R.drawable.notifications_icon_off)
        }

        if (txtUserNameHome != null) {
            if (usuario != null) {
                txtUserNameHome?.text = "${usuario.obtenerNombre()}!"
            } else {
                txtUserNameHome?.text = "Invitado (Falta Login)"
            }
        }

        if (txtBalanceAmount != null) {
            val balance = walletController.mostrarBalance()
            val moneda = if (cuenta?.obtenerTipoMoneda() != null) cuenta.obtenerTipoMoneda() else "CLP"
            txtBalanceAmount?.text = String.format("$%.2f %s", balance, moneda)
        }

        if (cuenta != null) {
            val historial = cuenta.obtenerHistorial()
            if (!historial.isNullOrEmpty()) {
                layoutTransactionsList?.visibility = View.VISIBLE
                layoutEmptyState?.visibility = View.GONE
                transactionAdapter.setTransacciones(historial)
            } else {
                layoutTransactionsList?.visibility = View.GONE
                layoutEmptyState?.visibility = View.VISIBLE
            }
        } else {
            layoutTransactionsList?.visibility = View.GONE
            layoutEmptyState?.visibility = View.VISIBLE
        }
    }
}
