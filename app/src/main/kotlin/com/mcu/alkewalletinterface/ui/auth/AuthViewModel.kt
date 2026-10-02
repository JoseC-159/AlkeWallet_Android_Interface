package com.mcu.alkewalletinterface.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mcu.alkewalletinterface.data.AuthRepository
import com.mcu.alkewalletinterface.data.local.AppDatabase
import com.mcu.alkewalletinterface.data.local.WalletDbHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    init {
        WalletDbHelper.init(application)
    }

    private val repository: AuthRepository by lazy {
        val db = AppDatabase.getDatabase(application)
        AuthRepository(db.userDao(), db.accountDao(), db.transactionDao())
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Ingresa tu email y contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val success = repository.login(email, password)
            if (success) {
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error("Credenciales incorrectas")
            }
        }
    }

    fun register(name: String, email: String, password: String, age: Int?) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Completa todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.register(name, email, password, age)
            _uiState.value = AuthUiState.Success
        }
    }
}
