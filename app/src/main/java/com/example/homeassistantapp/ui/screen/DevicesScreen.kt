package com.example.homeassistantapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.homeassistantapp.data.model.Entity
import com.example.homeassistantapp.ui.viewmodel.HomeAssistantUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    uiState: HomeAssistantUiState,
    onToggleEntity: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter out outdoor switch
    fun isOutdoorSwitch(entity: Entity): Boolean {
        val name = when (entity) {
            is Entity.Switch -> entity.name
            is Entity.Light -> entity.name
            is Entity.Sensor -> entity.name
        }.lowercase()
        val id = when (entity) {
            is Entity.Switch -> entity.id
            is Entity.Light -> entity.id
            is Entity.Sensor -> entity.id
        }.lowercase()
        return name.contains("outdoor") || id.contains("outdoor") ||
               name.contains("außen") || id.contains("aussen")
    }

    val filteredEntities = uiState.entities.filterNot { isOutdoorSwitch(it) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Geräte") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Aktualisieren")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && filteredEntities.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null && filteredEntities.isEmpty() -> {
                    ErrorView(
                        error = uiState.error,
                        onRetry = onRefresh,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Connection status
                        item {
                            ConnectionStatusCard(isConnected = uiState.isConnected)
                        }

                        // Error message if any
                        if (uiState.error != null) {
                            item {
                                ErrorBanner(error = uiState.error)
                            }
                        }

                        // Group entities by type (excluding outdoor)
                        val lights = filteredEntities.filterIsInstance<Entity.Light>()
                        val sensors = filteredEntities.filterIsInstance<Entity.Sensor>()
                        val switches = filteredEntities.filterIsInstance<Entity.Switch>()

                        // Lights section
                        if (lights.isNotEmpty()) {
                            item {
                                SectionHeader(title = "Lichter", icon = Icons.Default.Lightbulb)
                            }
                            items(lights) { light ->
                                LightCard(
                                    light = light,
                                    onToggle = { onToggleEntity(light.id) }
                                )
                            }
                        }

                        // Switches section
                        if (switches.isNotEmpty()) {
                            item {
                                SectionHeader(title = "Schalter", icon = Icons.Default.ToggleOn)
                            }
                            items(switches) { switch ->
                                SwitchCard(
                                    switch = switch,
                                    onToggle = { onToggleEntity(switch.id) }
                                )
                            }
                        }

                        // Sensors section
                        if (sensors.isNotEmpty()) {
                            item {
                                SectionHeader(title = "Sensoren", icon = Icons.Default.Thermostat)
                            }
                            items(sensors) { sensor ->
                                SensorCard(sensor = sensor)
                            }
                        }
                    }
                }
            }
        }
    }
}
