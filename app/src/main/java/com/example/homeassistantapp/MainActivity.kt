package com.example.homeassistantapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homeassistantapp.data.api.HomeAssistantApi
import com.example.homeassistantapp.data.repository.HomeAssistantRepository
import com.example.homeassistantapp.ui.screen.DevicesScreen
import com.example.homeassistantapp.ui.screen.OutdoorSwitchScreen
import com.example.homeassistantapp.ui.screen.SettingsContent
import com.example.homeassistantapp.ui.theme.HomeAssistantAppTheme
import com.example.homeassistantapp.ui.viewmodel.HomeAssistantViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    private val urlKey = stringPreferencesKey("home_assistant_url")
    private val tokenKey = stringPreferencesKey("home_assistant_token")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeAssistantAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    private fun MainScreen() {
        var selectedTab by remember { mutableIntStateOf(0) }
        var url by remember { mutableStateOf("") }
        var token by remember { mutableStateOf("") }
        var isConfigured by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(true) }
        var configVersion by remember { mutableIntStateOf(0) }

        val scope = rememberCoroutineScope()

        // Load settings on start
        LaunchedEffect(Unit) {
            val settings = dataStore.data.first()
            url = settings[urlKey] ?: ""
            token = settings[tokenKey] ?: ""
            isConfigured = url.isNotEmpty() && token.isNotEmpty()
            isLoading = false

            if (!isConfigured) {
                selectedTab = 2
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            selected = selectedTab == 0,
                            onClick = { if (isConfigured) selectedTab = 0 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Devices, contentDescription = "Geräte") },
                            label = { Text("Geräte") },
                            selected = selectedTab == 1,
                            onClick = { if (isConfigured) selectedTab = 1 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Einstellungen") },
                            label = { Text("Einstellungen") },
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 }
                        )
                    }
                }
            ) { paddingValues ->
                when (selectedTab) {
                    0, 1 -> {
                        if (isConfigured) {
                            HomeContent(
                                url = url,
                                token = token,
                                configVersion = configVersion,
                                selectedTab = selectedTab,
                                modifier = Modifier.padding(paddingValues)
                            )
                        }
                    }
                    2 -> {
                        SettingsContent(
                            currentUrl = url,
                            currentToken = token,
                            onSaveSettings = { newUrl, newToken ->
                                scope.launch {
                                    dataStore.edit { settings ->
                                        settings[urlKey] = newUrl
                                        settings[tokenKey] = newToken
                                    }
                                    url = newUrl
                                    token = newToken
                                    isConfigured = newUrl.isNotEmpty() && newToken.isNotEmpty()
                                    configVersion++
                                    if (isConfigured) {
                                        selectedTab = 0
                                    }
                                }
                            },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HomeContent(
        url: String,
        token: String,
        configVersion: Int,
        selectedTab: Int,
        modifier: Modifier = Modifier
    ) {
        val api = remember(url, token, configVersion) {
            HomeAssistantApi(url, token)
        }
        val repository = remember(api) {
            HomeAssistantRepository(api)
        }
        val viewModel: HomeAssistantViewModel = viewModel(
            key = "vm_$configVersion"
        ) {
            HomeAssistantViewModel(repository)
        }
        val uiState by viewModel.uiState.collectAsState()

        if (selectedTab == 0) {
            OutdoorSwitchScreen(
                uiState = uiState,
                onToggleOutdoor = { entityId -> viewModel.toggleEntity(entityId) },
                onRefresh = { viewModel.loadEntities() },
                modifier = modifier
            )
        } else {
            DevicesScreen(
                uiState = uiState,
                onToggleEntity = { entityId -> viewModel.toggleEntity(entityId) },
                onRefresh = { viewModel.loadEntities() },
                modifier = modifier
            )
        }
    }
}
