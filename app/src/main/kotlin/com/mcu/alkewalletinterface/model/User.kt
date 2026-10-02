package com.mcu.alkewalletinterface.model

open class User(
    val id: Int,
    var name: String,
    val email: String,
    var password: String,
    var age: Int?,
) {
    open fun obtenerId(): Int = id
    open fun obtenerNombre(): String = name
    open fun obtenerEmail(): String = email
    open fun obtenerContraseña(): String = password
    open fun obtenerEdad(): Int = age ?: 0
}
