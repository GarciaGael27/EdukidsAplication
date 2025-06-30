package com.example.edukidsaplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edukidsaplication.database.CategoryEntity
import com.example.edukidsaplication.database.LessonContentEntity
import com.example.edukidsaplication.di.AppModule
import com.example.edukidsaplication.repository.LessonWithProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

// Clase para representar un problema matemático individual
data class Problem(
    val num1: Int,
    val num2: Int,
    val answer: Int,
    val type: String,
    val question: String = ""
)

class LessonsViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "LessonsViewModel"
    private val contentRepository = AppModule.provideContentRepository(application.applicationContext)
    private val userRepository = AppModule.provideUserRepository(application.applicationContext)

    private val _lessonsWithProgress = MutableStateFlow<List<LessonWithProgress>>(emptyList())
    val lessonsWithProgress: StateFlow<List<LessonWithProgress>> = _lessonsWithProgress.asStateFlow()

    private val _selectedCategory = MutableStateFlow<CategoryEntity?>(null)
    val selectedCategory: StateFlow<CategoryEntity?> = _selectedCategory.asStateFlow()

    // Estado para gestionar el contenido de lección (que contiene múltiples problemas)
    private val _lessonContent = MutableStateFlow<LessonContentEntity?>(null)
    val lessonContent: StateFlow<LessonContentEntity?> = _lessonContent.asStateFlow()

    // Lista de problemas extraídos del contenido
    private val _problems = MutableStateFlow<List<Problem>>(emptyList())
    val problems: StateFlow<List<Problem>> = _problems.asStateFlow()

    // Estado para el problema actual
    private val _currentProblemIndex = MutableStateFlow(0)
    val currentProblemIndex: StateFlow<Int> = _currentProblemIndex.asStateFlow()

    // Estado para indicar si la lección se ha completado
    private val _isLessonCompleted = MutableStateFlow(false)
    val isLessonCompleted: StateFlow<Boolean> = _isLessonCompleted.asStateFlow()

    // Estado para forzar el refresh de las lecciones
    private val _refreshTrigger = MutableStateFlow(0)

    // Estado para la puntuación (ejercicios correctos)
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    // Estado para los problemas completados
    private val _completedProblems = MutableStateFlow(0)
    val completedProblems: StateFlow<Int> = _completedProblems.asStateFlow()

    // Estado para la carga de ejercicios
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Instrucciones para el ejercicio actual
    private val _instructions = MutableStateFlow("")
    val instructions: StateFlow<String> = _instructions.asStateFlow()

    // Tipo de ejercicio matemático
    private val _exerciseType = MutableStateFlow("")
    val exerciseType: StateFlow<String> = _exerciseType.asStateFlow()

    fun loadLessonsForCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                // Cargar los datos de la categoría
                val category = contentRepository.getCategoryById(categoryId)
                _selectedCategory.value = category

                // Obtener el usuario actual
                val user = userRepository.getLoggedInUser()
                if (user != null) {
                    // Cargar las lecciones con su progreso, combinado con el refreshTrigger
                    _refreshTrigger.combine(
                        contentRepository.getLessonsWithProgress(user.userId, categoryId)
                    ) { _, lessons ->
                        lessons
                    }
                        .catch { e ->
                            Log.e(TAG, "Error al cargar lecciones: ${e.message}")
                            _lessonsWithProgress.value = emptyList()
                        }
                        .collectLatest { lessons ->
                            _lessonsWithProgress.value = lessons
                            Log.d(TAG, "Lecciones de matemáticas actualizadas: ${lessons.size} lecciones cargadas")
                        }
                } else {
                    Log.e(TAG, "No hay usuario con sesión iniciada")
                    _lessonsWithProgress.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar categoría o lecciones: ${e.message}")
                _lessonsWithProgress.value = emptyList()
            }
        }
    }

    fun loadExercisesForLesson(lessonId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // Resetear TODOS los estados inmediatamente y de forma síncrona
            _currentProblemIndex.value = 0
            _score.value = 0
            _completedProblems.value = 0
            _problems.value = emptyList()
            _lessonContent.value = null
            _instructions.value = ""
            _exerciseType.value = ""
            _isLessonCompleted.value = false

            try {
                // Obtener el usuario actual
                val user = userRepository.getLoggedInUser()
                if (user != null) {
                    // Cargar los contenidos de la lección (permitimos repetir lecciones)
                    contentRepository.getLessonContents(lessonId)
                        .catch { e ->
                            Log.e(TAG, "Error al cargar ejercicios: ${e.message}")
                            _lessonContent.value = null
                            _isLoading.value = false
                        }
                        .collectLatest { contents ->
                            if (contents.isNotEmpty()) {
                                // Por ahora tomamos el primer contenido (podríamos implementar navegación entre contenidos después)
                                val content = contents.first()
                                _lessonContent.value = content

                                // Parsear el contenido para extraer los problemas matemáticos
                                parseProblems(content)
                            } else {
                                _lessonContent.value = null
                                _problems.value = emptyList()
                            }
                            _isLoading.value = false
                        }
                } else {
                    Log.e(TAG, "No hay usuario con sesión iniciada")
                    _lessonContent.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar ejercicios: ${e.message}")
                _lessonContent.value = null
                _isLoading.value = false
            }
        }
    }

    private fun parseProblems(content: LessonContentEntity) {
        try {
            Log.d(TAG, "Iniciando parseProblems para contenido: ${content.contentId}")
            Log.d(TAG, "Contenido JSON: ${content.content}")

            val jsonContent = JSONObject(content.content)

            // Extraer tipo de ejercicio e instrucciones (solo matemáticas)
            _exerciseType.value = jsonContent.optString("type", "")
            _instructions.value = jsonContent.optString("instructions", "")

            Log.d(TAG, "Tipo de ejercicio: ${_exerciseType.value}")
            Log.d(TAG, "Instrucciones: ${_instructions.value}")

            // Extraer los problemas matemáticos
            val problemsArray = jsonContent.getJSONArray("problems")
            val problemsList = mutableListOf<Problem>()

            Log.d(TAG, "Número de problemas encontrados: ${problemsArray.length()}")

            for (i in 0 until problemsArray.length()) {
                val problemJson = problemsArray.getJSONObject(i)
                val num1 = problemJson.optInt("num1", 0)
                val num2 = problemJson.optInt("num2", 0)
                val answer = problemJson.optInt("answer", 0)

                Log.d(TAG, "Problema $i: num1=$num1, num2=$num2, answer=$answer")

                // Crear una pregunta basada en el tipo de ejercicio matemático
                val question = when (_exerciseType.value) {
                    "simple_addition" -> "¿Cuánto es $num1 + $num2?"
                    "simple_subtraction" -> "¿Cuánto es $num1 - $num2?"
                    "simple_multiplication" -> "¿Cuánto es $num1 × $num2?"
                    "simple_division" -> "¿Cuánto es $num1 ÷ $num2?"
                    "multiplication_table" -> "¿Cuánto es $num1 × $num2?"
                    else -> "¿Cuánto es $num1 + $num2?" // Por defecto suma
                }

                problemsList.add(
                    Problem(
                        num1 = num1,
                        num2 = num2,
                        answer = answer,
                        type = _exerciseType.value,
                        question = question
                    )
                )
            }

            _problems.value = problemsList
            Log.d(TAG, "Problemas matemáticos cargados exitosamente: ${problemsList.size} problemas")
            Log.d(TAG, "Lista de problemas: $problemsList")

        } catch (e: Exception) {
            Log.e(TAG, "Error al parsear problemas matemáticos: ${e.message}")
            Log.e(TAG, "Stack trace: ", e)
            _problems.value = emptyList()
        }
    }

    fun submitAnswer(answer: String): Boolean {
        val problems = _problems.value
        val currentIndex = _currentProblemIndex.value

        if (problems.isEmpty() || currentIndex >= problems.size) {
            return false
        }

        val currentProblem = problems[currentIndex]

        // Convertir la respuesta a entero para comparar (solo matemáticas)
        val isCorrect = try {
            answer.trim().toInt() == currentProblem.answer
        } catch (e: Exception) {
            false
        }

        if (isCorrect) {
            _score.value = _score.value + 1
            Log.d(TAG, "Respuesta correcta! Score actual: ${_score.value}")
        }

        _completedProblems.value = _completedProblems.value + 1
        Log.d(TAG, "Problemas completados: ${_completedProblems.value}/${problems.size}")

        // NO avanzamos automáticamente al siguiente problema
        // El avance se hará cuando se presione "Continuar"

        return isCorrect
    }

    fun nextProblem() {
        val problems = _problems.value
        val currentIndex = _currentProblemIndex.value

        Log.d(TAG, "nextProblem() llamado - Índice actual: $currentIndex, Problemas completados: ${_completedProblems.value}, Total problemas: ${problems.size}")

        // Avanzar al siguiente problema si hay más
        if (currentIndex < problems.size - 1) {
            _currentProblemIndex.value = currentIndex + 1
            Log.d(TAG, "Avanzando al problema ${currentIndex + 1}")
        } else {
            // Si hemos terminado todos los problemas, guardar el progreso
            Log.d(TAG, "Todos los problemas completados, guardando progreso")
            saveProgress()
        }
    }

    private fun saveProgress() {
        viewModelScope.launch {
            try {
                val user = userRepository.getLoggedInUser() ?: return@launch
                val problems = _problems.value
                val content = _lessonContent.value
                val currentScore = _score.value

                Log.d(TAG, "Guardando progreso - problemas: ${problems.size}, respuestas correctas: $currentScore")

                if (problems.isEmpty() || content == null) {
                    Log.e(TAG, "No se puede guardar progreso: problemas vacíos o contenido nulo")
                    return@launch
                }

                // Calculamos el porcentaje de respuestas correctas de esta sesión
                val newScore = (currentScore * 100) / problems.size
                Log.d(TAG, "Nuevo score calculado: $newScore% (${currentScore}/${problems.size})")

                // Obtener el progreso actual de la lección (si existe)
                val currentProgress = contentRepository.getLessonProgress(user.userId, content.lessonId)
                val existingScore = currentProgress?.score ?: 0
                Log.d(TAG, "Score existente: $existingScore%")

                // Solo actualizar si la nueva puntuación es mayor que la actual
                if (newScore > existingScore) {
                    contentRepository.updateLessonProgress(
                        userId = user.userId,
                        lessonId = content.lessonId,
                        score = newScore,
                        isCompleted = true
                    )
                    Log.d(TAG, "Progreso actualizado: lessonId=${content.lessonId}, score anterior=$existingScore%, nueva score=$newScore%")
                } else {
                    // Marcar como completada pero mantener la puntuación anterior
                    contentRepository.updateLessonProgress(
                        userId = user.userId,
                        lessonId = content.lessonId,
                        score = existingScore,
                        isCompleted = true
                    )
                    Log.d(TAG, "Lección completada pero puntuación no mejorada: lessonId=${content.lessonId}, score actual=$existingScore%, nueva score=$newScore%")
                }

                // Forzar actualización del progreso de lecciones si estamos en una categoría
                _selectedCategory.value?.let { category ->
                    refreshLessonsProgress(user.userId, category.categoryId)
                    // Activar el trigger para forzar actualización del Flow
                    _refreshTrigger.value = _refreshTrigger.value + 1
                    Log.d(TAG, "RefreshTrigger activado: ${_refreshTrigger.value}")
                }

                // Marcar la lección como completada
                _isLessonCompleted.value = true
                Log.d(TAG, "Lección de matemáticas marcada como completada")
            } catch (e: Exception) {
                Log.e(TAG, "Error al guardar progreso: ${e.message}")
            }
        }
    }

    // Método público para refrescar las lecciones desde cualquier pantalla
    fun refreshLessons() {
        viewModelScope.launch {
            try {
                val user = userRepository.getLoggedInUser() ?: return@launch
                val categoryId = _selectedCategory.value?.categoryId ?: return@launch

                Log.d(TAG, "Refrescando lecciones de matemáticas para categoría: $categoryId")
                refreshLessonsProgress(user.userId, categoryId)

                // Incrementar el trigger para forzar actualización del flujo de datos
                _refreshTrigger.value = _refreshTrigger.value + 1
                Log.d(TAG, "RefreshTrigger activado manualmente: ${_refreshTrigger.value}")
            } catch (e: Exception) {
                Log.e(TAG, "Error al refrescar lecciones: ${e.message}")
            }
        }
    }

    private suspend fun refreshLessonsProgress(userId: String, categoryId: String) {
        try {
            Log.d(TAG, "Refrescando progreso de lecciones de matemáticas para categoría: $categoryId")
            // Simplemente forzar la recarga obteniendo los datos una sola vez
            val lessons = contentRepository.getLessonsWithProgress(userId, categoryId).first()
            _lessonsWithProgress.value = lessons
            Log.d(TAG, "Progreso de lecciones de matemáticas actualizado: ${lessons.size} lecciones")
        } catch (e: Exception) {
            Log.e(TAG, "Error al refrescar progreso de lecciones: ${e.message}")
        }
    }

    fun getCurrentProblem(): Problem? {
        val problems = _problems.value
        val currentIndex = _currentProblemIndex.value

        if (problems.isEmpty() || currentIndex >= problems.size) {
            return null
        }

        return problems[currentIndex]
    }

    fun resetExerciseSession() {
        _currentProblemIndex.value = 0
        _score.value = 0
        _completedProblems.value = 0
        _isLessonCompleted.value = false
        Log.d(TAG, "Sesión de ejercicios de matemáticas reseteada")
    }

    fun hasMoreProblems(): Boolean {
        return _completedProblems.value < _problems.value.size
    }

    fun getTotalProblems(): Int {
        return _problems.value.size
    }

    // Método para obtener el porcentaje de progreso basado en respuestas correctas
    fun getProgressPercentage(): Float {
        val totalProblems = _problems.value.size
        val correctAnswers = _score.value
        return if (totalProblems > 0) {
            (correctAnswers.toFloat() / totalProblems.toFloat())
        } else {
            0f
        }
    }
}
