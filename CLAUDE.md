# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ShadowCheckMobile is a network security and surveillance detection Android app for wardriving, threat monitoring, and network analysis. It scans WiFi networks, Bluetooth/BLE devices, and cellular towers with advanced threat detection capabilities.

**Critical Context**: This project was recovered from an APK using jadx decompiler. While fully functional, the codebase contains decompilation artifacts and is undergoing architectural refactoring from legacy code to modern Clean Architecture with Hilt.

## Build Commands

```bash
# Clean build (recommended after dependency changes)
./gradlew clean build

# Debug build
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run specific test class
./gradlew test --tests com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCaseTest

# Run tests with logging
./gradlew test --info

# Refresh dependencies (if build issues occur)
./gradlew --refresh-dependencies clean build
```

### Build Requirements
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 (strictly enforced - JDK 11 will fail)
- Android SDK 34
- Gradle 8.2+
- Kotlin 2.0.0 (migrated from 1.9.22)

## Architecture

### Dual Architecture (In Transition)

The codebase currently has two architectural patterns coexisting:

**Legacy Architecture** (`com.shadowcheck.mobile`)
- Direct ViewModel-to-Database access
- Single `MainViewModel` handling all state
- Monolithic view models without dependency injection
- Original decompiled code structure

**New Clean Architecture** (`com.shadowcheck.mobile` with DI)
- Repository pattern with interfaces in `domain/repository/`
- Repository implementations in `data/repository/`
- Use cases in `domain/usecase/` for business logic
- Hilt dependency injection throughout
- Separate ViewModels per feature: `WifiViewModel`, `BluetoothViewModel`, `CellularViewModel`

**When adding new features**: Use the new Clean Architecture pattern with Hilt. The legacy code is being gradually refactored.

### Clean Architecture Layers

**Data Layer** (`data/`)
- `data/database/`: Room database with entities and DAOs
  - `AppDatabase`: Room database class
  - `dao/`: DAO interfaces with Flow-based queries
  - `model/`: Database entities (suffixed with `Entity`)
- `data/repository/`: Repository implementations
  - Implement interfaces from `domain/repository/`
  - Handle data mapping between entities and domain models

**Domain Layer** (`domain/`)
- `domain/model/`: Domain models (pure business objects)
  - `WifiNetwork`, `BluetoothDevice`, `CellularTower`
  - `ThreatDetection`, `SurveillanceDetector`, `UnifiedSighting`
- `domain/repository/`: Repository interfaces (contracts)
- `domain/usecase/`: Single-responsibility use cases
  - Each use case is a distinct business operation
  - Injected into ViewModels via Hilt

**Presentation Layer** (`presentation/`, `ui/`)
- `presentation/viewmodel/`: Feature-specific ViewModels with Hilt
  - Annotated with `@HiltViewModel`
  - Constructor injection of use cases
  - Expose `StateFlow` for UI state
- `ui/screens/`: Composable screens organized by feature
  - `details/`: Detail screens for individual networks/devices
  - `lists/`: List screens for WiFi, Bluetooth, Cellular
  - `maps/`: Map visualizations
  - `security/`: Threat detection and security screens
  - `settings/`: App configuration
- `ui/components/`: Reusable UI components

**Dependency Injection** (`di/`)
- `DatabaseModule`: Provides Room database and DAOs
- `RepositoryModule`: Binds repository interfaces to implementations
- `NetworkModule`: Provides Retrofit and API services
- `DispatchersModule`: Provides coroutine dispatchers

### Key Architectural Components

**Room Database Flow**
```kotlin
// DAOs expose Flow for reactive updates
interface WifiNetworkDao {
    @Query("SELECT * FROM wifi_networks")
    fun getAllNetworks(): Flow<List<WifiNetworkEntity>>
}

// Repositories transform entities to domain models
class WifiNetworkRepositoryImpl @Inject constructor(
    private val dao: WifiNetworkDao
) : WifiNetworkRepository {
    override fun getAllNetworks(): Flow<List<WifiNetwork>> =
        dao.getAllNetworks().map { entities ->
            entities.map { it.toDomainModel() }
        }
}

// Use cases contain business logic
class GetAllWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(): Flow<List<WifiNetwork>> =
        repository.getAllNetworks()
            .map { networks ->
                networks.filter { it.ssid.isNotBlank() }
                    .sortedByDescending { it.timestamp }
            }
}

// ViewModels consume use cases
@HiltViewModel
class WifiViewModel @Inject constructor(
    private val getAllWifiNetworks: GetAllWifiNetworksUseCase
) : ViewModel() {
    private val _networks = MutableStateFlow<List<WifiNetwork>>(emptyList())
    val networks: StateFlow<List<WifiNetwork>> = _networks.asStateFlow()

    init {
        getAllWifiNetworks()
            .onEach { _networks.value = it }
            .launchIn(viewModelScope)
    }
}
```

