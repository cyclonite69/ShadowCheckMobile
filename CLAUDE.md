# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ShadowCheckMobile is a network security and surveillance detection Android app for wardriving, threat monitoring, and network analysis. It scans WiFi networks, Bluetooth/BLE devices, and cellular towers with advanced threat detection capabilities.

**Key Context**: This project was recovered from an APK using jadx decompiler. The codebase is fully functional but contains some decompilation artifacts.

## Build Commands

### Standard Build
```bash
# Clean build
./gradlew clean build

# Debug build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test

# Refresh dependencies (if build issues)
./gradlew --refresh-dependencies
```

### Requirements
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Gradle 8.2+
- Kotlin 1.9.22

## Architecture

### MVVM Pattern
The app follows a clean MVVM architecture:

**Model Layer** (`data/`)
- 13 Room entities tracking WiFi, Bluetooth, BLE, cellular, sensors, geofences, etc.
- DAOs with Flow-based reactive queries for automatic UI updates
- `ShadowCheckDatabase` singleton manages all database access

**ViewModel Layer** (`presentation/viewmodel/`)
- `MainViewModel`: Central state management with `MainUiState` data class
- Uses `StateFlow` for reactive UI updates
- Observes database through Flow and updates UI state automatically
- Manages scanning state, filters, map display, network selection

