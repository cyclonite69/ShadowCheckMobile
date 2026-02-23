package com.shadowcheck.mobile.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.shadowcheck.mobile.data.database.AppDatabase
import com.shadowcheck.mobile.data.database.model.WifiNetworkEntity
import com.shadowcheck.mobile.data.database.model.BluetoothDeviceEntity
import com.shadowcheck.mobile.data.database.model.CellularTowerEntity
import kotlinx.coroutines.*
import kotlin.random.Random

class MockScannerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var database: AppDatabase
    
    companion object {
        private const val TAG = "MockScannerService"
        private const val SCAN_INTERVAL_MS = 5000L
    }

    override fun onCreate() {
        super.onCreate()
        database = androidx.room.Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "shadowcheck.db"
        ).build()
        Log.d(TAG, "MockScannerService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "MockScannerService started")
        startMockScanning()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "MockScannerService destroyed")
    }

    private fun startMockScanning() {
        serviceScope.launch {
            while (isActive) {
                try {
                    scanWiFiNetworks()
                    scanBluetoothDevices()
                    scanCellularTowers()
                    delay(SCAN_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Error during mock scanning", e)
                }
            }
        }
    }

    private suspend fun scanWiFiNetworks() {
        val mockNetworks = listOf(
            WifiNetworkEntity(
                bssid = "AA:BB:CC:DD:EE:01",
                ssid = "CoffeeShop_WiFi",
                capabilities = "[WPA2-PSK-CCMP][ESS]",
                frequency = 2437,
                level = Random.nextInt(-70, -30),
                timestamp = System.currentTimeMillis()
            ),
            WifiNetworkEntity(
                bssid = "AA:BB:CC:DD:EE:02",
                ssid = "Home_Network_5G",
                capabilities = "[WPA3-SAE-CCMP][ESS]",
                frequency = 5180,
                level = Random.nextInt(-70, -30),
                timestamp = System.currentTimeMillis()
            ),
            WifiNetworkEntity(
                bssid = "AA:BB:CC:DD:EE:03",
                ssid = "Guest_Network",
                capabilities = "[WPA2-PSK-CCMP][ESS]",
                frequency = 2412,
                level = Random.nextInt(-70, -30),
                timestamp = System.currentTimeMillis()
            ),
            WifiNetworkEntity(
                bssid = "AA:BB:CC:DD:EE:04",
                ssid = "Apartment_Complex_WiFi",
                capabilities = "[WPA2-PSK-CCMP][ESS]",
                frequency = 2462,
                level = Random.nextInt(-70, -30),
                timestamp = System.currentTimeMillis()
            ),
            WifiNetworkEntity(
                bssid = "AA:BB:CC:DD:EE:05",
                ssid = "Office_Secure",
                capabilities = "[WPA2-EAP-CCMP][ESS]",
                frequency = 5240,
                level = Random.nextInt(-70, -30),
                timestamp = System.currentTimeMillis()
            )
        )

        mockNetworks.forEach { network ->
            database.wifiNetworkDao().insertNetwork(network.copy(level = Random.nextInt(-70, -30)))
        }

        Log.d(TAG, "Generated ${mockNetworks.size} mock WiFi networks")
    }

    private suspend fun scanBluetoothDevices() {
        val mockDevices = listOf(
            BluetoothDeviceEntity(
                macAddress = "11:22:33:44:55:01",
                name = "Wireless Headphones",
                type = 1,
                rssi = Random.nextInt(-80, -40),
                timestamp = System.currentTimeMillis()
            ),
            BluetoothDeviceEntity(
                macAddress = "11:22:33:44:55:02",
                name = "Smart Watch",
                type = 2,
                rssi = Random.nextInt(-80, -40),
                timestamp = System.currentTimeMillis()
            ),
            BluetoothDeviceEntity(
                macAddress = "11:22:33:44:55:03",
                name = "Fitness Tracker",
                type = 1,
                rssi = Random.nextInt(-80, -40),
                timestamp = System.currentTimeMillis()
            ),
            BluetoothDeviceEntity(
                macAddress = "11:22:33:44:55:04",
                name = "Bluetooth Speaker",
                type = 1,
                rssi = Random.nextInt(-80, -40),
                timestamp = System.currentTimeMillis()
            )
        )

        mockDevices.forEach { device ->
            database.bluetoothDeviceDao().insertDevice(device.copy(rssi = Random.nextInt(-80, -40)))
        }

        Log.d(TAG, "Generated ${mockDevices.size} mock Bluetooth devices")
    }

    private suspend fun scanCellularTowers() {
        val mockTowers = listOf(
            CellularTowerEntity(
                cellId = 12345,
                lac = 100,
                mcc = 310,
                mnc = 260,
                signalStrength = Random.nextInt(-110, -60),
                latitude = 40.7128 + Random.nextDouble(-0.01, 0.01),
                longitude = -74.0060 + Random.nextDouble(-0.01, 0.01),
                timestamp = System.currentTimeMillis()
            ),
            CellularTowerEntity(
                cellId = 12346,
                lac = 101,
                mcc = 310,
                mnc = 260,
                signalStrength = Random.nextInt(-110, -60),
                latitude = 40.7128 + Random.nextDouble(-0.01, 0.01),
                longitude = -74.0060 + Random.nextDouble(-0.01, 0.01),
                timestamp = System.currentTimeMillis()
            ),
            CellularTowerEntity(
                cellId = 12347,
                lac = 102,
                mcc = 310,
                mnc = 260,
                signalStrength = Random.nextInt(-110, -60),
                latitude = 40.7128 + Random.nextDouble(-0.01, 0.01),
                longitude = -74.0060 + Random.nextDouble(-0.01, 0.01),
                timestamp = System.currentTimeMillis()
            )
        )

        mockTowers.forEach { tower ->
            database.cellularTowerDao().insertTower(tower.copy(signalStrength = Random.nextInt(-110, -60)))
        }

        Log.d(TAG, "Generated ${mockTowers.size} mock cellular towers")
    }
}
