package com.carlo_mederos.room.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase de base de datos: punto de acceso principal a los datos persistidos.
 */
@Database(entities = [Tarea::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tareas_database"
                ).build().also { INSTANCE = it }
            }
    }
}
