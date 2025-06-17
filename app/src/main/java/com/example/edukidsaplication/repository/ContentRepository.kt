package com.example.edukidsaplication.repository

import android.content.Context
import com.example.edukidsaplication.database.CategoryEntity
import com.example.edukidsaplication.database.CategoryProgressEntity
import com.example.edukidsaplication.database.EduKidsDatabase
import com.example.edukidsaplication.database.LessonContentEntity
import com.example.edukidsaplication.database.LessonEntity
import com.example.edukidsaplication.database.UserProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ContentRepository(private val context: Context) {
    private val database = EduKidsDatabase.getDatabase(context)
    private val categoryDao = database.categoryDao()
    private val lessonDao = database.lessonDao()
    private val lessonContentDao = database.lessonContentDao()
    private val userProgressDao = database.userProgressDao()
    private val categoryProgressDao = database.categoryProgressDao()

    // Categorías
    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    suspend fun getCategoryById(categoryId: String): CategoryEntity? {
        return categoryDao.getCategoryById(categoryId)
    }

    // Lecciones
    fun getLessonsByCategory(categoryId: String): Flow<List<LessonEntity>> {
        return lessonDao.getLessonsByCategory(categoryId)
    }

    suspend fun getLessonById(lessonId: String): LessonEntity? {
        return lessonDao.getLessonById(lessonId)
    }

    // Contenido de lecciones
    fun getLessonContents(lessonId: String): Flow<List<LessonContentEntity>> {
        return lessonContentDao.getLessonContents(lessonId)
    }

    // Progreso del usuario en lecciones
    suspend fun getLessonProgress(userId: String, lessonId: String): UserProgressEntity? {
        return userProgressDao.getUserLessonProgress(userId, lessonId)
    }

    fun getUserLessonsProgress(userId: String): Flow<List<UserProgressEntity>> {
        return userProgressDao.getAllUserProgress(userId)
    }

    suspend fun updateLessonProgress(userId: String, lessonId: String, score: Int, isCompleted: Boolean) {
        userProgressDao.updateLessonCompletion(userId, lessonId, score, isCompleted)

        // Obtenemos la lección para saber a qué categoría pertenece
        val lesson = lessonDao.getLessonById(lessonId) ?: return

        // Actualizamos el progreso de la categoría
        categoryProgressDao.updateCategoryProgress(userId, lesson.categoryId, userProgressDao)
    }

    // Progreso del usuario en categorías
    fun getUserCategoriesProgress(userId: String): Flow<List<CategoryProgressEntity>> {
        return categoryProgressDao.getAllCategoryProgress(userId)
    }

    suspend fun getCategoryProgress(userId: String, categoryId: String): CategoryProgressEntity? {
        return categoryProgressDao.getCategoryProgress(userId, categoryId)
    }

    // Obtener categorías con información de progreso
    fun getCategoriesWithProgress(userId: String): Flow<List<CategoryWithProgress>> {
        val categories = categoryDao.getAllCategories()
        val categoryProgress = categoryProgressDao.getAllCategoryProgress(userId)

        return categories.combine(categoryProgress) { cats, progress ->
            cats.map { category ->
                val catProgress = progress.find { it.categoryId == category.categoryId }
                CategoryWithProgress(
                    category = category,
                    lessonsCompleted = catProgress?.lessonsCompleted ?: 0,
                    totalLessons = catProgress?.totalLessons ?: 0,
                    isCompleted = catProgress?.isCompleted ?: false
                )
            }
        }
    }

    // Obtener lecciones con información de progreso
    fun getLessonsWithProgress(userId: String, categoryId: String): Flow<List<LessonWithProgress>> {
        val lessons = lessonDao.getLessonsByCategory(categoryId)
        val userProgress = userProgressDao.getAllUserProgress(userId)

        return lessons.combine(userProgress) { lessonsList, progressList ->
            lessonsList.map { lesson ->
                val progress = progressList.find { it.lessonId == lesson.lessonId }
                LessonWithProgress(
                    lesson = lesson,
                    isCompleted = progress?.isCompleted ?: false,
                    score = progress?.score ?: 0,
                    completedAt = progress?.completedAt
                )
            }
        }
    }
}

// Clases para representar datos combinados
data class CategoryWithProgress(
    val category: CategoryEntity,
    val lessonsCompleted: Int,
    val totalLessons: Int,
    val isCompleted: Boolean
)

data class LessonWithProgress(
    val lesson: LessonEntity,
    val isCompleted: Boolean,
    val score: Int,
    val completedAt: Long?
)
