package com.carlo_mederos.room.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room: cada instancia representa una fila de la tabla "tareas".
 */
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "descripcion") val descripcion: String,
    @ColumnInfo(name = "prioridad") val prioridad: Int = 1, // 0 = baja, 1 = media, 2 = alta
    @ColumnInfo(name = "completada") val completada: Boolean = false,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long = System.currentTimeMillis()
)
