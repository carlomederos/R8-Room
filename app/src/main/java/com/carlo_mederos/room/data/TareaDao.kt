package com.carlo_mederos.room.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): define los métodos para acceder a la tabla "tareas".
 */
@Dao
interface TareaDao {

    @Query("SELECT * FROM tareas ORDER BY completada ASC, prioridad DESC, fecha_creacion DESC")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Query(
        "SELECT * FROM tareas WHERE titulo LIKE '%' || :texto || '%' " +
            "OR descripcion LIKE '%' || :texto || '%' " +
            "ORDER BY completada ASC, prioridad DESC, fecha_creacion DESC"
    )
    fun buscar(texto: String): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Tarea?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: Tarea): Long

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)

    @Query("DELETE FROM tareas WHERE completada = 1")
    suspend fun eliminarCompletadas()
}
