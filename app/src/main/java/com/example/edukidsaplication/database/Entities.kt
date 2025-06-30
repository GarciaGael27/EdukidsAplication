package com.example.edukidsaplication.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val nombre: String,
    val apellido: String,
    val lastLogin: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "category_table",
    indices = [Index("categoryId")]
)
data class CategoryEntity(
    @PrimaryKey val categoryId: String,
    val name: String,
    val description: String,
    val imageResourceName: String,
    val order: Int
)

@Entity(
    tableName = "lesson_table",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class LessonEntity(
    @PrimaryKey val lessonId: String,
    val categoryId: String,
    val title: String,
    val description: String,
    val difficulty: Int, // 1: Fácil, 2: Medio, 3: Difícil
    val order: Int,
    val type: String // "SUMA", "RESTA", "MULTIPLICACION", "DIVISION", etc.
)

@Entity(
    tableName = "lesson_content_table",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["lessonId"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId")]
)
data class LessonContentEntity(
    @PrimaryKey val contentId: String,
    val lessonId: String,
    val content: String, // JSON que contiene los datos específicos del ejercicio
    val order: Int
)

@Entity(
    tableName = "user_progress_table",
    primaryKeys = ["userId", "lessonId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["lessonId"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("lessonId")]
)
data class UserProgressEntity(
    val userId: String,
    val lessonId: String,
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val completedAt: Long? = null,
    val lastAttemptAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "category_progress_table",
    primaryKeys = ["userId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("categoryId")]
)
data class CategoryProgressEntity(
    val userId: String,
    val categoryId: String,
    val lessonsCompleted: Int = 0,
    val totalLessons: Int = 0,
    val isCompleted: Boolean = false,
    val lastUpdatedAt: Long = System.currentTimeMillis()
)

