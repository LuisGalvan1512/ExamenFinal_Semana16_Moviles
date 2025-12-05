package com.ejemplo.galvanmorales.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ejemplo.galvanmorales.model.Event
import com.ejemplo.galvanmorales.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEventScreen(
    eventId: String?,
    onNavigateBack: () -> Unit,
    viewModel: EventViewModel
) {
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()

    val eventToEdit = events.find { it.id == eventId }

    var titulo by remember { mutableStateOf(eventToEdit?.titulo ?: "") }
    var fecha by remember { mutableStateOf(eventToEdit?.fecha ?: "") }
    var descripcion by remember { mutableStateOf(eventToEdit?.descripcion ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    val isEditing = eventId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Evento" else "Agregar Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título del evento") },
                placeholder = { Text("Ej: Examen Final") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha") },
                placeholder = { Text("Ej: 2025-06-30") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                placeholder = { Text("Detalles del evento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Button(
                onClick = {
                    if (titulo.isEmpty() || fecha.isEmpty()) {
                        Toast.makeText(context, "Título y fecha son obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    if (isEditing) {
                        val eventActualizado = Event(
                            id = eventId!!,
                            titulo = titulo,
                            fecha = fecha,
                            descripcion = descripcion,
                            userId = eventToEdit?.userId ?: ""
                        )
                        viewModel.updateEvent(
                            event = eventActualizado,
                            onSuccess = {
                                Toast.makeText(context, "Evento actualizado", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            },
                            onError = {
                                isLoading = false
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        val nuevoEvent = Event(
                            titulo = titulo,
                            fecha = fecha,
                            descripcion = descripcion
                        )
                        viewModel.addEvent(
                            event = nuevoEvent,
                            onSuccess = {
                                Toast.makeText(context, "Evento agregado", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            },
                            onError = {
                                isLoading = false
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Guardando..." else "Guardar")
            }
        }
    }
}
