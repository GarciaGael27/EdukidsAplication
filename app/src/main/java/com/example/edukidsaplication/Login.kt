package com.example.edukidsaplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(onBackToLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(320.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1CD8D2), Color(0xFF93EDC7))
                        ),
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_info_details), // Cambia por tu logo
                    contentDescription = "Logo EduKids",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Registro", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("¡Crea tu cuenta para aprender!", fontSize = 15.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            var user by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var pass by remember { mutableStateOf("") }
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = { /* Acción de registro */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F8CFF)
                )
            ) {
                Text("Registrarse", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = Color(0xFF4F8CFF),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { onBackToLogin() }
            )
        }
    }
}

@Composable
fun LoginRegisterSwitcher() {
    var showLogin by remember { mutableStateOf(true) }
    if (showLogin) {
        LoginScreen(onRegisterClick = { showLogin = false })
    } else {
        RegisterScreen(onBackToLogin = { showLogin = true })
    }
}

// Modifica LoginScreen para aceptar onRegisterClick
@Composable
fun LoginScreen(onRegisterClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(320.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1CD8D2), Color(0xFF93EDC7))
                        ),
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_info_details), // Cambia por tu logo
                    contentDescription = "Logo EduKids",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("EduKids", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("¡Aprender es divertido!", fontSize = 15.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            var user by remember { mutableStateOf("") }
            var pass by remember { mutableStateOf("") }
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = { /* Acción de login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F8CFF)
                )
            ) {
                Text("Iniciar Sesión", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = Color(0xFF4F8CFF),
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { onRegisterClick() }
            )
        }
    }
}