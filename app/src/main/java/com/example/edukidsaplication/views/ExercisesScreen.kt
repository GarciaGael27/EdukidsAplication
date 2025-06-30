package com.example.edukidsaplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukidsaplication.viewmodel.LessonsViewModel
import com.example.edukidsaplication.viewmodel.Problem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    lessonId: String,
    lessonTitle: String,
    lessonsViewModel: LessonsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading by lessonsViewModel.isLoading.collectAsState()
    val problems by lessonsViewModel.problems.collectAsState()
    val currentProblemIndex by lessonsViewModel.currentProblemIndex.collectAsState()
    val completedProblems by lessonsViewModel.completedProblems.collectAsState()
    val score by lessonsViewModel.score.collectAsState()
    val instructions by lessonsViewModel.instructions.collectAsState()

    // Estado para la respuesta del usuario
    var userAnswer by remember { mutableStateOf("") }

    // Estado para el feedback visual
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    // Estado para guardar el problema actual mientras se muestra feedback
    var currentProblem by remember { mutableStateOf<Problem?>(null) }

    // Estado para el diálogo de finalización
    var showCompletionDialog by remember { mutableStateOf(false) }

    // Cargar los ejercicios cuando se muestra la pantalla
    LaunchedEffect(lessonId) {
        lessonsViewModel.loadExercisesForLesson(lessonId)
    }

    // Mostrar diálogo cuando se completan todos los problemas
    // Pero solo si realmente has completado alguno en esta sesión
    LaunchedEffect(completedProblems, problems.size) {
        if (problems.isNotEmpty() && completedProblems >= problems.size && completedProblems > 0) {
            showCompletionDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = lessonTitle,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4F8CFF)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF4F8CFF)
                )
            } else if (problems.isEmpty()) {
                Text(
                    text = "No hay ejercicios disponibles para esta lección",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (showCompletionDialog) {
                CompletionDialog(
                    score = score,
                    totalProblems = problems.size,
                    onDismiss = { onNavigateBack() }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Instrucciones generales
                    if (instructions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE3F2FD)
                            )
                        ) {
                            Text(
                                text = instructions,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2),
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    val problemToShow = if (currentProblemIndex < problems.size) {
                        problems[currentProblemIndex]
                    } else null

                    if (problemToShow != null) {
                        when (lessonsViewModel.exerciseType.collectAsState().value) {
                            "simple_addition", "simple_subtraction", "simple_multiplication", "simple_division", "multiplication_table" -> {
                                MathProblemContent(
                                    problem = problemToShow,
                                    currentProblemNumber = currentProblemIndex + 1,
                                    totalProblems = problems.size,
                                    userAnswer = userAnswer,
                                    onUserAnswerChange = { userAnswer = it },
                                    showFeedback = showFeedback,
                                    isCorrectAnswer = isCorrect,
                                    currentProblemForFeedback = currentProblem,
                                    onSubmitAnswer = {
                                        currentProblem = problemToShow
                                        isCorrect = lessonsViewModel.submitAnswer(userAnswer)
                                        showFeedback = true
                                        userAnswer = ""
                                    },
                                    onNextProblem = {
                                        showFeedback = false
                                        currentProblem = null
                                        lessonsViewModel.nextProblem()
                                    }
                                )
                            }
                            "vocabulary", "grammar", "spelling" -> {
                                LanguageProblemContent(
                                    problem = problemToShow,
                                    currentProblemNumber = currentProblemIndex + 1,
                                    totalProblems = problems.size,
                                    userAnswer = userAnswer,
                                    onUserAnswerChange = { userAnswer = it },
                                    showFeedback = showFeedback,
                                    isCorrectAnswer = isCorrect,
                                    currentProblemForFeedback = currentProblem,
                                    onSubmitAnswer = {
                                        currentProblem = problemToShow
                                        isCorrect = lessonsViewModel.submitAnswer(userAnswer)
                                        showFeedback = true
                                        userAnswer = ""
                                    },
                                    onNextProblem = {
                                        showFeedback = false
                                        currentProblem = null
                                        lessonsViewModel.nextProblem()
                                    }
                                )
                            }
                            "animals", "plants", "body" -> {
                                ScienceProblemContent(
                                    problem = problemToShow,
                                    currentProblemNumber = currentProblemIndex + 1,
                                    totalProblems = problems.size,
                                    userAnswer = userAnswer,
                                    onUserAnswerChange = { userAnswer = it },
                                    showFeedback = showFeedback,
                                    isCorrectAnswer = isCorrect,
                                    currentProblemForFeedback = currentProblem,
                                    onSubmitAnswer = {
                                        currentProblem = problemToShow
                                        isCorrect = lessonsViewModel.submitAnswer(userAnswer)
                                        showFeedback = true
                                        userAnswer = ""
                                    },
                                    onNextProblem = {
                                        showFeedback = false
                                        currentProblem = null
                                        lessonsViewModel.nextProblem()
                                    }
                                )
                            }
                            else -> {
                                // Contenido genérico para otros tipos de ejercicios
                                GenericProblemContent(
                                    problem = problemToShow,
                                    currentProblemNumber = currentProblemIndex + 1,
                                    totalProblems = problems.size,
                                    userAnswer = userAnswer,
                                    onUserAnswerChange = { userAnswer = it },
                                    showFeedback = showFeedback,
                                    isCorrectAnswer = isCorrect,
                                    currentProblemForFeedback = currentProblem,
                                    onSubmitAnswer = {
                                        currentProblem = problemToShow
                                        isCorrect = lessonsViewModel.submitAnswer(userAnswer)
                                        showFeedback = true
                                        userAnswer = ""
                                    },
                                    onNextProblem = {
                                        showFeedback = false
                                        currentProblem = null
                                        lessonsViewModel.nextProblem()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProblemContent(
    problem: Problem,
    currentProblemNumber: Int,
    totalProblems: Int,
    userAnswer: String,
    onUserAnswerChange: (String) -> Unit,
    showFeedback: Boolean,
    isCorrectAnswer: Boolean,
    currentProblemForFeedback: Problem?, // Recibimos el problema guardado para el feedback
    onSubmitAnswer: () -> Unit,
    onNextProblem: () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    contentDisplay: @Composable () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progreso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ejercicio $currentProblemNumber de $totalProblems",
                fontSize = 16.sp,
                color = Color(0xFF4F8CFF),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            LinearProgressIndicator(
                progress = { currentProblemNumber.toFloat() / totalProblems },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp),
                color = Color(0xFF4F8CFF),
            )
        }

        // Tarjeta del problema
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Contenido específico del problema
                contentDisplay()

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de respuesta
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = onUserAnswerChange,
                    label = { Text("Tu respuesta") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = keyboardType
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (userAnswer.isNotEmpty() && !showFeedback) {
                                onSubmitAnswer()
                            }
                        }
                    ),
                    enabled = !showFeedback
                )

                // Botón para enviar respuesta o continuar
                Button(
                    onClick = {
                        if (showFeedback) {
                            onNextProblem()
                        } else if (userAnswer.isNotEmpty()) {
                            focusManager.clearFocus()
                            onSubmitAnswer()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showFeedback) {
                            if (isCorrectAnswer) Color(0xFF4CAF50) else Color(0xFFF44336)
                        } else {
                            Color(0xFF4F8CFF)
                        }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = userAnswer.isNotEmpty() || showFeedback
                ) {
                    Text(
                        text = if (showFeedback) "Continuar" else "Comprobar",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Feedback
        if (showFeedback) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrectAnswer) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCorrectAnswer) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isCorrectAnswer) "Correcto" else "Incorrecto",
                        tint = if (isCorrectAnswer) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    val feedbackText = if (isCorrectAnswer) {
                        "¡Correcto!"
                    } else {
                        currentProblemForFeedback?.let {
                            "Incorrecto. La respuesta correcta es: ${it.answer}"
                        } ?: "Incorrecto"
                    }

                    Text(
                        text = feedbackText,
                        color = if (isCorrectAnswer) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MathProblemContent(
    problem: Problem,
    currentProblemNumber: Int,
    totalProblems: Int,
    userAnswer: String,
    onUserAnswerChange: (String) -> Unit,
    showFeedback: Boolean,
    isCorrectAnswer: Boolean,
    currentProblemForFeedback: Problem?,
    onSubmitAnswer: () -> Unit,
    onNextProblem: () -> Unit
) {
    ProblemContent(
        problem = problem,
        currentProblemNumber = currentProblemNumber,
        totalProblems = totalProblems,
        userAnswer = userAnswer,
        onUserAnswerChange = onUserAnswerChange,
        showFeedback = showFeedback,
        isCorrectAnswer = isCorrectAnswer,
        currentProblemForFeedback = currentProblemForFeedback,
        onSubmitAnswer = onSubmitAnswer,
        onNextProblem = onNextProblem,
        keyboardType = KeyboardType.Number,
        contentDisplay = {
            MathProblemDisplay(problem = problem)
        }
    )
}

@Composable
fun LanguageProblemContent(
    problem: Problem,
    currentProblemNumber: Int,
    totalProblems: Int,
    userAnswer: String,
    onUserAnswerChange: (String) -> Unit,
    showFeedback: Boolean,
    isCorrectAnswer: Boolean,
    currentProblemForFeedback: Problem?,
    onSubmitAnswer: () -> Unit,
    onNextProblem: () -> Unit
) {
    ProblemContent(
        problem = problem,
        currentProblemNumber = currentProblemNumber,
        totalProblems = totalProblems,
        userAnswer = userAnswer,
        onUserAnswerChange = onUserAnswerChange,
        showFeedback = showFeedback,
        isCorrectAnswer = isCorrectAnswer,
        currentProblemForFeedback = currentProblemForFeedback,
        onSubmitAnswer = onSubmitAnswer,
        onNextProblem = onNextProblem,
        keyboardType = KeyboardType.Text,
        contentDisplay = {
            Text(
                text = problem.question,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F8CFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    )
}

@Composable
fun ScienceProblemContent(
    problem: Problem,
    currentProblemNumber: Int,
    totalProblems: Int,
    userAnswer: String,
    onUserAnswerChange: (String) -> Unit,
    showFeedback: Boolean,
    isCorrectAnswer: Boolean,
    currentProblemForFeedback: Problem?,
    onSubmitAnswer: () -> Unit,
    onNextProblem: () -> Unit
) {
    ProblemContent(
        problem = problem,
        currentProblemNumber = currentProblemNumber,
        totalProblems = totalProblems,
        userAnswer = userAnswer,
        onUserAnswerChange = onUserAnswerChange,
        showFeedback = showFeedback,
        isCorrectAnswer = isCorrectAnswer,
        currentProblemForFeedback = currentProblemForFeedback,
        onSubmitAnswer = onSubmitAnswer,
        onNextProblem = onNextProblem,
        keyboardType = KeyboardType.Text,
        contentDisplay = {
            Text(
                text = problem.question,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50), // Color verde para ciencias
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    )
}

@Composable
fun GenericProblemContent(
    problem: Problem,
    currentProblemNumber: Int,
    totalProblems: Int,
    userAnswer: String,
    onUserAnswerChange: (String) -> Unit,
    showFeedback: Boolean,
    isCorrectAnswer: Boolean,
    currentProblemForFeedback: Problem?,
    onSubmitAnswer: () -> Unit,
    onNextProblem: () -> Unit
) {
    ProblemContent(
        problem = problem,
        currentProblemNumber = currentProblemNumber,
        totalProblems = totalProblems,
        userAnswer = userAnswer,
        onUserAnswerChange = onUserAnswerChange,
        showFeedback = showFeedback,
        isCorrectAnswer = isCorrectAnswer,
        currentProblemForFeedback = currentProblemForFeedback,
        onSubmitAnswer = onSubmitAnswer,
        onNextProblem = onNextProblem,
        keyboardType = KeyboardType.Text,
        contentDisplay = {
            Text(
                text = problem.question,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    )
}

@Composable
fun MathProblemDisplay(problem: Problem) {
    val operationSymbol = when (problem.type) {
        "simple_addition" -> "+"
        "simple_subtraction" -> "-"
        "simple_multiplication", "multiplication_table" -> "×"
        "simple_division" -> "÷"
        else -> "?"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visualización grande y clara del problema matemático
        Text(
            text = "${problem.num1} $operationSymbol ${problem.num2} = ?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CompletionDialog(
    score: Int,
    totalProblems: Int,
    onDismiss: () -> Unit
) {
    val percentage = if (totalProblems > 0) (score * 100) / totalProblems else 0

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = "¡Lección completada!",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF4F8CFF)
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Has completado todos los ejercicios",
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Puntuación: $score de $totalProblems",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (percentage >= 70) Color(0xFF4CAF50) else Color(0xFFF44336)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${percentage}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = if (percentage >= 70) Color(0xFF4CAF50) else Color(0xFFF44336)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (percentage >= 70) "¡Buen trabajo!" else "¡Sigue practicando!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Volver a las lecciones",
                    color = Color(0xFF4F8CFF),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}





