# Home Assistant Android App

Eine native Android-App zur Steuerung deiner Home Assistant Installation.

## Features

- ✅ Anzeige aller Lichter, Schalter und Sensoren
- ✅ Ein-/Ausschalten von Lichtern und Schaltern
- ✅ Live-Anzeige von Sensorwerten (z.B. Temperatur)
- ✅ Automatische Aktualisierung alle 5 Sekunden
- ✅ Material Design 3 UI
- ✅ Dark/Light Theme Unterstützung
- ✅ Sichere Speicherung der Zugangsdaten

## Installation

### Voraussetzungen

- Android Studio (neueste Version)
- Android SDK API Level 26 oder höher
- Ein Home Assistant Server mit aktivierter REST API

### Einrichtung

1. **Projekt in Android Studio öffnen:**
   - Öffne Android Studio
   - Wähle "Open" und navigiere zum Projektordner
   - Warte, bis Gradle fertig synchronisiert ist

2. **App auf Gerät/Emulator installieren:**
   - Verbinde dein Android-Gerät oder starte einen Emulator
   - Klicke auf den "Run"-Button (grüner Pfeil) in Android Studio
   - Die App wird kompiliert und installiert

3. **Home Assistant konfigurieren:**
   - Beim ersten Start öffnet sich automatisch der Einstellungsbildschirm
   - Gib die URL deines Home Assistant Servers ein (z.B. `https://homeassistant.local:8123`)
   - Erstelle ein Long-Lived Access Token in Home Assistant:
     1. Öffne Home Assistant im Browser
     2. Klicke auf dein Profil (unten links)
     3. Scrolle zu "Long-Lived Access Tokens"
     4. Klicke auf "Create Token"
     5. Gib einen Namen ein (z.B. "Android App")
     6. Kopiere das generierte Token
   - Füge das Token in der App ein
   - Klicke auf "Speichern"

## Verwendung

### Hauptbildschirm

Der Hauptbildschirm zeigt alle verfügbaren Entitäten, gruppiert nach Typ:

- **Lichter**: Anzeige mit Schalter zum Ein-/Ausschalten
- **Schalter**: Steuerung verschiedener Geräte
- **Sensoren**: Nur-Lese Anzeige von Werten (Temperatur, Luftfeuchtigkeit, etc.)

### Funktionen

- **Schalten**: Tippe auf eine Karte oder den Schalter, um Lichter/Schalter zu steuern
- **Aktualisieren**: Ziehe nach unten oder tippe auf das Refresh-Icon
- **Verbindungsstatus**: Oben siehst du, ob die Verbindung aktiv ist

## Anpassung

### Eigene Entity-IDs verwenden

Die App lädt automatisch alle verfügbaren Entitäten. Du kannst in Home Assistant folgende Entity-IDs verwenden:

- `light.aussenlicht` - Außenlicht
- `sensor.aussentemperatur` - Außentemperatur
- `switch.*` - Beliebige Schalter

### UI anpassen

Die UI verwendet Material Design 3 und passt sich automatisch an:
- System-Theme (Hell/Dunkel)
- Dynamic Colors (Android 12+)
- Farbschema kann in `Theme.kt` angepasst werden

## Technische Details

### Architektur

- **MVVM Pattern**: Model-View-ViewModel Architektur
- **Jetpack Compose**: Moderne deklarative UI
- **Kotlin Coroutines**: Asynchrone Operationen
- **Ktor Client**: HTTP-Kommunikation mit Home Assistant
- **DataStore**: Sichere Speicherung der Einstellungen

### Verwendete Libraries

- Jetpack Compose (UI)
- Material 3 (Design)
- Ktor (Networking)
- Kotlinx Serialization (JSON)
- DataStore (Settings)
- ViewModel & LiveData

### API Endpoints

Die App nutzt folgende Home Assistant REST API Endpoints:

- `GET /api/states` - Alle Entitäten abrufen
- `GET /api/states/{entity_id}` - Einzelne Entität abrufen
- `POST /api/services/{domain}/{service}` - Service aufrufen (turn_on, turn_off, toggle)

## Fehlerbehebung

### Verbindungsprobleme

1. **Überprüfe die URL**: Stelle sicher, dass die URL korrekt ist (inkl. `http://` oder `https://`)
2. **Netzwerk**: Stelle sicher, dass dein Smartphone im gleichen Netzwerk ist (oder VPN/externe URL nutzen)
3. **Token**: Überprüfe, ob das Access Token noch gültig ist
4. **SSL**: Bei selbst-signierten Zertifikaten musst du evtl. `usesCleartextTraffic` aktivieren oder das Zertifikat vertrauen

### App stürzt ab

1. Überprüfe die Logcat-Ausgabe in Android Studio
2. Stelle sicher, dass alle Dependencies korrekt geladen wurden
3. Führe "Build > Clean Project" und dann "Build > Rebuild Project" aus

## Erweiterungsmöglichkeiten

Die App kann einfach erweitert werden um:

- 📊 Graphen für Sensor-Historien
- 🎨 Farbsteuerung für RGB-Lichter
- 🔔 Push-Benachrichtigungen
- 🗺️ Räume und Bereiche
- 🎭 Szenen und Skripte
- 📱 Widgets für den Homescreen
- 🔊 Sprachsteuerung

## Lizenz

Dieses Projekt ist ein Beispielprojekt für den persönlichen Gebrauch.

## Support

Bei Fragen oder Problemen:
1. Überprüfe die Home Assistant Logs
2. Überprüfe die Android Logcat
3. Stelle sicher, dass die Home Assistant REST API aktiviert ist
