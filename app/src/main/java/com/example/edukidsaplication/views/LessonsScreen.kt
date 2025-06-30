package com.example.edukidsaplication.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukidsaplication.repository.CategoryWithProgress
import com.example.edukidsaplication.viewmodel.HomeViewModel
import com.example.edukidsaplication.viewmodel.LessonsViewModel

@Composable
fun LessonsScreen(
    homeViewModel: HomeViewModel,
    lessonsViewModel: LessonsViewModel,
    onNavigateToCategoryLessons: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val categories by homeViewModel.categories.collectAsState()
    val homeState = homeViewModel.homeState

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        if (homeState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF4F8CFF)
            )
        } else if (categories.isEmpty()) {
            Text(
                text = "No hay categorías disponibles",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Materias Disponibles",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4F8CFF),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            category = category,
                            onClick = {
                                onNavigateToCategoryLessons(
                                    category.category.categoryId,
                                    category.category.name
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: CategoryWithProgress,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = category.category.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F8CFF)
            )
            Text(
                text = category.category.description,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Progreso
            Text(
                text = "Progreso: ${category.lessonsCompleted}/${category.totalLessons} lecciones",
                fontSize = 12.sp,
                color = if (category.isCompleted) Color.Green else Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
