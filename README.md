# R8 – Guardado de datos con Room

App de Android en **Jetpack Compose** que guarda tareas de forma local usando la librería **Room**.

## Componentes principales de Room
| Componente | Archivo | Descripción |
|---|---|---|
| **Entidad** | `data/Tarea.kt` | Tabla `tareas` (id, título, descripción, prioridad, completada, fecha de creación). |
| **DAO** | `data/TareaDao.kt` | Consultas: listar (`Flow`), buscar, insertar, actualizar, eliminar, eliminar completadas. |
| **Base de datos** | `data/AppDatabase.kt` | `@Database` singleton creada con `Room.databaseBuilder`. |

Además: `TareaRepository` (capa de datos) y `TareaViewModel` (expone un `StateFlow` reactivo a la UI).

## Interfaz (Compose + Material 3)
- Lista con `LazyColumn` (claves estables y animaciones) y barra superior que se contrae al desplazarse.
- Botón **+** para crear tareas; tocar una tarjeta la edita (diálogo con validación y prioridad).
- Checkbox para marcar como completada, botón para eliminar con **Deshacer** en el Snackbar.
- Búsqueda en tiempo real (consulta `LIKE` en Room) y acción para eliminar las completadas.
- Los datos persisten al cerrar y volver a abrir la app.

## Tecnologías
Kotlin · Jetpack Compose · Material 3 · Room 2.8.5 (KSP) · ViewModel · Coroutines/Flow

## Ejecutar
Abrir en Android Studio y ejecutar en un emulador o dispositivo (minSdk 24), o:
```
./gradlew installDebug
```
