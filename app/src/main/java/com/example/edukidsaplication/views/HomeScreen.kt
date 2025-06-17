package com.example.edukidsaplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukidsaplication.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToLessons: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onSessionExpired: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val homeState = homeViewModel.homeState

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
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
                // Mostrar el saludo personalizado con el nombre del usuario
                Text(
                    text = "¡Hola ${homeState.user.nombre} ${homeState.user.apellido}!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4F8CFF),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Bienvenido/a a EduKids",
                    fontSize = 20.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Botón para empezar las lecciones
                Button(
                    onClick = { onNavigateToLessons() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F8CFF)
                    )
                ) {
                    Text(
                        text = "Comenzar a aprender",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    device = "id:pixel_9",
    name = "Pantallita")
@Composable
fun HomePreview() {
    // No podemos usar el preview con el ViewModel real
}