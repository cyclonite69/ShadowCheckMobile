package com.shadowcheck.mobile.rebuilt.service

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

class ScannerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var wifiManager: WifiManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var telephonyManager: TelephonyManager? = null
    private var isScanning = false
    
    private val wifiNetworks = ConcurrentHashMap<String, WifiData>()
    private val bluetoothDevices = ConcurrentHashMap<String, BluetoothData>()
    private val cellularTowers = ConcurrentHashMap<String, CellularData>()
    
    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                wifiManager?.scanResults?.forEach { result ->
                    wifiNetworks[result.BSSID] = WifiData(
                        bssid = result.BSSID,
                        ssid = result.SSID,
                        level = result.level,
                        frequency = result.frequency,
                        capabilities = result.capabilities,
                        timestamp = System.currentTimeMillis()
                    )
                }
                android.util.Log.d("Scanner", "WiFi: ${wifiNetworks.size} networks")
            }
        }
    }
    
    private val bleScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            result.device?.let { device ->
                bluetoothDevices[device.address] = BluetoothData(
                    address = device.address,
                    name = device.name ?: "Unknown",
                    rssi = result.rssi,
                    type = device.type,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SCAN -> {
                startForeground(1, createNotification())
                startScanning()
            }
            ACTION_STOP_SCAN -> stopScanning()
        }
        return START_STICKY
    }
    
    private fun startScanning() {
        if (isScanning) return
        isScanning = true
        
        registerReceiver(wifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        
        serviceScope.launch {
            while (isScanning) {
                scanWiFi()
                scanBluetooth()
                scanCellular()
                delay(5000)
            }
        }
    }
    
    private fun scanWiFi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wifiManager?.startScan()
        }
    }
    
    private fun scanBluetooth() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter?.bluetoothLeScanner?.startScan(bleScanCallback)
            serviceScope.launch {
                delay(3000)
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(bleScanCallback)
            }
        }
    }
    
    private fun scanCellular() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            telephonyManager?.allCellInfo?.forEach { cellInfo ->
                val key = cellInfo.toString()
                cellularTowers[key] = CellularData(
                    cellId = key,
                    type = cellInfo.javaClass.simpleName,
                    level = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) cellInfo.cellSignalStrength.level else 0,
                    timestamp = System.currentTimeMillis()
                )
            }
            android.util.Log.d("Scanner", "Cellular: ${cellularTowers.size} towers")
        }
    }
    
    private fun stopScanning() {
        isScanning = false
        try {
            unregisterReceiver(wifiScanReceiver)
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(bleScanCallback)
        } catch (e: Exception) {}
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Network Scanning",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ShadowCheck Scanning")
            .setContentText("WiFi: ${wifiNetworks.size} BT: ${bluetoothDevices.size} Cell: ${cellularTowers.size}")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopScanning()
        serviceScope.cancel()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        const val ACTION_START_SCAN = "START_SCAN"
        const val ACTION_STOP_SCAN = "STOP_SCAN"
        private const val CHANNEL_ID = "scanner_channel"
        
        fun getCounts(context: Context): Triple<Int, Int, Int> {
            // This would be better with a bound service, but for now return from static
            return Triple(0, 0, 0)
        }
    }
}

data class WifiData(
    val bssid: String,
    val ssid: String,
    val level: Int,
    val frequency: Int,
    val capabilities: String,
    val timestamp: Long
)

data class BluetoothData(
    val address: String,
    val name: String,
    val rssi: Int,
    val type: Int,
    val timestamp: Long
)

data class CellularData(
    val cellId: String,
    val type: String,
    val level: Int,
    val timestamp: Long
)
