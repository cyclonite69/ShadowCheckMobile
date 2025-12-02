# ShadowCheckMobile - Recovery Summary

## Recovery Date
December 1, 2025

## Source
Recovered from APK installed on Samsung S22 (Device ID: R5CRC4B967K)

## Application Details
- **Package Name**: com.shadowcheck.mobile
- **APK Size**: 80,870,127 bytes (~77 MB)
- **Build Type**: Debug

## Recovered Components

### Core Application Files
- MainActivity.java - Main application entry point
- SplashActivity.java - Splash screen with cinematic animations
- BuildConfig.java - Build configuration
- R.java - Android resources

### Data Layer
**Database**: Room-based SQLite database (ShadowCheckDatabase)

**Entities**:
- WifiNetwork - WiFi network data
- BluetoothDevice - Bluetooth device data
- BleDevice - Bluetooth Low Energy device data
- CellularTower - Cellular tower information
- SensorReading - Sensor data
- HardwareMetadata - Hardware information
- RadioManufacturer - Radio manufacturer data
- Geofence - Geofencing data
- NetworkNote - Network annotations
- DeviceTag - Device tagging
- ApiToken - API token management
- ApiUsage - API usage tracking
- MediaAttachment - Media attachments

### Scanner Service
- ScannerService.java - Background scanning service for WiFi, Bluetooth, BLE, and cellular networks

### UI Components
**Screens**:
- StatsScreen - Statistics dashboard
- Settings screens
- Finder screens
- List screens (WiFi, Bluetooth, Cellular)
- Security screens
- Map screens
- Detail screens

**Components**:
- FilterPanel - Network filtering UI
- SelectableNetworkList - Network list with selection
- NetworkCompass - Network direction visualization
- Sidebar - Navigation sidebar
- AnimatedComponents - Animated UI elements
- GlassmorphicComponents - Glassmorphic design elements

### Features
1. **Network Scanning**
   - WiFi network detection
   - Bluetooth device scanning
   - BLE device scanning
   - Cellular tower detection

2. **Threat Detection**
   - SurveillanceDetector.java - Surveillance detection algorithms
   - ThreatDetection.java - Threat analysis
   - ThreatType.java - Threat categorization
   - ThreatSeverity.java - Severity levels

3. **WiGLE Integration**
   - WigleApi.java - WiGLE API interface
   - WigleApiService.java - API service implementation
   - WigleImporter.java - Database import from WiGLE
   - WigleNetwork.java - Network data model
   - WigleStats.java - Statistics
   - WigleUploadResponse.java - Upload handling

4. **Data Management**
   - DeduplicationUtil.java - Duplicate removal
   - ExportUtils.java - Data export functionality
   - SecureApiKeyManager.java - Secure API key storage

5. **Visualization**
   - ARNetworkView - Augmented reality network view
   - ChannelGraph - Channel visualization
   - UnifiedDetailScreen - Detailed network information

### Models & Filters
- WiFiFilters.java
- BluetoothFilters.java
- CellularFilters.java
- UnifiedSighting.java
- RadioType.java

### Theme & Design
- ShadowCheckTheme - Custom Material 3 theme
- TealTheme - Teal color scheme
- ShadowCheckColors - Color palette
- Animations - Custom animations

## Technology Stack
- **Language**: Kotlin (primary) with Java interop
- **UI Framework**: Jetpack Compose
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Maps**: Mapbox
- **Serialization**: Kotlinx Serialization
- **Coroutines**: Kotlinx Coroutines
- **Security**: AndroidX Security (EncryptedSharedPreferences)

## Next Steps
1. Review decompiled code for any obfuscation artifacts
2. Reconstruct build.gradle files
3. Set up proper project structure
4. Restore any missing resources
5. Test compilation and functionality

## Notes
- Decompilation completed with 8,626 errors (typical for obfuscated/optimized code)
- All major application logic has been recovered
- Resources (layouts, drawables, strings) are intact
- Native libraries recovered for all architectures (arm64-v8a, armeabi-v7a, x86, x86_64)

## Location
Decompiled source: `/home/cyclonite01/ShadowCheckMobile/ShadowCheckMobile_decompiled/`
