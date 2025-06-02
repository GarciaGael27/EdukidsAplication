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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(onBackToLogin: () -> Unit, onRegisterSuccess: () -> Unit = {}) {
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
                    .size(250.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_contexto),
                    contentDescription = "Logo EduKids",
                    modifier = Modifier.size(250.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Registro", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("¡Crea tu cuenta para aprender!", fontSize = 15.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            var user by remember { mutableStateOf("") }
            var name by remember { mutableStateOf("") }
            var lastName by remember { mutableStateOf("") }
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    // Aquí podrías guardar el usuario
                    if (user.isNotBlank() && name.isNotBlank() && lastName.isNotBlank()) onRegisterSuccess()
                },
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
fun LoginRegisterSwitcher(onLoginSuccess: () -> Unit = {}) {
    var showLogin by remember { mutableStateOf(true) }
    if (showLogin) {
        LoginScreen(onRegisterClick = { showLogin = false }, onLoginSuccess = onLoginSuccess)
    } else {
        RegisterScreen(onBackToLogin = { showLogin = true }, onRegisterSuccess = onLoginSuccess)
    }
}

@Composable
fun LoginScreen(onRegisterClick: () -> Unit = {}, onLoginSuccess: () -> Unit = {}) {
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
                    .size(250.dp)
                    .background( color = Color.White,
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_sintexto),
                    contentDescription = "Logo EduKids",
                    modifier = Modifier.size(250.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("EduKids", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("¡Aprender es divertido!", fontSize = 15.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            var user by remember { mutableStateOf("") }
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    if (user.isNotBlank()) onLoginSuccess()
                },
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


@Preview(showBackground = true, device = "id:pixel_9", showSystemUi = true, name = "Pantallita")
@Composable
fun LoginScreenPreview() {
    //LoginScreen()
    //RegisterScreen(onBackToLogin = {})
}