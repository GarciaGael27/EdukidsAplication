package com.example.edukidsaplication.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Guarda el ID del usuario que ha iniciado sesión
    fun saveLoggedInUserId(userId: String) {
        sharedPreferences.edit {
            putString(KEY_LOGGED_IN_USER_ID, userId)
            putLong(KEY_LAST_LOGIN_TIME, System.currentTimeMillis())
        }
    }

    // Obtiene el ID del usuario que ha iniciado sesión
    fun getLoggedInUserId(): String? {
        return sharedPreferences.getString(KEY_LOGGED_IN_USER_ID, null)
    }

    // Verifica si hay un usuario con sesión iniciada
    fun hasLoggedInUser(): Boolean {
        return sharedPreferences.contains(KEY_LOGGED_IN_USER_ID)
    }

    // Cierra la sesión del usuario actual
    fun clearLoggedInUser() {
        sharedPreferences.edit {
            remove(KEY_LOGGED_IN_USER_ID)
        }
    }

    // Guarda el tema de la aplicación
    fun saveAppTheme(isDarkMode: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_DARK_MODE, isDarkMode)
        }
    }

    // Obtiene el tema de la aplicación
    fun getAppTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    // Guarda el nivel de sonido
    fun saveSoundLevel(level: Int) {
        sharedPreferences.edit {
            putInt(KEY_SOUND_LEVEL, level)
        }
    }

    // Obtiene el nivel de sonido
    fun getSoundLevel(): Int {
        return sharedPreferences.getInt(KEY_SOUND_LEVEL, 100)
    }

    // Guarda si las notificaciones están habilitadas
    fun saveNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        }
    }

    // Obtiene si las notificaciones están habilitadas
    fun getNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    companion object {
        private const val PREF_NAME = "edukids_preferences"
        private const val KEY_LOGGED_IN_USER_ID = "logged_in_user_id"
        private const val KEY_LAST_LOGIN_TIME = "last_login_time"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_SOUND_LEVEL = "sound_level"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }
}
