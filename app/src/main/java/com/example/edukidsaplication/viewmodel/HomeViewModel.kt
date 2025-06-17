package com.example.edukidsaplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edukidsaplication.di.AppModule
import com.example.edukidsaplication.model.User
import com.example.edukidsaplication.repository.CategoryWithProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "HomeViewModel"
    private val userRepository = AppModule.provideUserRepository(application.applicationContext)
    private val contentRepository = AppModule.provideContentRepository(application.applicationContext)

    var homeState by mutableStateOf(HomeState())
        private set

    private val _categories = MutableStateFlow<List<CategoryWithProgress>>(emptyList())
    val categories: StateFlow<List<CategoryWithProgress>> = _categories.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            homeState = homeState.copy(isLoading = true)

            try {
                val user = userRepository.getLoggedInUser()
                if (user != null) {
                    Log.d(TAG, "Usuario cargado correctamente: ${user.username}")
                    homeState = homeState.copy(
                        isLoading = false,
                        user = user,
                        error = null
                    )
                    // Cargamos las categorías solo si tenemos un usuario válido
                    loadCategories(user.userId)
                } else {
                    Log.e(TAG, "No se encontró ningún usuario con sesión iniciada")
                    homeState = homeState.copy(
                        isLoading = false,
                        error = "No se encontró ningún usuario con sesión iniciada"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar datos del usuario: ${e.message}")
                homeState = homeState.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar datos del usuario"
                )
            }
        }
    }

    private fun loadCategories(userId: String) {
        viewModelScope.launch {
            try {
                contentRepository.getCategoriesWithProgress(userId).collectLatest { categoriesList ->
                    _categories.value = categoriesList
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar categorías: ${e.message}")
            }
        }
    }

    fun signOut() {
        Log.d(TAG, "Cerrando sesión...")
        viewModelScope.launch {
            try {
                // Llamar al repositorio para cerrar sesión
                userRepository.signOut()

                // Reiniciar completamente el estado del ViewModel
                homeState = HomeState(isLoading = false, user = null, error = null)

                // Limpiar la lista de categorías
                _categories.value = emptyList()

                Log.d(TAG, "Sesión cerrada correctamente, todos los datos locales eliminados")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cerrar sesión: ${e.message}")
            }
        }
    }

    fun refreshData() {
        loadUserData()
    }
}

data class HomeState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
