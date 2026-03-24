package com.shadowcheck.mobile

import com.shadowcheck.mobile.domain.model.BluetoothDevice
import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.core.model.WifiNetwork

/**
 * Common test utilities and factory methods for creating test data.
 */
object TestUtils {

    /**
     * Creates a test WiFi network with default or custom values.
     */
    fun createTestWifiNetwork(
        ssid: String = "TestNetwork",
        bssid: String = "AA:BB:CC:DD:EE:FF",
        capabilities: String = "WPA2",
        frequency: Int = 2412,
        level: Int = -70,
        timestamp: Long = System.currentTimeMillis()
    ): WifiNetwork {
        return WifiNetwork(
            ssid = ssid,
            bssid = bssid,
            capabilities = capabilities,
            frequency = frequency,
            signalLevel = level,
            timestamp = timestamp
        )
    }

    /**
     * Creates a list of test WiFi networks.
     */
    fun createTestWifiNetworks(count: Int): List<WifiNetwork> {
        return (1..count).map { i ->
            createTestWifiNetwork(
                ssid = "Network$i",
                bssid = "AA:BB:CC:DD:EE:${i.toString().padStart(2, '0')}",
                level = -50 - (i * 5),
                timestamp = System.currentTimeMillis() - (i * 1000L)
            )
        }
    }

    /**
     * Creates a test Bluetooth device with default or custom values.
     */
    fun createTestBluetoothDevice(
        name: String = "TestDevice",
        address: String = "AA:BB:CC:DD:EE:FF",
        type: String = "Classic",
        rssi: Int = -70,
        timestamp: Long = System.currentTimeMillis()
    ): BluetoothDevice {
        return BluetoothDevice(
            name = name,
            address = address,
            type = type,
            rssi = rssi,
            timestamp = timestamp
        )
    }

    /**
     * Creates a list of test Bluetooth devices.
     */
    fun createTestBluetoothDevices(count: Int): List<BluetoothDevice> {
        return (1..count).map { i ->
            createTestBluetoothDevice(
                name = "Device$i",
                address = "AA:BB:CC:DD:EE:${i.toString().padStart(2, '0')}",
                rssi = -50 - (i * 5),
                timestamp = System.currentTimeMillis() - (i * 1000L)
            )
        }
    }

    /**
     * Creates a test Cellular tower with default or custom values.
     */
    fun createTestCellularTower(
        type: String = "LTE",
        mcc: Int = 310,
        mnc: Int = 260,
        cellId: Int = 12345,
        pci: Int = 1,
        latitude: Double = 40.7128,
        longitude: Double = -74.0060,
        rssi: Int = -85,
        timestamp: Long = System.currentTimeMillis()
    ): CellularTower {
        return CellularTower(
            type = type,
            mcc = mcc,
            mnc = mnc,
            cellId = cellId,
            pci = pci,
            latitude = latitude,
            longitude = longitude,
            rssi = rssi,
            timestamp = timestamp
        )
    }

    /**
     * Creates a list of test Cellular towers.
     */
    fun createTestCellularTowers(count: Int): List<CellularTower> {
        return (1..count).map { i ->
            createTestCellularTower(
                type = if (i % 2 == 0) "LTE" else "5G",
                cellId = 12345 + i,
                pci = i,
                latitude = 40.7128 + (i * 0.001),
                longitude = -74.0060 + (i * 0.001),
                rssi = -85 - (i * 2),
                timestamp = System.currentTimeMillis() - (i * 1000L)
            )
        }
    }
}

/**
 * Extension function to assert that a list is sorted by a selector in descending order.
 */
fun <T, R : Comparable<R>> List<T>.assertSortedByDescending(selector: (T) -> R) {
    val sorted = this.sortedByDescending(selector)
    assert(this == sorted) {
        "List is not sorted by descending order.\nExpected: $sorted\nActual: $this"
    }
}

/**
 * Extension function to assert that a list is sorted by a selector in ascending order.
 */
fun <T, R : Comparable<R>> List<T>.assertSortedBy(selector: (T) -> R) {
    val sorted = this.sortedBy(selector)
    assert(this == sorted) {
        "List is not sorted by ascending order.\nExpected: $sorted\nActual: $this"
    }
}
