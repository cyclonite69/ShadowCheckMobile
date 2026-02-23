#!/bin/bash
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ShadowCheckMobile Emulator Setup${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Configuration
AVD_NAME="ShadowCheck_Test_API34"
SYSTEM_IMAGE="system-images;android-34;google_apis;x86_64"
DEVICE="pixel_7"

# Check if Android SDK is installed
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo -e "${RED}❌ Error: Android SDK not found${NC}"
    echo "Please set ANDROID_HOME or ANDROID_SDK_ROOT environment variable"
    echo "Example: export ANDROID_HOME=~/Android/Sdk"
    exit 1
fi

SDK_ROOT="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
echo -e "${GREEN}✅ Android SDK found: $SDK_ROOT${NC}"

# Check if sdkmanager is available
SDKMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
if [ ! -f "$SDKMANAGER" ]; then
    SDKMANAGER="$SDK_ROOT/tools/bin/sdkmanager"
fi

if [ ! -f "$SDKMANAGER" ]; then
    echo -e "${RED}❌ Error: sdkmanager not found${NC}"
    echo "Please install Android SDK Command-line Tools"
    exit 1
fi

# Check if system image is installed
echo ""
echo -e "${YELLOW}📦 Checking system image...${NC}"
if ! $SDKMANAGER --list | grep -q "$SYSTEM_IMAGE"; then
    echo -e "${YELLOW}⬇️  System image not installed. Installing...${NC}"
    echo "This may take several minutes..."
    $SDKMANAGER "$SYSTEM_IMAGE"
else
    echo -e "${GREEN}✅ System image already installed${NC}"
fi

# Check if AVD already exists
AVDMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/avdmanager"
if [ ! -f "$AVDMANAGER" ]; then
    AVDMANAGER="$SDK_ROOT/tools/bin/avdmanager"
fi

echo ""
echo -e "${YELLOW}📱 Checking AVD...${NC}"
if $AVDMANAGER list avd | grep -q "$AVD_NAME"; then
    echo -e "${GREEN}✅ AVD '$AVD_NAME' already exists${NC}"
    read -p "Do you want to delete and recreate it? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}🗑️  Deleting existing AVD...${NC}"
        $AVDMANAGER delete avd --name "$AVD_NAME"
    else
        echo -e "${BLUE}ℹ️  Using existing AVD${NC}"
    fi
fi

# Create AVD if it doesn't exist
if ! $AVDMANAGER list avd | grep -q "$AVD_NAME"; then
    echo -e "${YELLOW}🔨 Creating AVD '$AVD_NAME'...${NC}"
    echo "no" | $AVDMANAGER create avd \
        --name "$AVD_NAME" \
        --package "$SYSTEM_IMAGE" \
        --device "$DEVICE"

    echo -e "${GREEN}✅ AVD created successfully${NC}"

    # Configure AVD for better performance
    AVD_CONFIG="$HOME/.android/avd/${AVD_NAME}.avd/config.ini"
    if [ -f "$AVD_CONFIG" ]; then
        echo -e "${YELLOW}⚙️  Configuring AVD for performance...${NC}"
        # Increase RAM
        sed -i 's/hw.ramSize=.*/hw.ramSize=4096/' "$AVD_CONFIG"
        # Enable hardware GPU
        echo "hw.gpu.enabled=yes" >> "$AVD_CONFIG"
        echo "hw.gpu.mode=auto" >> "$AVD_CONFIG"
        # Set multi-core
        echo "hw.cpu.ncore=4" >> "$AVD_CONFIG"
        echo -e "${GREEN}✅ AVD configured${NC}"
    fi
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ Emulator setup complete!${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "📝 Next steps:"
echo -e "  1. Start emulator:   ${YELLOW}emulator -avd $AVD_NAME${NC}"
echo -e "  2. Or from Android Studio: Tools → Device Manager → Start"
echo -e "  3. Install app:      ${YELLOW}./gradlew installDebug${NC}"
echo ""
echo -e "${YELLOW}⚠️  Remember:${NC}"
echo "  - WiFi scanning won't work (needs physical hardware)"
echo "  - Bluetooth scanning won't work (needs physical hardware)"
echo "  - Use emulator for UI/UX testing only"
echo "  - For full testing, use a real Android device"
echo ""

# Ask if user wants to start emulator now
read -p "Do you want to start the emulator now? (y/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}🚀 Starting emulator...${NC}"
    echo "This may take 2-3 minutes for first boot"
    EMULATOR="$SDK_ROOT/emulator/emulator"
    if [ -f "$EMULATOR" ]; then
        # Start in background
        $EMULATOR -avd "$AVD_NAME" -memory 4096 -gpu auto &
        EMULATOR_PID=$!
        echo -e "${GREEN}✅ Emulator starting (PID: $EMULATOR_PID)${NC}"
        echo ""
        echo "Wait for emulator to fully boot, then run:"
        echo -e "  ${YELLOW}./gradlew installDebug${NC}"
    else
        echo -e "${RED}❌ Emulator binary not found at: $EMULATOR${NC}"
        echo "Start manually from Android Studio"
    fi
else
    echo -e "${BLUE}👍 You can start the emulator later${NC}"
fi
