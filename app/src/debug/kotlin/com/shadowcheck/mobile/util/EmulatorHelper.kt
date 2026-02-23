package com.shadowcheck.mobile.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.shadowcheck.mobile.service.MockScannerService

/**
 * Helper utilities for running on Android Emulator
 *
 * ONLY included in debug builds (src/debug/)
 */
object EmulatorHelper {

    private const val TAG = "EmulatorHelper"

    /**
     * Check if running on an emulator
     */
    fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
    }

    /**
     * Start mock scanner service if running on emulator
     *
     * This provides fake WiFi, Bluetooth, and Cellular data
     * for testing when real hardware is not available.
     */
    fun startMockScannerIfEmulator(context: Context) {
        if (isEmulator()) {
            Log.d(TAG, "Emulator detected - Starting mock scanner service")
            val intent = Intent(context, MockScannerService::class.java)
            context.startService(intent)
        } else {
            Log.d(TAG, "Real device detected - Mock scanner not needed")
        }
    }

    /**
     * Stop mock scanner service
     */
    fun stopMockScanner(context: Context) {
        val intent = Intent(context, MockScannerService::class.java)
        context.stopService(intent)
        Log.d(TAG, "Mock scanner service stopped")
    }

    /**
     * Get a descriptive string of the current environment
     */
    fun getEnvironmentInfo(): String {
        return buildString {
            appendLine("Environment Info:")
            appendLine("  Running on: ${if (isEmulator()) "EMULATOR" else "REAL DEVICE"}")
            appendLine("  Brand: ${Build.BRAND}")
            appendLine("  Device: ${Build.DEVICE}")
            appendLine("  Model: ${Build.MODEL}")
            appendLine("  Product: ${Build.PRODUCT}")
            appendLine("  Hardware: ${Build.HARDWARE}")
            appendLine("  Manufacturer: ${Build.MANUFACTURER}")
            appendLine("  Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        }
    }
}