**View Layer** (`ui/`)
- Jetpack Compose with Material 3 design
- Glassmorphic UI components with dark theme and cyan accent (#00BCD4)
- Screens organized by feature: `details/`, `finder/`, `lists/`, `maps/`, `security/`, `settings/`

### Key Architectural Points

**Scanner Service** (`service/CompleteScannerService`)
- Foreground service for continuous background scanning
- Scans WiFi, Bluetooth, BLE, and cellular networks
- Integrates with location tracking
- Configured in AndroidManifest with `foregroundServiceType="location"`

**Database Schema**
13 entities across network types:
- `WifiNetwork`, `BluetoothDevice`, `BleDevice`, `CellularTower`
- `SensorReading`, `HardwareMetadata`, `RadioManufacturer`
- `Geofence`, `NetworkNote`, `DeviceTag`
- `ApiToken`, `ApiUsage`, `MediaAttachment`

**State Management Flow**
1. Scanner service writes to Room database
2. DAOs expose Flow-based queries
3. MainViewModel observes these Flows
4. UI state updates trigger Compose recomposition

## Directory Structure

```
app/src/main/kotlin/com/shadowcheck/mobile/
├── data/                       # Database layer (Room)
│   ├── Entities.kt             # All 13 Room entities
│   ├── Daos.kt                 # All database access objects
│   ├── ShadowCheckDatabase.kt  # Database singleton
│   ├── WifiNetwork.kt          # WiFi network entity
│   └── EnrichmentTask.kt       # Background enrichment tasks
├── models/                     # Data models & filters
│   ├── WiFiFilters.kt
│   ├── BluetoothFilters.kt
│   └── CellularFilters.kt
├── presentation/               # ViewModels
│   └── viewmodel/
│       └── MainViewModel.kt    # Main app state management
├── ui/                         # Jetpack Compose UI
│   ├── components/             # Reusable components
│   │   ├── FilterPanel.kt
│   │   ├── SelectableNetworkList.kt
│   │   ├── Chip.kt
│   │   └── RainbowShimmer.kt
│   ├── screens/                # Feature screens
│   │   ├── details/            # Network detail views
│   │   ├── finder/             # Network finder (AR/compass)
│   │   ├── lists/              # Network list screens
│   │   ├── maps/               # Map screens
│   │   ├── security/           # Threat detection
│   │   ├── settings/           # App settings
│   │   ├── NetworkStats.kt
│   │   └── StatsScreen.kt
│   ├── AnimatedComponents.kt
│   ├── NavItem.kt
│   ├── NetworkCompass.kt
│   └── Sidebar.kt
├── service/                    # Background services
│   └── CompleteScannerService  # Main scanning service
├── network/                    # API & networking
│   └── dto/                    # Data transfer objects
├── utils/                      # Utilities
│   ├── Animations.kt
│   ├── DeduplicationUtil.kt
│   ├── ExportUtils.kt          # CSV, JSON, KML export
│   ├── GlassmorphicComponents.kt
│   └── SecureApiKeyManager.kt  # Encrypted key storage
├── rebuilt/presentation/       # Rebuilt/refactored code
│   └── MainActivity.kt         # Main entry point
├── ARNetworkView.kt            # AR network visualization
├── ChannelGraph.kt             # WiFi channel graph
└── UnifiedDetailScreen.kt      # Unified detail view
```

## Key Dependencies

**UI & Compose**
- Jetpack Compose (Material 3) with BOM 2024.01.00
- Navigation Compose for screen navigation
- Material Icons Extended

**Database**
- Room 2.6.1 with KSP annotation processing
- Flow-based reactive queries

**Networking**
- Retrofit 2.9.0 + OkHttp 4.12.0 for WiGLE API integration
- Kotlin Serialization for JSON

**Maps**
- Mapbox SDK 11.0.0 (primary map provider)
- Google Maps SDK with Compose support (secondary)

**Location & Sensors**
- Google Play Services Location 21.1.0
- CameraX 1.3.1 for AR features

**Security**
- AndroidX Security Crypto 1.1.0-alpha06 for encrypted storage

## Build Configuration Notes

The `app/build.gradle.kts` includes these important compiler flags:
```kotlin
kotlinOptions {
    freeCompilerArgs += listOf(
        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        "-opt-in=kotlin.RequiresOptIn"
    )
}
```

These suppress opt-in warnings for experimental Material 3 APIs used throughout the UI.

## Common Tasks

### Adding a New Screen
1. Create composable in `ui/screens/{feature}/`
2. Add navigation route in MainActivity
3. Add NavItem entry in Sidebar if needed
4. Update MainViewModel if new state is needed

### Working with Database
```kotlin
// Observe data reactively
viewModelScope.launch {
    database.wifiNetworkDao().getAllFlow().collect { networks ->
        _uiState.update { it.copy(wifiNetworks = networks) }
    }
}

// Insert/update data
viewModelScope.launch {
    database.wifiNetworkDao().insert(network)
}
```

### Testing on Device
1. Enable Developer Options on Android device
2. Enable USB Debugging
3. Connect device via USB
4. Run `./gradlew installDebug`
5. Check logcat: `adb logcat | grep ShadowCheck`

## Known Issues & Quirks

### Decompilation Artifacts
- Some variable names are generic (`var1`, `var2`) - rename for clarity when editing
- Lambda expressions may have unusual formatting - reformat as needed
- Comments were lost during compilation - add new ones where logic is complex

### API Keys
The app requires API keys for full functionality:
- Mapbox: Set in `AndroidManifest.xml` meta-data `MAPBOX_ACCESS_TOKEN`
- Google Maps: Set in `AndroidManifest.xml` meta-data `com.google.android.geo.API_KEY`
- WiGLE: Stored encrypted in app via `SecureApiKeyManager`

### Package Structure
Note the dual structure: `com.shadowcheck.mobile` (original) and `com.shadowcheck.mobile.rebuilt` (refactored). The rebuilt package contains the MainActivity entry point. When adding new features, follow the original package structure unless explicitly refactoring.

## Testing Strategy

The app relies on real device testing due to hardware requirements:
- WiFi scanning requires device WiFi hardware
- Bluetooth/BLE scanning requires Bluetooth hardware
- Cellular scanning requires phone modem
- Location tracking requires GPS
- AR features require camera

Emulator testing is limited to UI and database operations only.

## Performance Considerations

- Database queries use Flow for reactive updates - avoid blocking calls
- Scanner service runs in foreground to prevent Android from killing it
- Large network lists (36k+ WiFi networks) use lazy lists in Compose
- Map markers are virtualized for performance with large datasets
- Export operations run in coroutines to avoid blocking UI

## WiGLE Integration

The app integrates with WiGLE.net for wardriving data:
- Upload scanned networks to WiGLE database
- Query WiGLE database for network info
- API tokens stored encrypted in Room database
- Rate limiting handled automatically

## Security & Privacy

- Location data stored locally in encrypted SQLite database
- API keys encrypted with AndroidX Security Crypto
- No analytics or tracking (privacy-focused design)
- Requires explicit permission grants for location, WiFi, Bluetooth
- Foreground service notification required when scanning

## Documentation

Additional documentation in `docs/`:
- `DEVELOPMENT.md` - Development workflow
- `PROJECT_STRUCTURE.md` - Detailed directory layout
- `FEATURES.md` - Complete feature list
- `QUICK_START.md` - Getting started guide
- `BUILD_FIX_GUIDE.md` - Build troubleshooting
- `APP_ANALYSIS.md` - UI/UX analysis
