# Schnellstart-Anleitung

## 1. Home Assistant Access Token erstellen

1. Öffne Home Assistant in deinem Browser
2. Klicke auf dein Profil (unten links in der Sidebar)
3. Scrolle nach unten zu **"Long-Lived Access Tokens"**
4. Klicke auf **"CREATE TOKEN"**
5. Gib einen Namen ein (z.B. "Android App")
6. Kopiere das angezeigte Token (du siehst es nur einmal!)

## 2. App in Android Studio öffnen

1. Starte Android Studio
2. Wähle **"Open"**
3. Navigiere zum `HomeAssistantApp` Ordner
4. Klicke auf **"OK"**
5. Warte, bis Gradle fertig synchronisiert ist (kann ein paar Minuten dauern)

## 3. App auf deinem Smartphone installieren

### Option A: Über USB (empfohlen)

1. Aktiviere **"Entwickleroptionen"** auf deinem Smartphone:
   - Gehe zu Einstellungen → Über das Telefon
   - Tippe 7x auf "Build-Nummer"
2. Aktiviere **"USB-Debugging"**:
   - Gehe zu Einstellungen → Entwickleroptionen
   - Aktiviere "USB-Debugging"
3. Verbinde dein Smartphone per USB mit dem Computer
4. Bestätige die USB-Debugging-Anfrage auf dem Smartphone
5. Klicke in Android Studio auf den grünen **"Run"**-Button (▶️)
6. Wähle dein Gerät aus der Liste

### Option B: Emulator

1. Klicke in Android Studio auf **"Device Manager"**
2. Klicke auf **"Create Device"**
3. Wähle ein Gerät (z.B. "Pixel 6") und klicke **"Next"**
4. Wähle ein System Image (z.B. "Tiramisu" API 33) und klicke **"Next"**
5. Klicke auf **"Finish"**
6. Klicke auf den grünen **"Run"**-Button

## 4. App konfigurieren

Beim ersten Start der App:

1. Gib die **URL** deines Home Assistant Servers ein
   - Lokal: `http://192.168.1.X:8123`
   - Extern: `https://deine-domain.de:8123`
   - Nabu Casa: `https://xyz.ui.nabu.casa`

2. Füge das **Access Token** ein (aus Schritt 1)

3. Klicke auf **"Speichern"**

## 5. Fertig! 🎉

Die App zeigt jetzt alle deine Entitäten:
- **Lichter** mit Schalter
- **Schalter** für Geräte
- **Sensoren** (Temperatur, etc.)

Die Daten werden automatisch alle 5 Sekunden aktualisiert.

## Tipps

- **Refresh**: Wische nach unten oder tippe auf das Refresh-Icon
- **Einstellungen**: Zum Ändern der Konfiguration musst du die App neu öffnen
- **Probleme?**: Schau in die vollständige README.md für Fehlerbehebung
