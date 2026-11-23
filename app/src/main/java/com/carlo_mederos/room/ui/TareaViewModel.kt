package com.carlo_mederos.room.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.carlo_mederos.room.data.AppDatabase
import com.carlo_mederos.room.data.Tarea
import com.carlo_mederos.room.data.TareaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TareaRepository(AppDatabase.getDatabase(application).tareaDao())

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tareas: StateFlow<List<Tarea>> = _busqueda
        .flatMapLatest { repository.obtenerTareas(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    fun guardar(tarea: Tarea) = viewModelScope.launch { repository.guardar(tarea) }

    fun alternarCompletada(tarea: Tarea) = viewModelScope.launch {
        repository.guardar(tarea.copy(completada = !tarea.completada))
    }

    fun eliminar(tarea: Tarea) = viewModelScope.launch { repository.eliminar(tarea) }

    fun restaurar(tarea: Tarea) = viewModelScope.launch { repository.restaurar(tarea) }

    fun eliminarCompletadas() = viewModelScope.launch { repository.eliminarCompletadas() }
}
