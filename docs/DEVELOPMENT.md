# Development Guide

## Project Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Gradle 8.2+

### Build Commands

```bash
# Clean build
./gradlew clean build

# Debug build
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run tests
./gradlew test
```

## Architecture

### MVVM Pattern
- **Model**: Room entities and DAOs
- **View**: Jetpack Compose UI
- **ViewModel**: State management with StateFlow

### Key Components

**Database Layer**
- 13 Room entities
- Flow-based reactive queries
- Automatic state updates

**Scanner Service**
- Background WiFi/Bluetooth/BLE/Cellular scanning
- Foreground service for continuous monitoring
- Location tracking integration

**UI Layer**
- Material 3 with Jetpack Compose
- Glassmorphic design elements
- Reactive state management

## Code Structure

```
app/src/main/kotlin/com/shadowcheck/mobile/
├── MainActivity.kt              # Main entry point
├── MainViewModel.kt             # State management
├── data/
│   ├── Entities.kt              # Room entities
│   ├── Daos.kt                  # Database access
│   └── ShadowCheckDatabase.kt   # Database singleton
├── models/
│   └── Filters.kt               # Filter models
├── scanner/
│   └── ScannerService.java      # Background scanning
└── ui/
    ├── components/              # Reusable UI components
    └── screens/                 # Screen composables
```

## Converting Java to Kotlin

Many files are decompiled Java. To convert:

1. Open file in Android Studio
2. **Code → Convert Java File to Kotlin File**
3. Review and clean up generated code
4. Fix any decompilation artifacts

## Testing

### Unit Tests
```kotlin
class MainViewModelTest {
    @Test
    fun `test state updates`() {
        val viewModel = MainViewModel(mockDatabase)
        viewModel.updateScanning(true)
        assertEquals(true, viewModel.uiState.value.isScanning)
    }
}
```

### UI Tests
```kotlin
@Test
fun `displays network count`() {
    composeTestRule.setContent {
        MainScreen(viewModel)
    }
    composeTestRule.onNodeWithText("WiFi: 5").assertExists()
}
```

## Common Issues

### Build Errors
- Ensure JDK 17 is configured
- Run `./gradlew --refresh-dependencies`
- Clean build: `./gradlew clean`

### Decompilation Artifacts
- Generic variable names (var1, var2)
- Lambda expressions may need cleanup
- Missing comments (lost during compilation)

## Best Practices

- Use Kotlin coroutines for async operations
- Observe database with Flow
- Keep ViewModels free of Android dependencies
- Use StateFlow for UI state
- Follow Material 3 design guidelines
