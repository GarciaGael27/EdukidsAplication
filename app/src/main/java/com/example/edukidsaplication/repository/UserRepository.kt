package com.example.edukidsaplication.repository

import android.content.Context
import android.util.Log
import com.example.edukidsaplication.database.EduKidsDatabase
import com.example.edukidsaplication.database.UserEntity
import com.example.edukidsaplication.model.User
import com.example.edukidsaplication.preferences.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository(private val context: Context) {
    private val TAG = "UserRepository"
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    private val database = EduKidsDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val userPreferences = UserPreferences(context)

    // Variable para mantener el usuario actual en memoria
    private var currentUser: User? = null

    // Registrar un nuevo usuario
    suspend fun registerUser(username: String, nombre: String, apellido: String): Result<User> {
        return try {
            // Verificar si el usuario existe localmente
            val localUser = userDao.getUserByUsername(username)
            if (localUser != null) {
                return Result.failure(Exception("El nombre de usuario ya está en uso"))
            }

            // Verificar si el usuario existe en Firestore si hay conexión
            if (isNetworkAvailable()) {
                val existingUser = usersCollection.whereEqualTo("username", username).get().await()
                if (!existingUser.isEmpty) {
                    return Result.failure(Exception("El nombre de usuario ya está en uso"))
                }
            }

            // Generar un nuevo ID de usuario único
            val userId = UUID.randomUUID().toString()

            // Crear usuario con autenticación anónima en Firebase Auth si hay conexión
            if (isNetworkAvailable()) {
                try {
                    val authResult = suspendCoroutine<FirebaseUser> { continuation ->
                        auth.signInAnonymously()
                            .addOnSuccessListener { result ->
                                continuation.resume(result.user!!)
                            }
                            .addOnFailureListener { e ->
                                continuation.resumeWithException(e)
                            }
                    }

                    // Crear documento del usuario en Firestore
                    val user = User(authResult.uid, username, nombre, apellido)
                    usersCollection.add(user).await()
                } catch (e: Exception) {
                    // Si falla la autenticación online, continuamos con el registro local
                    // pero no interrumpimos el proceso
                }
            }

            // Guardar usuario en la base de datos local
            val userEntity = UserEntity(
                userId = userId,
                username = username,
                nombre = nombre,
                apellido = apellido,
                lastLogin = System.currentTimeMillis()
            )
            userDao.insertUser(userEntity)

            // Guardar información de sesión en SharedPreferences
            userPreferences.saveLoggedInUserId(userId)

            Result.success(User(userId, username, nombre, apellido))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Iniciar sesión
    suspend fun loginUser(username: String): Result<User> {
        return try {
            // Primero intentamos obtener el usuario desde la base de datos local
            val localUser = userDao.getUserByUsername(username)

            if (localUser != null) {
                Log.d(TAG, "Usuario encontrado localmente: ${localUser.username}")
                // Actualizar último inicio de sesión
                userDao.updateLastLogin(localUser.userId)
                userPreferences.saveLoggedInUserId(localUser.userId)

                val user = User(localUser.userId, localUser.username, localUser.nombre, localUser.apellido)
                // Guardar el usuario actual en memoria para uso inmediato
                currentUser = user

                return Result.success(user)
            }

            // Si no encontramos el usuario localmente pero hay conexión, intentamos buscarlo en Firestore
            if (isNetworkAvailable()) {
                val querySnapshot = usersCollection.whereEqualTo("username", username).get().await()

                if (!querySnapshot.isEmpty) {
                    val userDoc = querySnapshot.documents.first()
                    val user = userDoc.toObject(User::class.java) ?:
                        return Result.failure(Exception("Error al obtener datos del usuario"))

                    // Guardar o actualizar el usuario en la base de datos local
                    val userEntity = UserEntity(
                        userId = user.userId,
                        username = user.username,
                        nombre = user.nombre,
                        apellido = user.apellido,
                        lastLogin = System.currentTimeMillis()
                    )
                    userDao.insertUser(userEntity)

                    // Guardar información de sesión en SharedPreferences
                    userPreferences.saveLoggedInUserId(user.userId)

                    // Guardar el usuario actual en memoria para uso inmediato
                    currentUser = user

                    return Result.success(user)
                }
            }

            Result.failure(Exception("Usuario no encontrado"))
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar sesión: ${e.message}")
            Result.failure(e)
        }
    }

    // Obtener el usuario que ha iniciado sesión
    suspend fun getLoggedInUser(): User? {
        // Si tenemos el usuario en memoria, lo devolvemos directamente
        if (currentUser != null) {
            return currentUser
        }

        val userId = userPreferences.getLoggedInUserId()
        if (userId == null) {
            Log.d(TAG, "No hay ID de usuario almacenado en preferencias")
            return null
        }

        val user = userDao.getUserById(userId)
        if (user == null) {
            Log.d(TAG, "Usuario con ID $userId no encontrado en la base de datos")
            return null
        }

        val userModel = User(
            userId = user.userId,
            username = user.username,
            nombre = user.nombre,
            apellido = user.apellido
        )

        // Guardamos el usuario en memoria para futuras consultas
        currentUser = userModel

        return userModel
    }

    // Verificar si hay un usuario con sesión iniciada
    fun hasLoggedInUser(): Boolean {
        return currentUser != null || userPreferences.hasLoggedInUser()
    }

    // Obtener todos los usuarios locales
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { entity ->
                User(
                    userId = entity.userId,
                    username = entity.username,
                    nombre = entity.nombre,
                    apellido = entity.apellido
                )
            }
        }
    }

    // Cerrar sesión
    fun signOut() {
        if (isNetworkAvailable()) {
            auth.signOut()
        }
        currentUser = null
        userPreferences.clearLoggedInUser()
    }

    // Función para comprobar si hay conexión a internet
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            return actNw.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }
}
