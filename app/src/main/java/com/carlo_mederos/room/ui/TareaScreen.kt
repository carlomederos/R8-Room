package com.carlo_mederos.room.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlo_mederos.room.data.Tarea
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val nombresPrioridad = listOf("Baja", "Media", "Alta")
private val coloresPrioridad = listOf(Color(0xFF4CAF50), Color(0xFFFFA000), Color(0xFFE53935))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaScreen(viewModel: TareaViewModel = viewModel()) {
    val tareas by viewModel.tareas.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()

    var tareaEditando by remember { mutableStateOf<Tarea?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val pendientes = tareas.count { !it.completada }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mis tareas", fontWeight = FontWeight.Bold)
                        Text(
                            "$pendientes pendientes · ${tareas.size} en total",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    if (tareas.any { it.completada }) {
                        IconButton(onClick = { viewModel.eliminarCompletadas() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Eliminar completadas")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                tareaEditando = null
                mostrarDialogo = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = viewModel::onBusquedaChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar tareas…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onBusquedaChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true
            )

            if (tareas.isEmpty()) {
                EstadoVacio(hayBusqueda = busqueda.isNotBlank())
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tareas, key = { it.id }) { tarea ->
                        TareaItem(
                            tarea = tarea,
                            modifier = Modifier.animateItem(),
                            onToggle = { viewModel.alternarCompletada(tarea) },
                            onClick = {
                                tareaEditando = tarea
                                mostrarDialogo = true
                            },
                            onEliminar = {
                                viewModel.eliminar(tarea)
                                scope.launch {
                                    val r = snackbarHostState.showSnackbar(
                                        message = "Tarea \"${tarea.titulo}\" eliminada",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (r == SnackbarResult.ActionPerformed) viewModel.restaurar(tarea)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        TareaDialog(
            tarea = tareaEditando,
            onDismiss = { mostrarDialogo = false },
            onGuardar = {
                viewModel.guardar(it)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun EstadoVacio(hayBusqueda: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.padding(4.dp))
            Text(
                if (hayBusqueda) "Sin resultados" else "Aún no hay tareas.\nToca + para agregar una.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun TareaItem(
    tarea: Tarea,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    val fondo by animateColorAsState(
        if (tarea.completada) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface,
        label = "fondo"
    )
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = fondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = tarea.completada, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = coloresPrioridad[tarea.prioridad],
                        modifier = Modifier.size(10.dp)
                    ) {}
                    Spacer(Modifier.width(8.dp))
                    Text(
                        tarea.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                    )
                }
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${nombresPrioridad[tarea.prioridad]} · ${formatearFecha(tarea.fechaCreacion)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun TareaDialog(
    tarea: Tarea?,
    onDismiss: () -> Unit,
    onGuardar: (Tarea) -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(tarea?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(tarea?.descripcion ?: "") }
    var prioridad by rememberSaveable { mutableIntStateOf(tarea?.prioridad ?: 1) }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tarea == null) "Nueva tarea" else "Editar tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it; error = false },
                    label = { Text("Título *") },
                    isError = error,
                    supportingText = if (error) {
                        { Text("El título es obligatorio") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Prioridad", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    nombresPrioridad.forEachIndexed { i, nombre ->
                        FilterChip(
                            selected = prioridad == i,
                            onClick = { prioridad = i },
                            label = { Text(nombre) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (titulo.isBlank()) {
                    error = true
                } else {
                    val base = tarea ?: Tarea(titulo = "", descripcion = "")
                    onGuardar(
                        base.copy(
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim(),
                            prioridad = prioridad
                        )
                    )
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun formatearFecha(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(millis))
