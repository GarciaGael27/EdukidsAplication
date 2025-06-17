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

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppModule.provideUserRepository(application.applicationContext)

    var registerState by mutableStateOf(RegisterState())
        private set

    fun onUsernameChange(username: String) {
        registerState = registerState.copy(username = username)
    }

    fun onNombreChange(nombre: String) {
        registerState = registerState.copy(nombre = nombre)
    }

    fun onApellidoChange(apellido: String) {
        registerState = registerState.copy(apellido = apellido)
    }

    fun register() {
        if (registerState.username.isBlank()) {
            registerState = registerState.copy(error = "El nombre de usuario no puede estar vacío")
            return
        }

        if (registerState.nombre.isBlank()) {
            registerState = registerState.copy(error = "El nombre no puede estar vacío")
            return
        }

        if (registerState.apellido.isBlank()) {
            registerState = registerState.copy(error = "El apellido no puede estar vacío")
            return
        }

        registerState = registerState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.registerUser(
                registerState.username,
                registerState.nombre,
                registerState.apellido
            )

            result.fold(
                onSuccess = { user ->
                    registerState = registerState.copy(
                        isLoading = false,
                        isRegistered = true,
                        user = user,
                        error = null
                    )
                },
                onFailure = { exception ->
                    registerState = registerState.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al registrar usuario"
                    )
                }
            )
        }
    }
}

data class RegisterState(
    val username: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
