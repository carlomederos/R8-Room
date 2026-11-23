package com.carlo_mederos.room.data

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio: abstrae el origen de datos para la capa de UI.
 */
class TareaRepository(private val dao: TareaDao) {
    fun obtenerTareas(filtro: String): Flow<List<Tarea>> =
        if (filtro.isBlank()) dao.obtenerTodas() else dao.buscar(filtro.trim())

    suspend fun guardar(tarea: Tarea) {
        if (tarea.id == 0) dao.insertar(tarea) else dao.actualizar(tarea)
    }

    suspend fun restaurar(tarea: Tarea) { dao.insertar(tarea) }
    suspend fun eliminar(tarea: Tarea) = dao.eliminar(tarea)
    suspend fun eliminarCompletadas() = dao.eliminarCompletadas()
}
