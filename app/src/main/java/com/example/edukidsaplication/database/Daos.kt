package com.example.edukidsaplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM user_table WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM user_table ORDER BY lastLogin DESC LIMIT 1")
    suspend fun getLastLoggedInUser(): UserEntity?

    @Query("SELECT * FROM user_table ORDER BY lastLogin DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("UPDATE user_table SET lastLogin = :timestamp WHERE userId = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM category_table ORDER BY `order`")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category_table WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM category_table LIMIT 1)")
    suspend fun hasAnyCategory(): Boolean
}

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("SELECT * FROM lesson_table WHERE categoryId = :categoryId ORDER BY `order`")
    fun getLessonsByCategory(categoryId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lesson_table WHERE lessonId = :lessonId")
    suspend fun getLessonById(lessonId: String): LessonEntity?

    @Query("SELECT COUNT(*) FROM lesson_table WHERE categoryId = :categoryId")
    suspend fun getLessonCountForCategory(categoryId: String): Int
}

@Dao
interface LessonContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonContent(content: LessonContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonContents(contents: List<LessonContentEntity>)

    @Query("SELECT * FROM lesson_content_table WHERE lessonId = :lessonId ORDER BY `order`")
    fun getLessonContents(lessonId: String): Flow<List<LessonContentEntity>>
}

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgressEntity)

    @Update
    suspend fun updateUserProgress(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress_table WHERE userId = :userId AND lessonId = :lessonId")
    suspend fun getUserLessonProgress(userId: String, lessonId: String): UserProgressEntity?

    @Query("SELECT * FROM user_progress_table WHERE userId = :userId")
    fun getAllUserProgress(userId: String): Flow<List<UserProgressEntity>>

    @Transaction
    suspend fun updateLessonCompletion(userId: String, lessonId: String, score: Int, isCompleted: Boolean) {
        val lesson = getLessonByIdForProgress(lessonId) ?: return
        val existingProgress = getUserLessonProgress(userId, lessonId)

        if (existingProgress != null) {
            val updatedProgress = existingProgress.copy(
                score = score,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) System.currentTimeMillis() else existingProgress.completedAt,
                lastAttemptAt = System.currentTimeMillis()
            )
            updateUserProgress(updatedProgress)
        } else {
            val newProgress = UserProgressEntity(
                userId = userId,
                lessonId = lessonId,
                score = score,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) System.currentTimeMillis() else null
            )
            insertUserProgress(newProgress)
        }
    }

    @Query("SELECT * FROM lesson_table WHERE lessonId = :lessonId")
    suspend fun getLessonByIdForProgress(lessonId: String): LessonEntity?

    @Query("SELECT COUNT(*) FROM user_progress_table WHERE userId = :userId AND lessonId IN (SELECT lessonId FROM lesson_table WHERE categoryId = :categoryId) AND isCompleted = 1")
    suspend fun getCompletedLessonsCount(userId: String, categoryId: String): Int

    @Query("SELECT COUNT(*) FROM lesson_table WHERE categoryId = :categoryId")
    suspend fun getTotalLessonsInCategory(categoryId: String): Int
}

@Dao
interface CategoryProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryProgress(progress: CategoryProgressEntity)

    @Update
    suspend fun updateCategoryProgress(progress: CategoryProgressEntity)

    @Query("SELECT * FROM category_progress_table WHERE userId = :userId AND categoryId = :categoryId")
    suspend fun getCategoryProgress(userId: String, categoryId: String): CategoryProgressEntity?

    @Query("SELECT * FROM category_progress_table WHERE userId = :userId")
    fun getAllCategoryProgress(userId: String): Flow<List<CategoryProgressEntity>>

    @Transaction
    suspend fun updateCategoryProgress(userId: String, categoryId: String, userProgressDao: UserProgressDao) {
        val totalLessons = userProgressDao.getTotalLessonsInCategory(categoryId)
        val completedLessons = userProgressDao.getCompletedLessonsCount(userId, categoryId)
        val isCompleted = totalLessons > 0 && completedLessons >= totalLessons

        val existingProgress = getCategoryProgress(userId, categoryId)
        if (existingProgress != null) {
            val updatedProgress = existingProgress.copy(
                lessonsCompleted = completedLessons,
                totalLessons = totalLessons,
                isCompleted = isCompleted,
                lastUpdatedAt = System.currentTimeMillis()
            )
            updateCategoryProgress(updatedProgress)
        } else {
            val newProgress = CategoryProgressEntity(
                userId = userId,
                categoryId = categoryId,
                lessonsCompleted = completedLessons,
                totalLessons = totalLessons,
                isCompleted = isCompleted
            )
            insertCategoryProgress(newProgress)
        }
    }
}
