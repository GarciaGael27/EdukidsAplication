package com.example.edukidsaplication

import android.app.Application
import android.util.Log
import com.example.edukidsaplication.database.EduKidsDatabase
import com.example.edukidsaplication.di.AppModule
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class EduKidsApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "EduKidsApplication"
    }

    override fun onCreate() {
        super.onCreate()

        try {
            // Inicializar Firebase
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase inicializado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar Firebase: ${e.message}")
        }

        // Inicializar la base de datos en un hilo secundario
        applicationScope.launch {
            try {
                // Inicializar la base de datos (esto también ejecutará la precarga)
                val database = EduKidsDatabase.getDatabase(applicationContext)
                Log.d(TAG, "Base de datos Room inicializada correctamente")

                // Inicializar los repositorios
                AppModule.provideUserRepository(applicationContext)
                AppModule.provideContentRepository(applicationContext)
                AppModule.provideUserPreferences(applicationContext)
                Log.d(TAG, "Repositorios inicializados correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error al inicializar la base de datos o repositorios: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
