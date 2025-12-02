# Build Fix Guide

## Problem
Laptop crashed during build, Android Studio won't start, project has Kotlin compilation errors.

## Root Cause
Multiple Kotlin files have:
- Missing required parameters in function calls
- Unresolved references (missing imports)
- Experimental API usage without opt-in annotations
- Missing Google Maps Compose dependencies

## Files with Errors
1. MainActivity.kt - PARTIALLY FIXED
2. HeatmapScreen.kt - Unresolved: Circle, Gradient
3. NetworkListScreen.kt - Experimental API warnings
4. BluetoothListScreen.kt - Experimental API warnings
5. CellularListScreen.kt - Experimental API warnings
6. RogueAPScreen.kt - Experimental API warnings
7. NetworkMonitorService.kt - Unknown errors
8. ScannerService.kt - Unknown errors

## Quick Fix Option 1: Minimal Build

Add to app/build.gradle.kts:
```kotlin
android {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    // Add Google Maps Compose
    implementation("com.google.maps.android:maps-compose:2.15.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:android-maps-utils:3.8.0")
}
```

## Quick Fix Option 2: Comment Out Broken Screens

Temporarily disable problematic screens in MainActivity.kt:
- Comment out HeatmapScreen route
- Comment out problematic list screens
- Use placeholder Text() composables

## Android Studio Recovery

1. Kill any stuck processes:
```bash
pkill -9 java
pkill -9 studio
```

2. Clear Android Studio caches:
```bash
rm -rf ~/.cache/Google/AndroidStudio*
rm -rf ~/.local/share/Google/AndroidStudio*/caches
```

3. Clear Gradle caches:
```bash
cd /home/cyclonite01/ShadowCheckMobile
rm -rf .gradle/caches
rm -rf app/build
./gradlew --stop
```

4. Restart Android Studio:
```bash
studio &
```

## Memory Settings

Add to gradle.properties:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
```

## Build from Command Line

Test build without Android Studio:
```bash
cd /home/cyclonite01/ShadowCheckMobile
./gradlew clean
./gradlew assembleDebug --stacktrace
```

## Current Status
- Gradle daemons: STOPPED (cleaned up)
- Build artifacts: CLEANED
- Lock files: PRESENT (normal)
- Compilation: FAILING (Kotlin errors)

## Next Steps
1. Apply Quick Fix Option 1 (add dependencies + compiler args)
2. Try command-line build
3. If successful, open in Android Studio
4. Fix remaining errors one by one
