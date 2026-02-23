# Android Emulator Setup Guide

This guide walks you through creating an Android Virtual Device (AVD) for testing ShadowCheckMobile.

## Important Limitations

⚠️ **The emulator CANNOT test:**
- WiFi network scanning (requires physical WiFi hardware)
- Bluetooth device scanning (requires Bluetooth adapter)
- Cellular tower detection (requires modem)
- Real GPS location (can simulate, but not realistic)

✅ **The emulator CAN test:**
- UI/UX
- Navigation
- Database operations (Room)
- Compose screens
- Most app logic

**For full feature testing, use a real Android device.**

---

## Method 1: Using Android Studio (Recommended)

### Step 1: Open AVD Manager

1. **Open Android Studio**
2. Click **Tools → Device Manager** (or **AVD Manager** in older versions)
   - Alternatively: Click the 📱 phone icon in the toolbar
   - Or: `Ctrl+Shift+A` → type "Device Manager"

### Step 2: Create Virtual Device

1. Click **"Create Device"** button (the + icon)

2. **Select Hardware**:
   - **Category**: Phone
   - **Device**: Select one of these recommended options:
     - ✅ **Pixel 7** (Modern, good performance)
     - ✅ **Pixel 6** (Good balance)
     - ✅ **Pixel 5** (Lighter, faster on lower-end machines)
   - Click **Next**

3. **Select System Image**:
   - **Release Name**: Select **"UpsideDownCake"** (API 34, Android 14)
     - This matches the app's `targetSdk = 34`
   - **ABI**: Select **x86_64** (faster than ARM on most PCs)
   - If not downloaded, click **Download** next to the system image
   - Click **Next**

4. **Verify Configuration**:
   - **AVD Name**: `Pixel_7_API_34` (or similar)
   - **Startup orientation**: Portrait
   - **Graphics**: Automatic (or Hardware - GLES 2.0)
   - **Device Frame**: Enable device frame (optional, for realistic look)
   - **RAM**: 2048 MB minimum (4096 MB recommended)
   - **VM Heap**: 256 MB minimum
   - **Internal Storage**: 2048 MB minimum
   - Click **Show Advanced Settings** for more options:
     - **Camera**:
       - Front: Emulated
       - Back: VirtualScene (for AR testing if needed)
     - **Network**:
       - Speed: Full
       - Latency: None
     - **Boot option**: Cold boot (recommended for consistency)

5. Click **Finish**

### Step 3: Start Emulator

1. In Device Manager, find your newly created AVD
2. Click the **▶ Play** button
3. Wait for emulator to boot (first boot takes 2-3 minutes)

### Step 4: Install App on Emulator

**Option A: From Android Studio**
1. With emulator running, open ShadowCheckMobile project
2. Select your emulator from the device dropdown (top toolbar)
3. Click **Run** (green ▶ button) or press `Shift+F10`

**Option B: From Command Line**
```bash
# Install the APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Or rebuild and install
./gradlew installDebug
```

---

## Method 2: Command Line (Advanced)

### Step 1: List Available System Images

```bash
# Update SDK manager
sdkmanager --update

# List available system images
sdkmanager --list | grep "system-images"

# Install Android 14 (API 34) system image
sdkmanager "system-images;android-34;google_apis;x86_64"
```

### Step 2: Create AVD via Command Line

```bash
# Create AVD named "ShadowCheck_Test"
avdmanager create avd \
  --name ShadowCheck_Test \
  --package "system-images;android-34;google_apis;x86_64" \
  --device "pixel_7"

# List created AVDs
avdmanager list avd
```

### Step 3: Configure AVD (Optional)

Edit `~/.android/avd/ShadowCheck_Test.avd/config.ini`:

```ini
hw.ramSize=4096
vm.heapSize=256
hw.lcd.density=420
hw.lcd.width=1080
hw.lcd.height=2400
hw.gpu.enabled=yes
hw.gpu.mode=auto
```

### Step 4: Start Emulator

```bash
# Start emulator
emulator -avd ShadowCheck_Test

# Start with specific RAM and resolution
emulator -avd ShadowCheck_Test -memory 4096 -gpu host

# Start in headless mode (no UI, faster)
emulator -avd ShadowCheck_Test -no-window -no-audio

# Start with specific port
emulator -avd ShadowCheck_Test -port 5554
```

---

## Method 3: Quick Setup Script

I've created a script to automate this process:

```bash
# Run the setup script
./setup-emulator.sh
```

This will:
1. Check if system image is installed
2. Create an AVD if it doesn't exist
3. Start the emulator
4. Install the app

---

## Recommended Emulator Settings

### For Best Performance:

1. **Enable Hardware Acceleration**:
   - **Linux**: Ensure KVM is enabled
     ```bash
     # Check KVM support
     egrep -c '(vmx|svm)' /proc/cpuinfo
     # Should return > 0

     # Check KVM module
     lsmod | grep kvm

     # Install KVM (if needed)
     sudo apt install qemu-kvm libvirt-daemon-system
     sudo usermod -aG kvm $USER
     # Log out and back in
     ```

   - **Windows**: Enable Hyper-V or use HAXM
   - **macOS**: Built-in hypervisor

2. **Graphics Settings**:
   - **Automatic** (recommended) - Android Studio chooses best option
   - **Hardware - GLES 2.0** - For better graphics performance
   - **Software - GLES 2.0** - Fallback if hardware doesn't work

3. **RAM Allocation**:
   - **Minimum**: 2 GB
   - **Recommended**: 4 GB
   - **Maximum**: Half of your system RAM

4. **Multi-Core CPU**:
   - Edit AVD config: `hw.cpu.ncore=4` (or 2, 8, etc.)

### For Faster Boot Times:

