# EduKids Application

Aplicación educativa Android para niños que aprenden matemáticas a través de lecciones interactivas y ejercicios gamificados. Desarrollada con Jetpack Compose y arquitectura MVVM.

---

## Características principales

- **Autenticación local y remota** — Registro e inicio de sesión con respaldo en Firebase Firestore
- **Lecciones por categoría** — Contenido organizado por tema y nivel de dificultad
- **Ejercicios interactivos** — Problemas de suma, resta, multiplicación y división
- **Seguimiento de progreso** — Puntuación y avance por lección y categoría
- **Modo offline** — Funciona sin conexión a internet gracias a Room Database
- **Preferencias de usuario** — Tema oscuro, control de sonido y notificaciones

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose 2.9.0 |
| Base de datos local | Room 2.7.1 |
| Autenticación | Firebase Auth 23.2.1 |
| Base de datos remota | Cloud Firestore 25.1.4 |
| Almacenamiento ligero | SharedPreferences |
| Build | Android Gradle Plugin 8.9.3 + KSP |
| Asincronía | Kotlin Coroutines + Flow |

---

## Arquitectura

El proyecto sigue el patrón **MVVM (Model-View-ViewModel)** con una estrategia **offline-first**:

```
UI (Composables)
      ↓
ViewModel (StateFlow, viewModelScope)
      ↓
Repository (lógica de negocio)
      ↓
Room DB ←→ Firebase Firestore
```

### Capas del proyecto

```
com.example.edukidsaplication/
├── database/          # Entidades Room, DAOs y configuración de BD
├── di/                # Módulo de inyección de dependencias (AppModule)
├── model/             # Modelos de datos (User)
├── navegation/        # Rutas y NavHost
├── preferences/       # UserPreferences (SharedPreferences)
├── repository/        # UserRepository, ContentRepository
├── ui/theme/          # Colores y tipografía (Compose Theme)
├── viewmodel/         # ViewModels con lógica de negocio
├── views/             # Pantallas Composables
├── MainActivity.kt
└── EduKidsApplication.kt
```

---

## Pantallas

| Pantalla | Descripción |
|----------|-------------|
| `LoginScreen` | Inicio de sesión con nombre de usuario |
| `RegisterScreen` | Registro de nuevo usuario (nombre, apellido, username) |
| `HomeScreen` | Dashboard con consejos, tarjetas de categorías y progreso |
| `LessonsScreen` | Listado de categorías de lecciones disponibles |
| `CategoryLessonsScreen` | Lecciones dentro de una categoría por nivel de dificultad |
| `ExercisesScreen` | Ejercicios matemáticos interactivos con puntuación |
| `SettingsScreen` | Preferencias de la app y cierre de sesión |

### Flujo de navegación

```
Login ──→ Register
  │
  └──→ Home (barra de navegación inferior)
         ├── Home (Dashboard)
         ├── Lessons → CategoryLessons → Exercises
         └── Settings
```

---

## Base de datos (Room)

El esquema local contiene 6 entidades relacionadas:

### Entidades principales

- **UserEntity** — Datos del usuario (userId, username, nombre, apellido, lastLogin)
- **CategoryEntity** — Categorías de contenido (categoryId, name, description, imageResourceName, order)
- **LessonEntity** — Lecciones por categoría con tipo (`SUMA`, `RESTA`, `MULTIPLICACION`, `DIVISION`) y dificultad (1-3)
- **LessonContentEntity** — Contenido JSON con problemas matemáticos por lección
- **UserProgressEntity** — Progreso por lección: completado, puntuación (0-100%), timestamps
- **CategoryProgressEntity** — Progreso por categoría: lecciones completadas vs. total

### Contenido precargado

La base de datos (versión 3) incluye:
- 1 categoría: **Matemáticas**
- 12 lecciones organizadas en cuatro bloques de dificultad creciente
- 8 paquetes de contenido con problemas en formato JSON

---

## ViewModels

| ViewModel | Responsabilidad |
|-----------|----------------|
| `LoginViewModel` | Estado de autenticación y login |
| `RegisterViewModel` | Registro de nuevo usuario |
| `HomeViewModel` | Datos del usuario, progreso y categorías |
| `LessonsViewModel` | Gestión de lecciones, parseo de problemas JSON y puntuación |

---

## Configuración del proyecto

### Requisitos

- Android Studio Hedgehog o superior
- JDK 17
- Android SDK 35 (compileSdk)
- minSdk 24

### Firebase

El proyecto usa Firebase Auth y Firestore. Para configurarlo:

1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agregar una aplicación Android con el paquete `com.example.edukidsaplication`
3. Descargar `google-services.json` y colocarlo en `app/`
4. Habilitar **Authentication → Anonymous** y **Cloud Firestore**

### Clonar y ejecutar

```bash
git clone https://github.com/GarciaGael27/EduKidsAplication.git
cd EduKidsAplication
# Agregar google-services.json en app/
# Abrir con Android Studio y ejecutar
```

---

## Soporte offline

La aplicación funciona completamente sin conexión a internet:

- Los datos del usuario y el progreso se almacenan localmente en Room
- Firebase se usa como respaldo cuando hay conexión disponible
- La sesión del usuario se persiste en SharedPreferences

---

## Pruebas

```bash
# Tests unitarios
./gradlew test

# Tests de UI (Espresso)
./gradlew connectedAndroidTest
```

---

## Licencia

Este proyecto es de uso educativo y personal.
