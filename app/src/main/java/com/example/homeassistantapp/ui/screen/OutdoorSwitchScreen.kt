package com.example.homeassistantapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.homeassistantapp.data.model.Entity
import com.example.homeassistantapp.ui.viewmodel.HomeAssistantUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutdoorSwitchScreen(
    uiState: HomeAssistantUiState,
    onToggleOutdoor: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Find outdoor switch
    val outdoorSwitch = uiState.entities
        .filterIsInstance<Entity.Switch>()
        .find {
            it.name.lowercase().contains("outdoor") ||
            it.id.lowercase().contains("outdoor") ||
            it.name.lowercase().contains("außen") ||
            it.id.lowercase().contains("aussen")
        }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Außenbeleuchtung") },
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading && uiState.entities.isEmpty() -> {
                    CircularProgressIndicator()
                }
                uiState.error != null && uiState.entities.isEmpty() -> {
                    Column(
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
                            text = uiState.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Erneut versuchen")
                        }
                    }
                }
                outdoorSwitch == null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Kein Outdoor Switch gefunden",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                else -> {
                    OutdoorSwitchCard(
                        switch = outdoorSwitch,
                        onToggle = { onToggleOutdoor(outdoorSwitch.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OutdoorSwitchCard(
    switch: Entity.Switch,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = if (switch.isOn)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WbTwilight,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = if (switch.isOn)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline
            )

            Text(
                text = switch.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (switch.isOn) "Eingeschaltet" else "Ausgeschaltet",
                style = MaterialTheme.typography.titleLarge,
                color = if (switch.isOn)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline
            )

            Switch(
                checked = switch.isOn,
                onCheckedChange = { onToggle() },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
