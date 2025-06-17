package com.example.edukidsaplication.di

import android.content.Context
import com.example.edukidsaplication.database.EduKidsDatabase
import com.example.edukidsaplication.preferences.UserPreferences
import com.example.edukidsaplication.repository.ContentRepository
import com.example.edukidsaplication.repository.UserRepository

/**
 * Clase de proveedor de dependencias simplificada
 */
object AppModule {

    private var userRepository: UserRepository? = null
    private var contentRepository: ContentRepository? = null
    private var userPreferences: UserPreferences? = null

    fun provideUserRepository(context: Context): UserRepository {
        return userRepository ?: synchronized(this) {
            UserRepository(context.applicationContext).also {
                userRepository = it
            }
        }
    }

    fun provideContentRepository(context: Context): ContentRepository {
        return contentRepository ?: synchronized(this) {
            ContentRepository(context.applicationContext).also {
                contentRepository = it
            }
        }
    }

    fun provideUserPreferences(context: Context): UserPreferences {
        return userPreferences ?: synchronized(this) {
            UserPreferences(context.applicationContext).also {
                userPreferences = it
            }
        }
    }

    fun provideDatabase(context: Context): EduKidsDatabase {
        return EduKidsDatabase.getDatabase(context.applicationContext)
    }
}
