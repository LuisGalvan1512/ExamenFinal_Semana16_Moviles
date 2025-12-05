# EventPlanner

Alumno: **Galván Morales Luis Enrique**  
Seccion: **C24-A**

EventPlanner es una app Android hecha con **Kotlin**, **Jetpack Compose**, **Firebase Authentication** y **Cloud Firestore**, usando **MVVM**. Cada usuario gestiona sus propios eventos en la nube.

## Funcionalidades

- Registro e inicio de sesión con **email/contraseña** (Firebase Auth).
- Lista de **eventos del usuario actual** (no se comparten entre usuarios).
- Crear, ver, editar y eliminar eventos (CRUD completo).
- Campos del evento: **título**, **fecha**, **descripción (opcional)**.
- Datos en Firestore, colección `events`, filtrados por `userId`.
- Lectura en tiempo real con listener / Flow.

## Arquitectura

- **model**: `Event` (id, titulo, fecha, descripcion, userId).
- **repository**: `EventRepository` (CRUD sobre `events` en Firestore).
- **viewmodel**: `EventViewModel` (maneja estado y llama al repositorio).
- **ui/screens**:
  - `LoginScreen`, `RegisterScreen`
  - `EventsScreen` (lista)
  - `AddEditEventScreen` (formulario)
- **navigation**: `AppNavigation` con Navigation Compose.
- **MainActivity**: inicializa Firebase y muestra `AppNavigation()`.

## Firebase

- Proyecto Firebase con:
  - **Authentication**: Email/Password.
  - **Firestore**: colección `events`.
- Reglas: solo usuarios autenticados y cada uno solo accede a sus propios eventos (`userId`).

