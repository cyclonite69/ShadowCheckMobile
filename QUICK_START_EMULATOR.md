# Quick Start: Android Emulator

**Goal**: Create an Android Virtual Device and test ShadowCheckMobile

**Time**: ~10 minutes (first time setup)

---

## Option 1: Android Studio (Easiest) ⭐

### Step 1: Open Device Manager (30 seconds)

```
Android Studio → Tools → Device Manager
```

Or click the 📱 phone icon in the toolbar.

### Step 2: Create Device (2 minutes)

1. Click **"Create Device"** (+ button)

2. Select **Pixel 7** → Click **Next**

3. Select **UpsideDownCake** (API 34, Android 14)
   - Click **Download** if not installed
   - Wait for download to complete

4. Click **Next**

5. Configure:
   - **AVD Name**: `ShadowCheck_Test`
   - **RAM**: 4096 MB
   - Click **Finish**

### Step 3: Start Emulator (2-3 minutes)

1. Click **▶ Play** button next to your AVD
2. Wait for emulator to boot
3. You'll see the Android home screen

### Step 4: Install & Run App (30 seconds)

1. With emulator running, in Android Studio:
   - Select `ShadowCheck_Test` from device dropdown (top toolbar)
   - Click green **Run** button (▶)

2. Or from terminal:
   ```bash
   ./gradlew installDebug
   ```

**Done!** App should launch on emulator.

---

## Option 2: Command Line (Automated) 🚀

### One Command Setup:

```bash
./setup-emulator.sh
```

This script will:
- ✅ Check Android SDK
- ✅ Download system image (if needed)
- ✅ Create AVD with optimal settings
- ✅ Ask if you want to start emulator

### Manual Commands:

```bash
# 1. Install system image
sdkmanager "system-images;android-34;google_apis;x86_64"

# 2. Create AVD
avdmanager create avd \
  --name ShadowCheck_Test \
  --package "system-images;android-34;google_apis;x86_64" \
  --device "pixel_7"

# 3. Start emulator
emulator -avd ShadowCheck_Test -memory 4096 -gpu auto

# 4. Install app (in new terminal)
./gradlew installDebug
```

---

## What You Can Test on Emulator

### ✅ WORKS:
- ✅ UI Navigation
- ✅ Screen layouts
- ✅ Database operations
- ✅ Settings
- ✅ Map display (Google Maps)
- ✅ List views
- ✅ Filters
- ✅ Search functionality

### ❌ DOESN'T WORK:
- ❌ WiFi network scanning
- ❌ Bluetooth device scanning
- ❌ Cellular tower detection
- ❌ Real GPS (can simulate location)
- ❌ AR features (no real camera)

**For WiFi/Bluetooth testing → Use real Android device**

---

## Testing Checklist

Once emulator is running:

- [ ] App launches without crashes
- [ ] Navigate to WiFi list screen
- [ ] Navigate to Bluetooth list screen
- [ ] Navigate to Map screen
- [ ] Open Settings
- [ ] Test database (data persists after app restart)
- [ ] Check themes/dark mode
- [ ] Test search/filter functionality

---

## Grant Permissions

When app first launches, grant all permissions:
- Location (required)
- Camera (if using AR)
- Bluetooth (won't work but grant anyway)

**Or grant via command:**
```bash
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.ACCESS_FINE_LOCATION
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.ACCESS_COARSE_LOCATION
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.CAMERA
```

---

## Common Issues

### Emulator won't start

**Problem**: "The emulator process has terminated"

**Fix**:
```bash
# Check hardware acceleration
kvm-ok

# If not installed:
sudo apt install cpu-checker qemu-kvm
sudo usermod -aG kvm $USER
# Log out and back in
```

### Emulator is slow

**Fix 1**: Use software rendering
```bash
emulator -avd ShadowCheck_Test -gpu swiftshader_indirect
```

**Fix 2**: Disable animations
```bash
adb shell settings put global window_animation_scale 0.0
adb shell settings put global transition_animation_scale 0.0
adb shell settings put global animator_duration_scale 0.0
```

### App won't install

**Fix**:
```bash
# Uninstall old version
adb uninstall com.shadowcheck.mobile.rebuilt

# Rebuild and install
./gradlew clean installDebug
```

---

## Quick Commands

```bash
# List running devices
adb devices

# View logs
adb logcat | grep ShadowCheck

# Take screenshot
adb shell screencap /sdcard/screen.png
adb pull /sdcard/screen.png

# Clear app data
adb shell pm clear com.shadowcheck.mobile.rebuilt

# Restart emulator
adb reboot
```

---

## Next Steps

1. **Create emulator** (see above)
2. **Test UI features** on emulator
3. **Get a real Android device** for WiFi/Bluetooth testing
4. **Enable Developer Options** on real device:
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → System → Developer Options → Enable USB Debugging
5. **Connect device** via USB and run: `./gradlew installDebug`

---

## Full Documentation

For detailed instructions, see:
- **`docs/ANDROID_EMULATOR_SETUP.md`** - Complete emulator guide
- **`CLAUDE.md`** - Project architecture and build info
- **`CONTRIBUTING.md`** - Development workflow

---

**Ready to test!** 🚀

Start with the emulator for UI testing, then switch to a real device for full functionality.
