package com.example.edukidsaplication.navegation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.edukidsaplication.viewmodel.HomeViewModel
import com.example.edukidsaplication.viewmodel.LoginViewModel
import com.example.edukidsaplication.viewmodel.RegisterViewModel
import com.example.edukidsaplication.views.HomeScreen
import com.example.edukidsaplication.views.LessonsScreen
import com.example.edukidsaplication.views.LoginScreen
import com.example.edukidsaplication.views.RegisterScreen
import com.example.edukidsaplication.views.SettingsScreen

// Objeto que contiene todas las rutas de navegación
object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val LESSONS = "lessons"
    const val SETTINGS = "settings"
}

// Definición de los items de navegación para la barra inferior
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = NavRoutes.HOME,
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Lessons : BottomNavItem(
        route = NavRoutes.LESSONS,
        title = "Lecciones",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange
    )

    object Settings : BottomNavItem(
        route = NavRoutes.SETTINGS,
        title = "Ajustes",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        val items = listOf(Home, Lessons, Settings)
    }
}

/**
 * Punto de entrada principal para la navegación de la aplicación.
 * Gestiona todas las rutas y la navegación entre pantallas.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val myStartDestination = NavRoutes.LOGIN

    // Creamos los ViewModels aquí para compartirlos entre destinos
    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    // Verificar si hay un usuario logueado para navegar directamente al home
    LaunchedEffect(loginViewModel.loginState.isLoggedIn) {
        if (loginViewModel.loginState.isLoggedIn) {
            navController.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.LOGIN) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = myStartDestination) {
        // Rutas de autenticación (sin barra de navegación)
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onClickRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                },
                onLoginSuccess = {
                    // Refrescar datos del home antes de navegar
                    homeViewModel.refreshData()
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                registerViewModel = registerViewModel,
                onClickLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // No necesitamos navegar aquí, porque ya lo hacemos en onClickLogin
                }
            )
        }

        // Rutas principales de la aplicación (con barra de navegación)
        composable(NavRoutes.HOME) {
            MainScreenWithBottomNav(
                homeViewModel = homeViewModel,
                navController = navController,
                currentRoute = NavRoutes.HOME,
                onSignOut = {
                    performSignOut(navController, homeViewModel, loginViewModel)
                }
            ) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToLessons = {
                        navController.navigate(NavRoutes.LESSONS) {
                            // Evitamos copias múltiples del mismo destino
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(NavRoutes.LESSONS) {
            MainScreenWithBottomNav(
                homeViewModel = homeViewModel,
                navController = navController,
                currentRoute = NavRoutes.LESSONS,
                onSignOut = {
                    performSignOut(navController, homeViewModel, loginViewModel)
                }
            ) {
                LessonsScreen(
                    homeViewModel = homeViewModel
                )
            }
        }

        composable(NavRoutes.SETTINGS) {
            MainScreenWithBottomNav(
                homeViewModel = homeViewModel,
                navController = navController,
                currentRoute = NavRoutes.SETTINGS,
                onSignOut = {
                    performSignOut(navController, homeViewModel, loginViewModel)
                }
            ) {
                SettingsScreen(
                    homeViewModel = homeViewModel,
                    onSignOut = {
                        performSignOut(navController, homeViewModel, loginViewModel)
                    }
                )
            }
        }
    }
}

/**
 * Función para manejar el cierre de sesión de forma unificada.
 * Limpia todos los datos de sesión y navega a la pantalla de login.
 */
private fun performSignOut(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    loginViewModel: LoginViewModel
) {
    // Primero limpiamos completamente los datos de sesión
    homeViewModel.signOut()

    // Reiniciamos el estado del LoginViewModel
    loginViewModel.resetLoginState()

    // Navegamos a la pantalla de login
    navController.navigate(NavRoutes.LOGIN) {
        // Eliminamos todas las pantallas del backstack para evitar
        // que el usuario pueda volver atrás después de cerrar sesión
        popUpTo(0) { inclusive = true }
    }
}

/**
 * Componente que proporciona un scaffold con barra de navegación inferior
 * para las pantallas principales de la aplicación.
 */
@Composable
fun MainScreenWithBottomNav(
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    currentRoute: String,
    onSignOut: () -> Unit,
    content: @Composable () -> Unit
) {
    // Utilizamos Scaffold para proporcionar una estructura básica con una barra de navegación inferior
    Scaffold(
        bottomBar = {
            BottomNavigation(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding ->
        // Contenido de la pantalla actual con padding para la barra de navegación
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            content()
        }
    }
}

/**
 * Componente que muestra la barra de navegación inferior
 * con los elementos de navegación definidos en BottomNavItem.
 */
@Composable
fun BottomNavigation(
    navController: NavHostController,
    currentRoute: String
) {
    // Creamos la barra de navegación de Material Design
    NavigationBar {
        // Iteramos sobre los items de navegación para crear los botones
        BottomNavItem.items.forEach { item ->
            // Verificamos si la ruta actual coincide con la ruta del item
            val selected = currentRoute == item.route

            // Creamos el elemento de la barra de navegación
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        // Al hacer clic, navegamos a la ruta correspondiente
                        navController.navigate(item.route) {
                            // Evitamos múltiples copias de la misma pantalla en el backstack
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Restauramos el estado si ya existía
                            restoreState = true
                            // Evita múltiples copias del mismo destino
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
