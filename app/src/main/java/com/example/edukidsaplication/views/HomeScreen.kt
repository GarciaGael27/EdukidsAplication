package com.example.edukidsaplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukidsaplication.viewmodel.HomeViewModel
import kotlin.random.Random

// Datos para los consejos educativos
data class EducationalTip(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToLessons: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onSessionExpired: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val homeState = homeViewModel.homeState

    // Lista de consejos educativos rotativos
    val educationalTips = remember {
        listOf(
            EducationalTip(
                "Practica diariamente",
                "Dedica 15 minutos al día a las matemáticas para mejorar tus habilidades",
                Icons.Default.Create,
                Color(0xFF4CAF50)
            ),
            EducationalTip(
                "Usa objetos reales",
                "Cuenta juguetes, frutas o lápices para practicar sumas y restas",
                Icons.Default.Create,
                Color(0xFF2196F3)
            ),
            EducationalTip(
                "Celebra tus logros",
                "¡Cada ejercicio correcto es un paso hacia ser mejor en matemáticas!",
                Icons.Default.Create,
                Color(0xFFFF9800)
            ),
            EducationalTip(
                "Aprende jugando",
                "Las matemáticas son más divertidas cuando las practicas como un juego",
                Icons.Default.Create,
                Color(0xFF9C27B0)
            ),
            EducationalTip(
                "Busca patrones",
                "En las tablas de multiplicar hay patrones que te ayudarán a recordar",
                Icons.Default.Create,
                Color(0xFFFF5722)
            ),
            EducationalTip(
                "No tengas miedo a los errores",
                "Los errores nos ayudan a aprender y mejorar cada día",
                Icons.Default.Create,
                Color(0xFF607D8B)
            )
        )
    }

    // Seleccionar un consejo aleatorio
    var currentTip by remember {
        mutableStateOf(educationalTips[Random.nextInt(educationalTips.size)])
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (homeState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4F8CFF)
                    )
                }
            } else if (homeState.error != null && homeState.user == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = homeState.error,
                            color = Color.Red,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )

                        Button(
                            onClick = { onSignOut() },
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4F8CFF)
                            )
                        ) {
                            Text(
                                text = "Volver al inicio de sesión",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            } else if (homeState.user != null) {
                // Saludo personalizado con el nombre del usuario
                WelcomeSection(userName = "${homeState.user.nombre} ${homeState.user.apellido}")

                Spacer(modifier = Modifier.height(24.dp))

                // Consejo educativo del día
                DailyTipCard(tip = currentTip) {
                    currentTip = educationalTips[Random.nextInt(educationalTips.size)]
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de motivación matemática
                MathMotivationSection()

                Spacer(modifier = Modifier.height(24.dp))

                // Stats rápidas (simuladas por ahora)
                QuickStatsSection()

                Spacer(modifier = Modifier.height(32.dp))

                // Botón principal para empezar las lecciones
                Button(
                    onClick = { onNavigateToLessons() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F8CFF)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "¡Comenzar a aprender matemáticas!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
            }
        }
    }
}

@Composable
fun WelcomeSection(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "¡Hola $userName! 👋",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F8CFF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bienvenido/a a EduKids",
                fontSize = 18.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "¿Listo para una nueva aventura matemática?",
                fontSize = 16.sp,
                color = Color(0xFF888888)
            )
        }
    }
}

@Composable
fun DailyTipCard(tip: EducationalTip, onRefresh: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = tip.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(tip.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tip.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "💡 Consejo del día",
                        fontSize = 14.sp,
                        color = tip.color,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = tip.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tip.description,
                fontSize = 16.sp,
                color = Color(0xFF666666),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(
                    containerColor = tip.color
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Otro consejo",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MathMotivationSection() {
    val motivationalQuotes = listOf(
        "Las matemáticas son el alfabeto con el cual Dios ha escrito el universo 🌟",
        "Cada problema resuelto te hace más inteligente 🧠",
        "Las matemáticas no mienten, las personas sí 📊",
        "La práctica hace al maestro 🎯",
        "¡Eres más capaz de lo que crees! 💪"
    )

    val randomQuote = remember { motivationalQuotes[Random.nextInt(motivationalQuotes.size)] }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✨ Motivación matemática ✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = randomQuote,
                fontSize = 16.sp,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun QuickStatsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📊 Tus estadísticas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "16",
                    label = "Lecciones\ndisponibles",
                    color = Color(0xFF2196F3)
                )
                StatItem(
                    value = "120+",
                    label = "Ejercicios\nmatemáticos",
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    value = "4",
                    label = "Tipos de\noperaciones",
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}


@Composable
fun AchievementBadge(emoji: String, name: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(80.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                fontSize = 10.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    device = "id:pixel_9",
    name = "Pantalla de inicio mejorada")
@Composable
fun HomePreview() {
    // No podemos usar el preview con el ViewModel real
}