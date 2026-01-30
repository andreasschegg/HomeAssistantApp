package com.example.homeassistantapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeassistantapp.data.model.Entity
import com.example.homeassistantapp.data.repository.HomeAssistantRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeAssistantUiState(
    val isLoading: Boolean = false,
    val entities: List<Entity> = emptyList(),
    val error: String? = null,
    val isConnected: Boolean = false
)

class HomeAssistantViewModel(
    private val repository: HomeAssistantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeAssistantUiState())
    val uiState: StateFlow<HomeAssistantUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        loadEntities()
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(5000) // Refresh every 5 seconds
                loadEntities(showLoading = false)
            }
        }
    }

    fun loadEntities(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            repository.getEntities().collect { result ->
                result.fold(
                    onSuccess = { entities ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            entities = entities,
                            isConnected = true,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Unbekannter Fehler",
                            isConnected = false
                        )
                    }
                )
            }
        }
    }

    fun toggleEntity(entityId: String) {
        viewModelScope.launch {
            repository.toggleEntity(entityId).fold(
                onSuccess = {
                    // Refresh the entity state after a short delay
                    delay(500)
                    loadEntities(showLoading = false)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = "Fehler beim Schalten: ${exception.message}"
                    )
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
