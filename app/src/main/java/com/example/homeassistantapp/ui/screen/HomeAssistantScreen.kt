package com.example.homeassistantapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
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
fun HomeAssistantScreen(
    uiState: HomeAssistantUiState,
    onToggleEntity: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Home Assistant") },
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
                uiState.isLoading && uiState.entities.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null && uiState.entities.isEmpty() -> {
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

                        // Group entities by type
                        val lights = uiState.entities.filterIsInstance<Entity.Light>()
                        val sensors = uiState.entities.filterIsInstance<Entity.Sensor>()
                        val switches = uiState.entities.filterIsInstance<Entity.Switch>()

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

@Composable
fun ConnectionStatusCard(isConnected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isConnected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = if (isConnected) "Verbunden" else "Nicht verbunden",
                style = MaterialTheme.typography.titleMedium,
                color = if (isConnected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun ErrorBanner(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun LightCard(light: Entity.Light, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = if (light.isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
                Column {
                    Text(
                        text = light.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (light.isOn) "An" else "Aus",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Switch(
                checked = light.isOn,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun SwitchCard(switch: Entity.Switch, onToggle: () -> Unit) {
    // Determine icon based on switch name/id
    val isOutdoorLight = switch.name.lowercase().contains("outdoor") ||
            switch.id.lowercase().contains("outdoor") ||
            switch.name.lowercase().contains("außen") ||
            switch.id.lowercase().contains("aussen")

    val icon = if (isOutdoorLight) {
        Icons.Default.WbTwilight
    } else {
        Icons.Default.Power
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (switch.isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
                Column {
                    Text(
                        text = switch.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (switch.isOn) "An" else "Aus",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Switch(
                checked = switch.isOn,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun SensorCard(sensor: Entity.Sensor) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    sensor.unit?.contains("°") == true -> Icons.Default.Thermostat
                    sensor.unit?.contains("%") == true -> Icons.Default.WaterDrop
                    else -> Icons.Default.Sensors
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sensor.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = sensor.id,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                text = "${sensor.state}${sensor.unit ?: ""}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ErrorView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = "Verbindungsfehler",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Erneut versuchen")
        }
    }
}
