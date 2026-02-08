# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Native Android app (Kotlin) for controlling a Home Assistant installation via its REST API. Uses Jetpack Compose for UI with Material Design 3.

## Build & Run Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run Android instrumentation tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Lint check
./gradlew lint
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Tech Stack

- **Kotlin** with JVM target 1.8
- **Jetpack Compose** with Material 3 (Compose BOM 2024.02)
- **Ktor Client** (2.3.7) for HTTP/REST communication
- **Kotlinx Serialization** for JSON parsing
- **DataStore Preferences** for persisting settings (URL + token)
- **Gradle 9.1** with Kotlin DSL (`build.gradle.kts`)
- **AGP 9.0.0**, Kotlin 2.2.10
- compileSdk/targetSdk 34, minSdk 26

## Architecture

MVVM pattern without dependency injection framework. Dependencies are manually wired in `MainActivity`.

### Data Flow

```
HomeAssistantApi  ->  HomeAssistantRepository  ->  HomeAssistantViewModel  ->  Compose Screens
   (Ktor HTTP)         (Flow<Result<>>)            (StateFlow<UiState>)       (Compose UI)
```

- **`HomeAssistantApi`** (`data/api/`) — Ktor HTTP client wrapping HA REST API (`/api/states`, `/api/services/{domain}/{service}`). Bearer token auth.
- **`HomeAssistantRepository`** (`data/repository/`) — Exposes `Flow<Result<List<Entity>>>`, delegates to API. Converts `HomeAssistantState` to domain `Entity` sealed class.
- **`HomeAssistantViewModel`** (`ui/viewmodel/`) — Holds `HomeAssistantUiState` as `StateFlow`. Auto-refreshes entities every 5 seconds.
- **`Entity`** sealed class (`data/model/`) — Three subtypes: `Light`, `Sensor`, `Switch`. Mapped from HA states via `HomeAssistantState.toEntity()` extension, filtering by entity ID prefix (`light.`, `sensor.`, `switch.`).

### Dependency Wiring (in MainActivity)

`HomeAssistantApi` and `HomeAssistantRepository` are created via `remember()` in `HomeContent()` composable, keyed on URL/token/configVersion. The ViewModel is created via `viewModel()` factory with a key to force recreation on config change.

### Navigation & Screens

Bottom navigation with 3 tabs, managed by `selectedTab` state in `MainActivity.MainScreen()`:

| Tab | Screen | Purpose |
|-----|--------|---------|
| 0 - Home | `OutdoorSwitchScreen` | Dedicated large toggle for outdoor switch (matches entities containing "outdoor"/"aussen") |
| 1 - Geraete | `DevicesScreen` | All entities grouped by type (lights, switches, sensors), excluding outdoor switch |
| 2 - Einstellungen | `SettingsContent` | Server URL + Access Token configuration |

`HomeAssistantScreen` contains shared reusable composables (`ConnectionStatusCard`, `ErrorBanner`, `SectionHeader`, `LightCard`, `SwitchCard`, `SensorCard`, `ErrorView`) used by both `OutdoorSwitchScreen` and `DevicesScreen`.

### Settings Persistence

URL and token stored via Jetpack DataStore Preferences (`Context.dataStore` extension in `MainActivity.kt`). On first launch without config, the Settings tab is shown automatically.

## Conventions

- UI text is in German (labels, error messages, button text)
- Code comments and commit messages in English
- Package: `com.example.homeassistantapp`
- `ExperimentalMaterial3Api` is globally opted in via `freeCompilerArgs` in `build.gradle.kts`
