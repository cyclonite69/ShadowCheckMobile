# JDK Configuration Guide

## Problem

You're seeing "Undefined java.home" or Gradle is using the wrong JDK version (e.g., JDK 21 instead of JDK 17).

## Important: This Project Requires JDK 17

**Do NOT use JDK 21, JDK 11, or other versions.** The project is configured for **JDK 17** specifically.

Using the wrong JDK will cause:
- Compilation errors
- KAPT/annotation processing failures
- Runtime crashes
- Incompatible bytecode

---

## Solution 1: Configure Android Studio (Recommended)

### Step 1: Check Current JDK

1. Open Android Studio
2. **File → Project Structure** (or `Ctrl+Alt+Shift+S` / `Cmd+;` on Mac)
3. **Project** section on the left
4. Look at **"Gradle JDK"** dropdown

**Current Issue**: It's likely set to "Project SDK (JetBrains Runtime 21.0.8)" or similar

### Step 2: Install/Select JDK 17

**Option A: Download JDK 17 via Android Studio**

1. In the Gradle JDK dropdown, click **"Download JDK..."**
2. Select:
   - **Version**: 17
   - **Vendor**: Azul Zulu, Eclipse Temurin, or Oracle
3. Click **Download**
4. Wait for download to complete
5. Select the newly downloaded JDK 17 from the dropdown

**Option B: Use Existing JDK 17**

1. In the Gradle JDK dropdown, select **"Add JDK..."**
2. Navigate to your JDK 17 installation:
   - **Linux**: `/usr/lib/jvm/java-17-openjdk`
   - **macOS**: `/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home`
   - **Windows**: `C:\Program Files\Java\jdk-17`
3. Click **OK**

### Step 3: Verify Configuration

1. Still in **File → Project Structure → Project**:
   - **SDK**: Should be "Android API 34 Platform"
   - **Language level**: Should be "17 - Sealed types, always-strict floating-point semantics"
   - **Gradle JDK**: Should be "jbr-17" or "17" (whatever you selected)

2. Click **Apply** then **OK**

### Step 4: Sync Gradle

1. Click **File → Sync Project with Gradle Files**
2. Or click the 🐘 (elephant) icon in the toolbar
3. Wait for sync to complete

---

## Solution 2: Configure via gradle.properties (Alternative)

If Android Studio configuration doesn't work, manually set JDK path:

### Step 1: Find Your JDK 17 Installation

**Linux:**
```bash
sudo update-alternatives --config java
# or
ls /usr/lib/jvm/
```

**macOS:**
```bash
/usr/libexec/java_home -V
```

**Windows:**
```powershell
dir "C:\Program Files\Java"
```

### Step 2: Edit gradle.properties

Open `gradle.properties` in the project root and uncomment/add:

```properties
# Set this to your actual JDK 17 path
org.gradle.java.home=/usr/lib/jvm/java-17-openjdk
```

**Example paths:**
- Linux: `/usr/lib/jvm/java-17-openjdk`
- macOS: `/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home`
- Windows: `C:\\Program Files\\Java\\jdk-17` (note double backslashes)

### Step 3: Sync Gradle

```bash
./gradlew --stop
./gradlew clean build
```

---

## Solution 3: Verify JDK Installation

### Check Java Version

```bash
java -version
```

Should output:
```
openjdk version "17.0.x" ...
```

If not, install JDK 17:

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Linux (Fedora):**
```bash
sudo dnf install java-17-openjdk-devel
```

**macOS (Homebrew):**
```bash
brew install openjdk@17
```

**Windows:**
Download from [Adoptium](https://adoptium.net/temurin/releases/?version=17) or [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

---

## Troubleshooting

### Issue: "Unsupported class file major version 65"

**Cause**: Code compiled with JDK 21 but project expects JDK 17

**Fix**:
1. Change Gradle JDK to 17 (see above)
2. Clean build:
   ```bash
   ./gradlew clean
   rm -rf .gradle build app/build
   ./gradlew build
   ```

### Issue: "KAPT: Kotlin class file not supported version"

**Cause**: KAPT annotation processing incompatible with wrong JDK

**Fix**:
1. Ensure JDK 17 is selected
2. Rebuild project:
   ```bash
   ./gradlew clean
   ./gradlew kaptDebugKotlin
   ./gradlew build
   ```

### Issue: Gradle still uses wrong JDK

**Fix**:
1. Delete `.gradle` folder:
   ```bash
   rm -rf .gradle
   ```
2. Delete `~/.gradle/caches` (optional, nuclear option):
   ```bash
   rm -rf ~/.gradle/caches
   ```
3. Restart Android Studio
4. Sync Gradle

### Issue: "Could not determine java version from '21.0.8'"

**Cause**: Gradle doesn't recognize JBR 21 as valid

**Fix**: Don't use JetBrains Runtime for this project. Use standard JDK 17.

---

## Verification Checklist

After configuration, verify everything works:

- [ ] `./gradlew --version` shows Gradle 8.2+ with JVM 17
- [ ] `./gradlew clean build` completes without errors
- [ ] Android Studio shows "Gradle JDK: 17" in Project Structure
- [ ] No "Unsupported class file" errors
- [ ] KAPT/KSP processors run successfully

---

## Why JDK 17?

- **Android Gradle Plugin 8.2** requires JDK 17
- **Kotlin 2.0** is optimized for JDK 17
- **Room 2.6.1** KAPT works best with JDK 17
- **Project build.gradle.kts** specifies `JavaVersion.VERSION_17`

**Do not change to JDK 21** unless you're willing to:
- Update AGP to 8.3+
- Update all dependencies
- Test extensively
- Update all documentation

---

## Quick Reference

| Component | Required Version |
|-----------|-----------------|
| JDK | **17** (not 11, not 21) |
| Android Gradle Plugin | 8.2.0 |
| Kotlin | 2.0.0 |
| Gradle | 8.2+ |
| Android SDK | 34 |

---

## Still Having Issues?

1. **Check build.gradle.kts**:
   ```kotlin
   compileOptions {
       sourceCompatibility = JavaVersion.VERSION_17
       targetCompatibility = JavaVersion.VERSION_17
   }

   kotlinOptions {
       jvmTarget = "17"
   }
   ```

2. **Check JAVA_HOME environment variable**:
   ```bash
   echo $JAVA_HOME
   # Should point to JDK 17
   ```

3. **Set JAVA_HOME** (if needed):
   ```bash
   # Linux/macOS (add to ~/.bashrc or ~/.zshrc)
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk

   # Windows (PowerShell as Administrator)
   [System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "Machine")
   ```

4. **Restart your terminal/IDE** after setting JAVA_HOME

---

## Contact

If you're still experiencing issues after following this guide:
1. Run `./gradlew --version` and save the output
2. Run `java -version` and save the output
3. Open an issue on GitHub with both outputs
