package com.mcu.alkewalletinterface.data

import com.mcu.alkewalletinterface.controller.WalletController
import com.mcu.alkewalletinterface.controller.WalletNotification
import com.mcu.alkewalletinterface.data.local.AccountDao
import com.mcu.alkewalletinterface.data.local.AccountEntity
import com.mcu.alkewalletinterface.data.local.TransactionDao
import com.mcu.alkewalletinterface.data.local.UserDao
import com.mcu.alkewalletinterface.data.local.UserEntity
import com.mcu.alkewalletinterface.data.local.WalletDbHelper
import com.mcu.alkewalletinterface.model.Cuenta
import com.mcu.alkewalletinterface.model.Transaccion
import com.mcu.alkewalletinterface.model.User

class AuthRepository(
    private val userDao: UserDao,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao
) {

    var currentUser: User? = null
        private set

    suspend fun login(email: String, password: String): Boolean {
        val userEntity = userDao.login(email, password)
        if (userEntity != null) {
            val user = User(
                id = userEntity.id,
                name = userEntity.name,
                email = userEntity.email,
                password = userEntity.password,
                age = userEntity.age
            )
            currentUser = user
            WalletController.usuarioActivo = user

            // Cargar cuenta persistida de Room o crear por defecto
            val accountEntity = accountDao.getAccountForUser(user.email)
            val balance = accountEntity?.balance ?: 0.0
            val tipoMoneda = accountEntity?.tipoMoneda ?: "CLP"

            val cuenta = Cuenta(user, balance, tipoMoneda)

            // Cargar transacciones persistidas de Room
            val txEntities = transactionDao.getTransactionsForUser(user.email)
            for (tx in txEntities) {
                val transaccion = Transaccion(tx.id, tx.tipo, tx.monto, tx.concepto, tx.nombreContraparte)
                cuenta.agregarTransaccion(transaccion)
            }

            WalletController.cuentaActiva = cuenta
            WalletController.cuentas.clear()
            WalletController.cuentas.add(cuenta)

            WalletDbHelper.saveLoggedInEmail(user.email)

            return true
        }
        return false
    }

    suspend fun register(name: String, email: String, password: String, age: Int?): User {
        val userEntity = UserEntity(
            name = name,
            email = email,
            password = password,
            age = age ?: 0
        )
        userDao.insertUser(userEntity)
        val inserted = userDao.getUserByEmail(email)
        val newUser = User(
            id = inserted?.id ?: 0,
            name = name,
            email = email,
            password = password,
            age = age
        )
        currentUser = newUser

        WalletController.usuarioActivo = newUser
        val newCuenta = Cuenta(newUser, 0.0, "CLP")

        // Persistir cuenta inicial en Room
        accountDao.insertOrUpdateAccount(AccountEntity(email, 0.0, "CLP"))

        WalletController.cuentas.clear()
        WalletController.cuentas.add(newCuenta)
        WalletController.cuentaActiva = newCuenta

        WalletController.notifications.clear()
        WalletController.notifications.add(
            WalletNotification("¡Bienvenido a AlkeWallet!", "Tu cuenta ha sido creada con éxito.")
        )

        WalletDbHelper.saveLoggedInEmail(email)

        return newUser
    }
}
