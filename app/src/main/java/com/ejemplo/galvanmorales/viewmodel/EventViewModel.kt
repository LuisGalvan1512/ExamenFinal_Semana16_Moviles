package com.ejemplo.galvanmorales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.galvanmorales.model.Event
import com.ejemplo.galvanmorales.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val repository = EventRepository()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadEvents() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getEvents().collect { listaEvents ->
                    _events.value = listaEvents
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar eventos: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun addEvent(event: Event, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val success = repository.addEvent(event)
                _isLoading.value = false

                if (success) {
                    onSuccess()
                } else {
                    onError("No se pudo agregar el evento")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                onError("Error: ${e.message}")
            }
        }
    }

    fun updateEvent(event: Event, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val success = repository.updateEvent(event)
                _isLoading.value = false

                if (success) {
                    onSuccess()
                } else {
                    onError("No se pudo actualizar el evento")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                onError("Error: ${e.message}")
            }
        }
    }

    fun deleteEvent(eventId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val success = repository.deleteEvent(eventId)
                _isLoading.value = false

                if (success) {
                    onSuccess()
                } else {
                    onError("No se pudo eliminar el evento")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                onError("Error: ${e.message}")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearData() {
        _events.value = emptyList()
    }
}
