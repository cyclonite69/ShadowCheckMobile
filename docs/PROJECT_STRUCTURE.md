# ShadowCheckMobile - Reconstructed Project Structure

## Directory Structure
```
ShadowCheckMobile/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── kotlin/com/shadowcheck/mobile/
│           │   ├── MainActivity.java
│           │   ├── SplashActivity.java
│           │   ├── data/
│           │   │   ├── Entities.kt (NEW - Clean Kotlin)
│           │   │   ├── WifiNetwork.kt (NEW - Clean Kotlin)
│           │   │   ├── Daos.kt (NEW - Clean Kotlin)
│           │   │   └── ShadowCheckDatabase.kt (NEW - Clean Kotlin)
│           │   ├── models/
│           │   │   ├── WiFiFilters.java
│           │   │   ├── BluetoothFilters.java
│           │   │   └── CellularFilters.java
│           │   ├── scanner/
│           │   │   └── ScannerService.java
│           │   └── ui/
│           │       ├── components/
│           │       │   ├── FilterPanel.java
│           │       │   └── SelectableNetworkList.java
│           │       └── screens/
│           │           ├── StatsScreen.java
│           │           ├── details/
│           │           ├── finder/
│           │           ├── lists/
│           │           ├── maps/
│           │           ├── security/
│           │           └── settings/
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   ├── values/
│           │   └── xml/
│           ├── assets/
│           │   ├── oui.properties
│           │   ├── oui_manufacturers.csv
│           │   └── radio_manufacturers.csv
│           └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Reconstructed Files (Clean Kotlin)
- ✅ build.gradle.kts (app & root)
- ✅ settings.gradle.kts
- ✅ gradle.properties
- ✅ Entities.kt - All Room entities
- ✅ WifiNetwork.kt - WiFi network entity
- ✅ Daos.kt - All Room DAOs
- ✅ ShadowCheckDatabase.kt - Room database

## Decompiled Files (Java - Need Conversion)
- MainActivity.java
- SplashActivity.java
- ScannerService.java
- All UI components and screens
- Filter models
- Threat detection logic
- WiGLE integration
- Export/Import utilities

## Next Steps
1. Convert remaining Java files to Kotlin
2. Fix any decompilation artifacts
3. Add missing imports
4. Test compilation
5. Restore functionality

## Build Instructions
```bash
cd /home/cyclonite01/ShadowCheckMobile
./gradlew build
```

## Notes
- All resources (layouts, drawables, strings) are intact
- AndroidManifest.xml is preserved
- Assets (OUI databases, manufacturer lists) are included
- Native libraries are available for all architectures
