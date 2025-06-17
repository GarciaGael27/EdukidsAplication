package com.example.edukidsaplication.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edukidsaplication.R
import com.example.edukidsaplication.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onClickRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val loginState = loginViewModel.loginState

    // Efecto para navegar a la pantalla Home cuando el login es exitoso
    LaunchedEffect(loginState.isLoggedIn) {
        if (loginState.isLoggedIn) {
            onLoginSuccess()
        }
    }

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

            OutlinedTextField(
                value = loginState.username,
                onValueChange = { loginViewModel.onUsernameChange(it) },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Mostrar mensaje de error si existe
            if (loginState.error != null) {
                Text(
                    text = loginState.error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { loginViewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F8CFF)
                ),
                enabled = !loginState.isLoading
            ) {
                if (loginState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Iniciar Sesión", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = Color(0xFF4F8CFF),
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { onClickRegister() }
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_9", showSystemUi = true, name = "Pantallita")
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}