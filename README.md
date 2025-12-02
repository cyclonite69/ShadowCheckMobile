# ShadowCheckMobile

**Network Security & Surveillance Detection App**

A powerful Android application for detecting and analyzing WiFi networks, Bluetooth devices, BLE beacons, and cellular towers with advanced threat detection capabilities.

## Features

- 📡 **Network Scanning**: WiFi, Bluetooth, BLE, and Cellular tower detection
- 🛡️ **Threat Detection**: Surveillance and security threat monitoring
- 🗺️ **Mapping**: Visualize networks on interactive maps with Mapbox
- 📊 **Statistics**: Comprehensive network analytics and insights
- 🔐 **Security**: Encrypted local storage and secure API management
- 📤 **Export**: CSV, JSON, and KML format support
- 🌐 **WiGLE Integration**: Upload and query wardriving data
- 📱 **Modern UI**: Material 3 design with Jetpack Compose
- 🎯 **AR View**: Augmented reality network visualization

## Quick Start

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Gradle 8.2+

### Build & Run

```bash
# Clone the repository
git clone https://github.com/yourusername/ShadowCheckMobile.git
cd ShadowCheckMobile

# Build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Open in Android Studio
1. Open Android Studio
2. **File → Open**
3. Select the project directory
4. Wait for Gradle sync
5. Run on device or emulator

## Technology Stack

- **Language**: Kotlin + Java
- **UI**: Jetpack Compose (Material 3)
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow
- **Maps**: Mapbox SDK
- **Security**: AndroidX Security
- **Image Loading**: Coil
- **Architecture**: MVVM

## Project Structure

```
app/src/main/
├── kotlin/com/shadowcheck/mobile/
│   ├── MainActivity.kt         # Main entry point
│   ├── MainViewModel.kt        # State management
│   ├── data/                   # Database entities & DAOs
│   ├── models/                 # Data models & filters
│   ├── scanner/                # Background scanning service
│   └── ui/                     # Compose UI components
├── res/                        # Resources
├── assets/                     # OUI databases
└── AndroidManifest.xml
```

## Database Schema

13 entities tracking:
- WiFi networks
- Bluetooth devices
- BLE devices
- Cellular towers
- Sensor readings
- Hardware metadata
- Radio manufacturers
- Geofences
- Network notes
- Device tags
- API tokens & usage
- Media attachments

## Permissions

- Location (fine & coarse)
- WiFi state & scanning
- Bluetooth & BLE
- Camera (for AR features)
- Storage (for export)
- Foreground service

## Documentation

- [Development Guide](docs/DEVELOPMENT.md) - Setup and development workflow
- [Features](docs/FEATURES.md) - Complete feature list
- [Quick Start](docs/QUICK_START.md) - Getting started guide
- [Project Structure](docs/PROJECT_STRUCTURE.md) - Directory layout
- [Build Guide](docs/BUILD_FIX_GUIDE.md) - Build troubleshooting

## Contributing

Contributions are welcome! Please read the development guide before submitting pull requests.

## License

[Your License Here]

## Acknowledgments

- Recovered and reconstructed from APK using jadx
- Built with modern Android development best practices
- Database layer written in clean Kotlin

---

**Built with ❤️ for network security enthusiasts**
