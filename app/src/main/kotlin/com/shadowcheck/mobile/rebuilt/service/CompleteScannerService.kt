package com.shadowcheck.mobile.rebuilt.service

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.*
import android.content.pm.PackageManager
import android.location.*
import android.net.wifi.WifiManager
import android.os.*
import android.telephony.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.core.model.BleDevice
import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.domain.repository.BleDeviceRepository
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import com.shadowcheck.mobile.domain.repository.HardwareMetadataRepository
import com.shadowcheck.mobile.domain.repository.SensorReadingRepository
import com.shadowcheck.mobile.wifi.domain.repository.WifiNetworkRepository
import com.shadowcheck.mobile.wifi.model.WifiNetwork
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class CompleteScannerService : Service() {
    @Inject
    lateinit var wifiNetworkRepository: WifiNetworkRepository

    @Inject
    lateinit var bleDeviceRepository: BleDeviceRepository

    @Inject
    lateinit var bluetoothDeviceRepository: BluetoothDeviceRepository

    @Inject
    lateinit var cellularTowerRepository: CellularTowerRepository

    @Inject
    lateinit var sensorReadingRepository: SensorReadingRepository

    @Inject
    lateinit var hardwareMetadataRepository: HardwareMetadataRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var wifiManager: WifiManager? = null
    private var bluetoothAdapter: android.bluetooth.BluetoothAdapter? = null
    private var telephonyManager: TelephonyManager? = null
    private var locationManager: LocationManager? = null
    private var sensorService: SensorCollectionService? = null
    
    private var isScanning = false
    private var currentLat = 0.0
    private var currentLon = 0.0
    private var currentAlt = 0.0
    private var currentAccuracy = 0f
    private var hasValidLocation = false
    
    private var wifiUniqueCount = 0
    private var wifiTotalCount = 0
    private var btUniqueCount = 0
    private var btTotalCount = 0
    private var cellUniqueCount = 0
    private var cellTotalCount = 0
    
    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!hasValidLocation) return
            
            wifiManager?.scanResults?.forEach { result ->
                scope.launch {
                    val existing = wifiNetworkRepository
                        .getNetworksByBssid(result.BSSID)
                        .first()
                        .maxByOrNull { it.timestamp }
                    val now = System.currentTimeMillis()
                    
                    // 30-second deduplication
                    if (existing == null || (now - existing.timestamp) >= 30000) {
                        wifiTotalCount++
                        if (existing == null) wifiUniqueCount++
                        
                        wifiNetworkRepository.insertNetwork(
                            WifiNetwork(
                                ssid = result.SSID ?: "",
                                bssid = result.BSSID,
                                capabilities = result.capabilities ?: "",
                                frequency = result.frequency,
                                signalLevel = result.level,
                                timestamp = now,
                                latitude = currentLat,
                                longitude = currentLon,
                                channel = getChannelFromFreq(result.frequency),
                                channelWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) result.channelWidth else 0,
                                centerFreq0 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) result.centerFreq0 else 0,
                                centerFreq1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) result.centerFreq1 else 0,
                                is80211mc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) result.is80211mcResponder else false,
                                isPasspoint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) result.isPasspointNetwork else false,
                                operatorFriendlyName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    result.operatorFriendlyName?.toString() ?: ""
                                } else {
                                    ""
                                },
                                venueName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    result.venueName?.toString() ?: ""
                                } else {
                                    ""
                                },
                                altitude = currentAlt,
                                accuracy = currentAccuracy,
                                firstSeen = existing?.firstSeen ?: now,
                                lastSeen = now,
                                source = "complete_scanner"
                            )
                        )
                        updateNotification()
                    }
                }
            }
        }
    }
    
    private val bleScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (!hasValidLocation) return
            
            scope.launch {
                val existing = bleDeviceRepository
                    .getDeviceByMacAddress(result.device.address)
                    .first()
                val now = System.currentTimeMillis()
                
                // Get name from device or scan record
                val deviceName = result.device.name ?: result.scanRecord?.deviceName
                
                // 30-second deduplication
                if (existing == null || (now - existing.lastSeen) >= 30000) {
                    btTotalCount++
                    if (existing == null) btUniqueCount++
                    
                    bleDeviceRepository.insertDevice(BleDevice(
                        macAddress = result.device.address,
                        name = deviceName.orEmpty(),
                        rssi = result.rssi,
                        timestamp = now,
                        txPower = result.txPower,
                        isConnectable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) result.isConnectable else false,
                        serviceUuids = result.scanRecord?.serviceUuids?.joinToString(",") ?: "",
                        manufacturerData = result.scanRecord?.manufacturerSpecificData?.toString() ?: "",
                        latitude = currentLat,
                        longitude = currentLon,
                        altitude = currentAlt,
                        accuracy = currentAccuracy,
                        firstSeen = existing?.firstSeen ?: now,
                        lastSeen = now,
                        source = "complete_scanner"
                    ))
                    updateNotification()
                }
            }
        }
    }
    
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                android.bluetooth.BluetoothDevice.ACTION_FOUND -> {
                    if (!hasValidLocation) return
                    val device = intent.getParcelableExtra<android.bluetooth.BluetoothDevice>(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)
                    val rssi = intent.getShortExtra(android.bluetooth.BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                    
                    device?.let {
                        scope.launch {
                            val existing = bluetoothDeviceRepository
                                .getDeviceByMacAddress(it.address)
                                .first()
                            val now = System.currentTimeMillis()
                            
                            if (existing == null || (now - existing.timestamp) >= 30000) {
                                btTotalCount++
                                if (existing == null) btUniqueCount++
                                
                                bluetoothDeviceRepository.insertDevice(BluetoothDevice(
                                    macAddress = it.address,
                                    name = it.name,
                                    rssi = rssi,
                                    deviceType = it.type,
                                    timestamp = now,
                                    latitude = currentLat,
                                    longitude = currentLon,
                                    deviceClass = it.bluetoothClass?.deviceClass ?: 0,
                                    bondState = it.bondState,
                                    altitude = currentAlt,
                                    accuracy = currentAccuracy,
                                    firstSeen = existing?.firstSeen ?: now,
                                    lastSeen = now,
                                    source = "complete_scanner"
                                ))
                                updateNotification()
                            }
                        }
                    }
                }
            }
        }
    }
    
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLat = location.latitude
            currentLon = location.longitude
            currentAlt = location.altitude
            currentAccuracy = location.accuracy
            hasValidLocation = location.accuracy < 50f && currentLat != 0.0 && currentLon != 0.0
            sensorService?.updateLocation(location)
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }
    
    override fun onCreate() {
        super.onCreate()
        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorService = SensorCollectionService(
            context = this,
            sensorReadingRepository = sensorReadingRepository,
            hardwareMetadataRepository = hardwareMetadataRepository
        )
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> {
                startForeground(1, createNotification())
                startScanning()
            }
            "STOP" -> stopScanning()
        }
        return START_STICKY
    }
    
    private fun startScanning() {
        if (isScanning) return
        isScanning = true
        
        sensorService?.start()
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, locationListener)
        }
        
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        registerReceiver(bluetoothReceiver, IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND))
        
        scope.launch {
            while (isScanning) {
                if (hasValidLocation) {
                    scanWiFi()
                    scanBluetooth()
                    scanCellular()
                }
                delay(3000)
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
            // BLE scan
            bluetoothAdapter?.bluetoothLeScanner?.let { scanner ->
                scanner.stopScan(bleScanCallback)
                scanner.startScan(bleScanCallback)
            }
            // Classic Bluetooth discovery
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter?.cancelDiscovery()
            }
            bluetoothAdapter?.startDiscovery()
        }
    }
    
    private fun scanCellular() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            telephonyManager?.allCellInfo?.forEach { cellInfo ->
                scope.launch {
                    val now = System.currentTimeMillis()
                    when (cellInfo) {
                        is CellInfoGsm -> {
                            val identity = cellInfo.cellIdentity
                            val existing = cellularTowerRepository
                                .getTowerByCellId(identity.cid)
                                .first()
                            
                            // 30-second deduplication
                            if (existing == null || (now - existing.lastSeen) >= 30000) {
                                cellTotalCount++
                                if (existing == null) cellUniqueCount++
                                
                                cellularTowerRepository.insertTower(CellularTower(
                                    cellId = identity.cid,
                                    lac = identity.lac,
                                    mcc = identity.mcc,
                                    mnc = identity.mnc,
                                    signalStrength = cellInfo.cellSignalStrength.level,
                                    signalQuality = cellInfo.cellSignalStrength.dbm,
                                    networkType = "GSM",
                                    operatorName = telephonyManager?.networkOperatorName ?: "",
                                    latitude = currentLat,
                                    longitude = currentLon,
                                    altitude = currentAlt,
                                    accuracy = currentAccuracy,
                                    timestamp = now,
                                    firstSeen = existing?.firstSeen ?: now,
                                    lastSeen = now,
                                    source = "complete_scanner"
                                ))
                                updateNotification()
                            }
                        }
                        is CellInfoLte -> {
                            val identity = cellInfo.cellIdentity
                            val existing = cellularTowerRepository
                                .getTowerByCellId(identity.ci)
                                .first()
                            
                            // 30-second deduplication
                            if (existing == null || (now - existing.lastSeen) >= 30000) {
                                cellTotalCount++
                                if (existing == null) cellUniqueCount++
                                
                                cellularTowerRepository.insertTower(CellularTower(
                                    cellId = identity.ci,
                                    lac = identity.tac,
                                    mcc = identity.mcc,
                                    mnc = identity.mnc,
                                    psc = identity.pci,
                                    signalStrength = cellInfo.cellSignalStrength.level,
                                    signalQuality = cellInfo.cellSignalStrength.dbm,
                                    networkType = "LTE",
                                    operatorName = telephonyManager?.networkOperatorName ?: "",
                                    latitude = currentLat,
                                    longitude = currentLon,
                                    altitude = currentAlt,
                                    accuracy = currentAccuracy,
                                    timestamp = now,
                                    firstSeen = existing?.firstSeen ?: now,
                                    lastSeen = now,
                                    source = "complete_scanner"
                                ))
                                updateNotification()
                            }
                        }
                    }
                }
            }
        }
    }
    
    private fun stopScanning() {
        isScanning = false
        sensorService?.stop()
        try {
            unregisterReceiver(wifiReceiver)
            unregisterReceiver(bluetoothReceiver)
            locationManager?.removeUpdates(locationListener)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(bleScanCallback)
                bluetoothAdapter?.cancelDiscovery()
            }
        } catch (e: Exception) {}
    }
    
    private fun updateNotification() {
        val notification = createNotification()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "scanner")
            .setContentTitle("ShadowCheck ${if (isScanning && hasValidLocation) "Scanning" else "Waiting for GPS"}")
            .setContentText("WiFi: $wifiUniqueCount/$wifiTotalCount BT: $btUniqueCount/$btTotalCount Cell: $cellUniqueCount/$cellTotalCount")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("scanner", "Scanning", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
    
    private fun getChannelFromFreq(freq: Int): Int = when (freq) {
        in 2412..2484 -> (freq - 2407) / 5
        in 5170..5825 -> (freq - 5000) / 5
        else -> 0
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopScanning()
        scope.cancel()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        fun getCounts(context: Context): ScanCounts {
            // Would use bound service in production
            return ScanCounts(0, 0, 0, 0, 0, 0, false)
        }
    }
}

data class ScanCounts(
    val wifiUnique: Int,
    val wifiTotal: Int,
    val btUnique: Int,
    val btTotal: Int,
    val cellUnique: Int,
    val cellTotal: Int,
    val isScanning: Boolean
)
