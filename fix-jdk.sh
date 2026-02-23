#!/bin/bash
set -e

echo "🔧 ShadowCheckMobile JDK Fix Script"
echo "===================================="
echo ""

# Stop all Gradle daemons
echo "1️⃣ Stopping all Gradle daemons..."
./gradlew --stop

# Clean Gradle caches
echo "2️⃣ Cleaning Gradle caches..."
rm -rf .gradle
rm -rf build
rm -rf app/build

# Clean user Gradle cache (configuration cache)
echo "3️⃣ Cleaning configuration cache..."
rm -rf ~/.gradle/configuration-cache

# Clean transforms cache (where the error occurs)
echo "4️⃣ Cleaning transforms cache..."
rm -rf ~/.gradle/caches/transforms-*
rm -rf ~/.gradle/caches/8.*/transforms

echo ""
echo "✅ Cleanup complete!"
echo ""
echo "📝 Next steps:"
echo "   1. Open Android Studio"
echo "   2. File → Project Structure → Project"
echo "   3. Set 'Gradle JDK' to JDK 17 (NOT JetBrains Runtime)"
echo "   4. Click Apply → OK"
echo "   5. File → Sync Project with Gradle Files"
echo "   6. Run: ./gradlew clean build"
echo ""
echo "⚠️  IMPORTANT: Do NOT use JetBrains Runtime or JDK 21!"
echo "              You MUST use standard JDK 17."