1. **Enable Quick Boot**:
   - Android Studio → Tools → Device Manager
   - Click ⋮ (three dots) next to AVD → Edit
   - Show Advanced Settings → Boot option → **Quick Boot**
   - Set **Number of boot snapshots to save**: 1

2. **Keep Emulator Running**:
   - Don't close emulator between test sessions
   - Uses quick boot for subsequent starts

---

## Testing the App on Emulator

### 1. Launch App
```bash
# Install and launch
./gradlew installDebug

# Launch manually
adb shell am start -n com.shadowcheck.mobile.rebuilt/.rebuilt.presentation.MainActivity
```

### 2. Simulate Location (for Location Features)

**Via Android Studio:**
1. With emulator running, click **⋮ (three dots)** on emulator toolbar
2. Select **Location**
3. Enter coordinates or use search
4. Click **"Send"**

**Via Command Line:**
```bash
# Set location (latitude, longitude)
adb emu geo fix -74.0060 40.7128

# Or use telnet
telnet localhost 5554
auth <auth_token>
geo fix -74.0060 40.7128
```

### 3. Grant Permissions

First launch will request permissions:
- Location: Required for scanning
- Camera: Required for AR features (if using)
- Bluetooth: Won't work on emulator, but grant anyway
- WiFi: Won't work on emulator, but grant anyway

**Grant all permissions via command line:**
```bash
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.ACCESS_FINE_LOCATION
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.ACCESS_COARSE_LOCATION
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.CAMERA
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.BLUETOOTH
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.BLUETOOTH_SCAN
adb shell pm grant com.shadowcheck.mobile.rebuilt android.permission.BLUETOOTH_CONNECT
```

### 4. View Logs

```bash
# View all logs
adb logcat

# Filter for ShadowCheck logs
adb logcat | grep ShadowCheck

# Filter by priority (Error and above)
adb logcat *:E

# Save to file
adb logcat > emulator_logs.txt
```

### 5. Access Database

```bash
# Open database inspector in Android Studio
# View → Tool Windows → Database Inspector

# Or via command line
adb shell
cd /data/data/com.shadowcheck.mobile.rebuilt/databases/
ls
```

---

## Troubleshooting

### Emulator Won't Start

**Problem**: "The emulator process has terminated"

**Solutions**:
1. Check hardware acceleration:
   ```bash
   # Linux
   sudo apt install cpu-checker
   kvm-ok
   ```

2. Try software rendering:
   ```bash
   emulator -avd ShadowCheck_Test -gpu swiftshader_indirect
   ```

3. Increase RAM:
   - Edit AVD → Advanced → RAM to 4096 MB

### Emulator Is Slow

**Solutions**:
1. Enable hardware acceleration (see above)
2. Use x86_64 system image (not ARM)
3. Reduce screen resolution:
   - Edit AVD → Advanced → Resolution: 720p instead of 1080p
4. Disable animations:
   ```bash
   adb shell settings put global window_animation_scale 0.0
   adb shell settings put global transition_animation_scale 0.0
   adb shell settings put global animator_duration_scale 0.0
   ```

### App Crashes on Emulator

**Check Logcat:**
```bash
adb logcat | grep -E "AndroidRuntime|ShadowCheck"
```

**Common Issues:**
1. **WiFi scanning code**: Will fail on emulator (expected)
2. **Bluetooth code**: Will fail on emulator (expected)
3. **Database errors**: Check file paths and permissions
4. **Compose errors**: Check Kotlin version compatibility

### Cannot Install APK

**Error**: "INSTALL_FAILED_UPDATE_INCOMPATIBLE"

**Solution**:
```bash
# Uninstall old version
adb uninstall com.shadowcheck.mobile.rebuilt

# Reinstall
./gradlew installDebug
```

---

## Emulator vs Real Device Comparison

| Feature | Emulator | Real Device |
|---------|----------|-------------|
| WiFi Scanning | ❌ No | ✅ Yes |
| Bluetooth Scanning | ❌ No | ✅ Yes |
| Cellular Detection | ❌ No | ✅ Yes |
| GPS Location | 🟡 Simulated | ✅ Real |
| Camera/AR | 🟡 Virtual | ✅ Real |
| UI Testing | ✅ Yes | ✅ Yes |
| Database | ✅ Yes | ✅ Yes |
| Navigation | ✅ Yes | ✅ Yes |
| Performance Testing | ❌ Not accurate | ✅ Accurate |
| Speed | 🟡 Depends on PC | ✅ Native |

---

## Quick Reference

### Common Commands

```bash
# List devices
adb devices

# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall app
adb uninstall com.shadowcheck.mobile.rebuilt

# Clear app data
adb shell pm clear com.shadowcheck.mobile.rebuilt

# Take screenshot
adb shell screencap /sdcard/screen.png
adb pull /sdcard/screen.png

# Record video
adb shell screenrecord /sdcard/demo.mp4
# Stop with Ctrl+C, then:
adb pull /sdcard/demo.mp4

# Restart emulator
adb reboot

# Kill emulator
adb emu kill
```

### Emulator Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+M` | Open menu |
| `Ctrl+F` | Full screen |
| `Ctrl+Shift+S` | Screenshot |
| `Ctrl+Shift+P` | Power menu |
| `Ctrl+Shift+V` | Toggle volume |
| `Ctrl+Shift+← →` | Rotate left/right |

---

## Next Steps

1. **Create emulator** using Android Studio (Method 1)
2. **Install app**: `./gradlew installDebug`
3. **Test UI features** (navigation, screens, database)
4. **For WiFi/Bluetooth**: Switch to real Android device

**Remember**: This emulator is for **UI/UX testing only**. For full functionality testing (WiFi, Bluetooth, Cellular), you **must use a real device**.
