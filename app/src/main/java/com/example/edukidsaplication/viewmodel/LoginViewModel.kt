package com.example.edukidsaplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edukidsaplication.di.AppModule
import com.example.edukidsaplication.model.User
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppModule.provideUserRepository(application.applicationContext)
    private val preferences = AppModule.provideUserPreferences(application.applicationContext)

    var loginState by mutableStateOf(LoginState())
        private set

    init {
        // Verificar si hay un usuario que haya iniciado sesión previamente
        viewModelScope.launch {
            val lastUser = repository.getLoggedInUser()
            if (lastUser != null) {
                loginState = loginState.copy(
                    username = lastUser.username,
                    isLoggedIn = true,
                    user = lastUser
                )
            }
        }
    }

    fun onUsernameChange(username: String) {
        loginState = loginState.copy(username = username)
    }

    fun login() {
        if (loginState.username.isBlank()) {
            loginState = loginState.copy(error = "El nombre de usuario no puede estar vacío")
            return
        }

        loginState = loginState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.loginUser(loginState.username)

            result.fold(
                onSuccess = { user ->
                    loginState = loginState.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        error = null
                    )
                },
                onFailure = { exception ->
                    loginState = loginState.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al iniciar sesión"
                    )
                }
            )
        }
    }

    // Método para reiniciar el estado cuando se cierra sesión
    fun resetLoginState() {
        loginState = LoginState(
            username = "",
            isLoading = false,
            isLoggedIn = false,
            user = null,
            error = null
        )
    }
}

data class LoginState(
    val username: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