**Scanner Service** (`rebuilt/service/CompleteScannerService`)
- Foreground service for continuous background scanning
- Scans WiFi, Bluetooth, BLE, and cellular networks
- Writes scan results directly to Room database
- Configured in AndroidManifest with `foregroundServiceType="location"`

**Application Entry Points**
- `ShadowCheckApp.kt`: Application class annotated with `@HiltAndroidApp`
- `rebuilt/presentation/MainActivity.kt`: Main activity entry point
- Navigation handled via Jetpack Navigation Compose

## Dependency Injection with Hilt

### Adding a New Hilt Component

**1. Create Domain Model** (`domain/model/`)
```kotlin
data class MyFeature(
    val id: String,
    val name: String,
    val timestamp: Long
)
```

**2. Create Repository Interface** (`domain/repository/`)
```kotlin
interface MyFeatureRepository {
    fun getAllFeatures(): Flow<List<MyFeature>>
}
```

**3. Create Repository Implementation** (`data/repository/`)
```kotlin
class MyFeatureRepositoryImpl @Inject constructor(
    private val dao: MyFeatureDao
) : MyFeatureRepository {
    override fun getAllFeatures(): Flow<List<MyFeature>> =
        dao.getAll().map { entities -> entities.map { it.toDomainModel() } }
}
```

**4. Bind in RepositoryModule** (`di/RepositoryModule.kt`)
```kotlin
@Binds
abstract fun bindMyFeatureRepository(
    impl: MyFeatureRepositoryImpl
): MyFeatureRepository
```

**5. Create Use Case** (`domain/usecase/`)
```kotlin
@Singleton
class GetAllFeaturesUseCase @Inject constructor(
    private val repository: MyFeatureRepository
) {
    operator fun invoke(): Flow<List<MyFeature>> =
        repository.getAllFeatures()
}
```

**6. Create ViewModel** (`presentation/viewmodel/`)
```kotlin
@HiltViewModel
class MyFeatureViewModel @Inject constructor(
    private val getAllFeatures: GetAllFeaturesUseCase
) : ViewModel() {
    private val _features = MutableStateFlow<List<MyFeature>>(emptyList())
    val features: StateFlow<List<MyFeature>> = _features.asStateFlow()

    init {
        getAllFeatures()
            .onEach { _features.value = it }
            .launchIn(viewModelScope)
    }
}
```

**7. Use in Composable**
```kotlin
@Composable
fun MyFeatureScreen(viewModel: MyFeatureViewModel = hiltViewModel()) {
    val features by viewModel.features.collectAsState()
    // UI code
}
```

## Database Schema

**Primary Database**: `shadowcheck.db` (Room)

**Current Entities** (Clean Architecture):
- `WifiNetworkEntity`: WiFi network scans
- `BluetoothDeviceEntity`: Bluetooth device scans
- `CellularTowerEntity`: Cellular tower scans

**Legacy Entities** (being migrated):
- `WifiNetwork`, `BluetoothDevice`, `BleDevice`, `CellularTower`
- `SensorReading`, `HardwareMetadata`, `RadioManufacturer`
- `Geofence`, `NetworkNote`, `DeviceTag`
- `ApiToken`, `ApiUsage`, `MediaAttachment`

**Note**: When working with the database, check whether you're modifying legacy entities in `data/Entities.kt` or new entities in `data/database/model/`. Prefer the new structure.

## Key Dependencies

**Core**
- Kotlin 2.0.0 with Compose Compiler 1.5.11
- Jetpack Compose BOM 2024.01.00 (Material 3)
- Hilt 2.48 (using KAPT, not KSP)

