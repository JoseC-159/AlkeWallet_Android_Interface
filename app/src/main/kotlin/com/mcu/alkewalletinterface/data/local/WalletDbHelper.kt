package com.mcu.alkewalletinterface.data.local

import android.content.Context
import com.mcu.alkewalletinterface.controller.WalletController
import com.mcu.alkewalletinterface.model.Cuenta
import com.mcu.alkewalletinterface.model.Transaccion
import com.mcu.alkewalletinterface.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WalletDbHelper {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun saveLoggedInEmail(email: String) {
        appContext?.let {
            val prefs = it.getSharedPreferences("alke_wallet_session", Context.MODE_PRIVATE)
            prefs.edit().putString("logged_in_email", email).apply()
        }
    }

    fun getLoggedInEmail(): String? {
        return appContext?.let {
            val prefs = it.getSharedPreferences("alke_wallet_session", Context.MODE_PRIVATE)
            prefs.getString("logged_in_email", null)
        }
    }

    fun clearSession() {
        appContext?.let {
            val prefs = it.getSharedPreferences("alke_wallet_session", Context.MODE_PRIVATE)
            prefs.edit().remove("logged_in_email").apply()
        }
    }

    suspend fun restoreSessionIfNeeded(): Boolean {
        if (WalletController.usuarioActivo != null) return true
        val email = getLoggedInEmail() ?: return false
        return withContext(Dispatchers.IO) {
            appContext?.let { ctx ->
                val db = AppDatabase.getDatabase(ctx)
                val userEntity = db.userDao().getUserByEmail(email)
                if (userEntity != null) {
                    val user = User(
                        id = userEntity.id,
                        name = userEntity.name,
                        email = userEntity.email,
                        password = userEntity.password,
                        age = userEntity.age
                    )
                    WalletController.usuarioActivo = user

                    val accountEntity = db.accountDao().getAccountForUser(email)
                    val balance = accountEntity?.balance ?: 0.0
                    val tipoMoneda = accountEntity?.tipoMoneda ?: "CLP"

                    val cuenta = Cuenta(user, balance, tipoMoneda)

                    val txEntities = db.transactionDao().getTransactionsForUser(email)
                    for (tx in txEntities) {
                        val transaccion = Transaccion(tx.id, tx.tipo, tx.monto, tx.concepto, tx.nombreContraparte)
                        cuenta.agregarTransaccion(transaccion)
                    }

                    WalletController.cuentaActiva = cuenta
                    WalletController.cuentas.clear()
                    WalletController.cuentas.add(cuenta)
                    true
                } else {
                    false
                }
            } ?: false
        }
    }

    suspend fun saveAccount(email: String, balance: Double, tipoMoneda: String) {
        withContext(Dispatchers.IO) {
            appContext?.let {
                val db = AppDatabase.getDatabase(it)
                db.accountDao().insertOrUpdateAccount(AccountEntity(email, balance, tipoMoneda))
            }
        }
    }

    suspend fun saveTransaction(email: String, tipo: String, monto: Double, concepto: String, contraparte: String) {
        withContext(Dispatchers.IO) {
            appContext?.let {
                val db = AppDatabase.getDatabase(it)
                val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val tx = TransactionEntity(
                    userEmail = email,
                    tipo = tipo,
                    monto = monto,
                    concepto = concepto,
                    nombreContraparte = contraparte,
                    fecha = dateStr
                )
                db.transactionDao().insertTransaction(tx)
            }
        }
    }
}
