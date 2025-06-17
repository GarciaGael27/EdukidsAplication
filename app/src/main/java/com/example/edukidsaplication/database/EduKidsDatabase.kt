package com.example.edukidsaplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Convertidores de tipos para manejar valores como Long? (nullable)
class Converters {
    @TypeConverter
    fun longToTimestamp(value: Long?): Long {
        return value ?: 0L
    }

    @TypeConverter
    fun timestampToLong(timestamp: Long): Long? {
        return if (timestamp == 0L) null else timestamp
    }
}

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        LessonEntity::class,
        LessonContentEntity::class,
        UserProgressEntity::class,
        CategoryProgressEntity::class
    ],
    version = 2, // Incrementamos la versión de 1 a 2
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EduKidsDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun lessonDao(): LessonDao
    abstract fun lessonContentDao(): LessonContentDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun categoryProgressDao(): CategoryProgressDao

    companion object {
        @Volatile
        private var INSTANCE: EduKidsDatabase? = null

        fun getDatabase(context: Context): EduKidsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduKidsDatabase::class.java,
                    "edukids_database"
                )
                    .fallbackToDestructiveMigration(false)
                .allowMainThreadQueries() // Permitir consultas en el hilo principal (solo para desarrollo)
                .build()

                INSTANCE = instance

                // Precargar datos iniciales en un hilo separado
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        instance.prepopulateDatabase()
                    } catch (e: Exception) {
                        android.util.Log.e("EduKidsDatabase", "Error al precargar datos: ${e.message}")
                        e.printStackTrace()
                    }
                }

                instance
            }
        }

        private suspend fun EduKidsDatabase.prepopulateDatabase() {
            val categoryDao = categoryDao()
            val lessonDao = lessonDao()
            val lessonContentDao = lessonContentDao()

            // Verificar si ya existen categorías
            if (!categoryDao.hasAnyCategory()) {
                // Precargar categoría de Matemáticas
                val mathCategory = CategoryEntity(
                    categoryId = "math_001",
                    name = "Matemáticas",
                    description = "Aprende matemáticas básicas para niños de primaria",
                    imageResourceName = "ic_math",
                    order = 1
                )
                categoryDao.insertCategory(mathCategory)

                // Precargar lecciones de suma
                val sumLessons = listOf(
                    LessonEntity(
                        lessonId = "sum_001",
                        categoryId = "math_001",
                        title = "Sumas de un dígito",
                        description = "Aprende a sumar números de un dígito",
                        difficulty = 1,
                        order = 1,
                        type = "SUMA"
                    ),
                    LessonEntity(
                        lessonId = "sum_002",
                        categoryId = "math_001",
                        title = "Sumas de dos dígitos",
                        description = "Practica sumas con números de dos dígitos",
                        difficulty = 2,
                        order = 2,
                        type = "SUMA"
                    ),
                    LessonEntity(
                        lessonId = "sum_003",
                        categoryId = "math_001",
                        title = "Sumas llevando",
                        description = "Aprende a sumar llevando unidades a decenas",
                        difficulty = 3,
                        order = 3,
                        type = "SUMA"
                    )
                )
                lessonDao.insertLessons(sumLessons)

                // Precargar lecciones de resta
                val subtractLessons = listOf(
                    LessonEntity(
                        lessonId = "sub_001",
                        categoryId = "math_001",
                        title = "Restas de un dígito",
                        description = "Aprende a restar números de un dígito",
                        difficulty = 1,
                        order = 4,
                        type = "RESTA"
                    ),
                    LessonEntity(
                        lessonId = "sub_002",
                        categoryId = "math_001",
                        title = "Restas de dos dígitos",
                        description = "Practica restas con números de dos dígitos",
                        difficulty = 2,
                        order = 5,
                        type = "RESTA"
                    ),
                    LessonEntity(
                        lessonId = "sub_003",
                        categoryId = "math_001",
                        title = "Restas prestando",
                        description = "Aprende a restar prestando de decenas a unidades",
                        difficulty = 3,
                        order = 6,
                        type = "RESTA"
                    )
                )
                lessonDao.insertLessons(subtractLessons)

                // Precargar lecciones de multiplicación
                val multiplyLessons = listOf(
                    LessonEntity(
                        lessonId = "mul_001",
                        categoryId = "math_001",
                        title = "Tablas del 1 al 5",
                        description = "Aprende las tablas de multiplicar del 1 al 5",
                        difficulty = 1,
                        order = 7,
                        type = "MULTIPLICACION"
                    ),
                    LessonEntity(
                        lessonId = "mul_002",
                        categoryId = "math_001",
                        title = "Tablas del 6 al 10",
                        description = "Aprende las tablas de multiplicar del 6 al 10",
                        difficulty = 2,
                        order = 8,
                        type = "MULTIPLICACION"
                    ),
                    LessonEntity(
                        lessonId = "mul_003",
                        categoryId = "math_001",
                        title = "Multiplicación por dos dígitos",
                        description = "Aprende a multiplicar números de dos dígitos",
                        difficulty = 3,
                        order = 9,
                        type = "MULTIPLICACION"
                    )
                )
                lessonDao.insertLessons(multiplyLessons)

                // Precargar lecciones de división
                val divideLessons = listOf(
                    LessonEntity(
                        lessonId = "div_001",
                        categoryId = "math_001",
                        title = "Divisiones exactas simples",
                        description = "Aprende divisiones básicas con números pequeños",
                        difficulty = 2,
                        order = 10,
                        type = "DIVISION"
                    ),
                    LessonEntity(
                        lessonId = "div_002",
                        categoryId = "math_001",
                        title = "Divisiones con resto",
                        description = "Aprende divisiones que tienen resto",
                        difficulty = 2,
                        order = 11,
                        type = "DIVISION"
                    ),
                    LessonEntity(
                        lessonId = "div_003",
                        categoryId = "math_001",
                        title = "Divisiones por dos dígitos",
                        description = "Aprende a dividir por números de dos dígitos",
                        difficulty = 3,
                        order = 12,
                        type = "DIVISION"
                    )
                )
                lessonDao.insertLessons(divideLessons)

                // Precargar contenido para las lecciones (ejemplos de ejercicios)
                val lessonContents = mutableListOf<LessonContentEntity>()

                // Contenido para sumas de un dígito
                lessonContents.add(
                    LessonContentEntity(
                        contentId = "content_sum_001_1",
                        lessonId = "sum_001",
                        content = """
                            {
                                "type": "simple_addition",
                                "problems": [
                                    {"num1": 2, "num2": 3, "answer": 5},
                                    {"num1": 4, "num2": 5, "answer": 9},
                                    {"num1": 1, "num2": 6, "answer": 7},
                                    {"num1": 7, "num2": 2, "answer": 9},
                                    {"num1": 3, "num2": 4, "answer": 7}
                                ],
                                "instructions": "Resuelve las siguientes sumas"
                            }
                        """.trimIndent(),
                        order = 1
                    )
                )

                // Contenido para restas de un dígito
                lessonContents.add(
                    LessonContentEntity(
                        contentId = "content_sub_001_1",
                        lessonId = "sub_001",
                        content = """
                            {
                                "type": "simple_subtraction",
                                "problems": [
                                    {"num1": 5, "num2": 3, "answer": 2},
                                    {"num1": 9, "num2": 4, "answer": 5},
                                    {"num1": 7, "num2": 2, "answer": 5},
                                    {"num1": 8, "num2": 5, "answer": 3},
                                    {"num1": 6, "num2": 1, "answer": 5}
                                ],
                                "instructions": "Resuelve las siguientes restas"
                            }
                        """.trimIndent(),
                        order = 1
                    )
                )

                // Contenido para multiplicaciones
                lessonContents.add(
                    LessonContentEntity(
                        contentId = "content_mul_001_1",
                        lessonId = "mul_001",
                        content = """
                            {
                                "type": "multiplication_table",
                                "problems": [
                                    {"num1": 2, "num2": 3, "answer": 6},
                                    {"num1": 4, "num2": 2, "answer": 8},
                                    {"num1": 5, "num2": 3, "answer": 15},
                                    {"num1": 3, "num2": 4, "answer": 12},
                                    {"num1": 5, "num2": 5, "answer": 25}
                                ],
                                "instructions": "Resuelve las siguientes multiplicaciones"
                            }
                        """.trimIndent(),
                        order = 1
                    )
                )

                // Contenido para divisiones
                lessonContents.add(
                    LessonContentEntity(
                        contentId = "content_div_001_1",
                        lessonId = "div_001",
                        content = """
                            {
                                "type": "simple_division",
                                "problems": [
                                    {"num1": 6, "num2": 2, "answer": 3},
                                    {"num1": 8, "num2": 4, "answer": 2},
                                    {"num1": 10, "num2": 5, "answer": 2},
                                    {"num1": 12, "num2": 3, "answer": 4},
                                    {"num1": 15, "num2": 5, "answer": 3}
                                ],
                                "instructions": "Resuelve las siguientes divisiones"
                            }
                        """.trimIndent(),
                        order = 1
                    )
                )

                lessonContentDao.insertLessonContents(lessonContents)
            }
        }
    }
}