**Database & Persistence**
- Room 2.6.1 with KAPT annotation processing
- AndroidX Security Crypto 1.1.0-alpha06

**Networking**
- Retrofit 2.9.0 + OkHttp 4.12.0
- Kotlin Serialization 1.6.2

**Maps**
- Mapbox SDK 11.0.0 (requires `MAPBOX_ACCESS_TOKEN` in AndroidManifest)
- Google Maps SDK 18.2.0 with Compose support

**Testing**
- JUnit 4.13.2
- MockK 1.13.8
- Coroutines Test 1.7.3

## Build Configuration Critical Notes

### Annotation Processing: KAPT vs KSP

**IMPORTANT**: This project uses KAPT for all annotation processing, NOT KSP.

```kotlin
// app/build.gradle.kts uses KAPT plugin
plugins {
    id("kotlin-kapt")  // NOT ksp
}

dependencies {
    kapt("androidx.room:room-compiler:$roomVersion")
    kapt("com.google.dagger:hilt-compiler:2.48")
}

// KAPT configuration in android block
android {
    kapt {
        correctErrorTypes = true
    }
}
```

Room and Hilt currently use KAPT. Do not attempt to migrate to KSP without extensive testing.

### Compiler Flags

```kotlin
kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs += listOf(
        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    )
}
```

Do not add `-Xskip-prerelease-check` - it's unnecessary with Kotlin 2.0.0.

### Kotlin Version Mismatch

- Root `build.gradle.kts` declares Kotlin 2.0.0
- Serialization plugin uses Kotlin 1.9.22
- This mismatch is known and works correctly
- Do not "fix" version mismatches without testing

## Testing Strategy

### Unit Tests
- Test use cases with MockK for repository mocking
- Test ViewModels with coroutine test dispatcher
- Test repository implementations with fake DAOs

### Device Testing Required
- WiFi scanning requires physical device WiFi hardware
- Bluetooth/BLE scanning requires Bluetooth adapter
- Cellular scanning requires phone modem
- Location tracking requires GPS
- AR features require camera

Emulator testing is limited to UI and database operations only.

## Common Pitfalls & Solutions

### Decompilation Artifacts
- Generic variable names (`var1`, `var2`) - rename when editing
- Unusual lambda formatting - reformat as needed
- Missing comments - add documentation for complex logic
- Duplicate classes - check both legacy and new packages

### Build Issues
- **Hilt not generating code**: Run `./gradlew clean build`
- **Room DAO not found**: Ensure KAPT is configured, not KSP
- **Compose compiler errors**: Verify Kotlin 2.0.0 with Compose Compiler 1.5.11
- **Java version errors**: Strictly use JDK 17, no other version

### Dual Package Structure
When adding features:
- **NEW code**: Use `com.shadowcheck.mobile` with Hilt DI
- **AVOID**: Creating new files in `com.shadowcheck.mobile.rebuilt` unless explicitly refactoring
- MainActivity is in `rebuilt/presentation/` but new screens go in `ui/screens/`

### API Keys Configuration
Required in `AndroidManifest.xml`:
```xml
<meta-data android:name="MAPBOX_ACCESS_TOKEN" android:value="your_token"/>
<meta-data android:name="com.google.android.geo.API_KEY" android:value="your_key"/>
```

WiGLE API keys are stored encrypted at runtime via `SecureApiKeyManager`.

## Performance Considerations

- Database queries use Flow for reactive updates - avoid blocking calls
- Scanner service runs in foreground to prevent Android from killing it
- Large network lists (36k+ entries) use Compose `LazyColumn` for virtualization
- Map markers are culled/clustered for performance with large datasets
- Export operations (CSV, JSON, KML) run in coroutines to avoid blocking UI
- Heavy operations use `Dispatchers.IO`, not `Dispatchers.Main`

## Security & Privacy

- Location data stored locally in encrypted SQLite database
- API keys encrypted with AndroidX Security Crypto
- No analytics or tracking (privacy-focused)
- Explicit permission grants required for location, WiFi, Bluetooth
- Foreground service notification required when scanning (Android requirement)

## Documentation

Additional docs in `docs/`:
- `DEVELOPMENT.md` - Development workflow
- `FEATURES.md` - Complete feature list
- `archive/` - Historical reconstruction notes
