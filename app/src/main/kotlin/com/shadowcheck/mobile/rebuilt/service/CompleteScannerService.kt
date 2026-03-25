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
import android.util.SparseArray
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
import java.util.concurrent.ConcurrentLinkedQueue
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
    private var wakeLock: PowerManager.WakeLock? = null
    private var highPerformanceMode = false
    private var wifiScanIntervalMs = BALANCED_WIFI_SCAN_INTERVAL_MS
    private var bluetoothScanIntervalMs = BALANCED_BLUETOOTH_SCAN_INTERVAL_MS
    private var cellularScanIntervalMs = BALANCED_CELLULAR_SCAN_INTERVAL_MS
    
    private var isScanning = false
    private var currentLat = 0.0
    private var currentLon = 0.0
    private var currentAlt = 0.0
    private var currentAccuracy = 0f
    private var currentSpeed = 0f
    private var currentBearing = 0f
    private var hasValidLocation = false
    
    private var wifiUniqueCount = 0
    private var wifiTotalCount = 0
    private var btUniqueCount = 0
    private var btTotalCount = 0
    private var cellUniqueCount = 0
    private var cellTotalCount = 0
    private val pendingWifiNetworks = ConcurrentLinkedQueue<WifiNetwork>()
    private val pendingBleDevices = ConcurrentLinkedQueue<BleDevice>()
    private val pendingBluetoothDevices = ConcurrentLinkedQueue<BluetoothDevice>()
    private val pendingCellularTowers = ConcurrentLinkedQueue<CellularTower>()
    private var wifiDroppedCount = 0
    private var bleDroppedCount = 0
    private var bluetoothDroppedCount = 0
    private var cellDroppedCount = 0
    private var lastFlushStartedAt = 0L
    private var lastFlushCompletedAt = 0L
    private var lastFlushDurationMs = 0L
    
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
                        
                        enqueueWifiNetwork(
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
                                standard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    wifiStandardToString(result.wifiStandard)
                                } else {
                                    ""
                                },
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
                                vendorOui = extractOui(result.BSSID),
                                altitude = currentAlt,
                                accuracy = currentAccuracy,
                                speed = currentSpeed,
                                bearing = currentBearing,
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
                    
                    enqueueBleDevice(BleDevice(
                        macAddress = result.device.address,
                        name = deviceName.orEmpty(),
                        rssi = result.rssi,
                        timestamp = now,
                        txPower = result.txPower,
                        isConnectable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) result.isConnectable else false,
                        serviceUuids = result.scanRecord?.serviceUuids?.joinToString(",") ?: "",
                        manufacturerData = serializeManufacturerData(
                            result.scanRecord?.manufacturerSpecificData
                        ),
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
                                
                                enqueueBluetoothDevice(BluetoothDevice(
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
            currentSpeed = location.speed
            currentBearing = location.bearing
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
            hardwareMetadataRepository = hardwareMetadataRepository,
            highPerformanceMode = highPerformanceMode
        )
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ShadowCheck:CompleteScannerWakeLock"
        ).apply {
            setReferenceCounted(false)
        }
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        highPerformanceMode = intent?.getBooleanExtra(EXTRA_HIGH_PERFORMANCE, true) ?: true
        configureScanCadence()
        sensorService = SensorCollectionService(
            context = this,
            sensorReadingRepository = sensorReadingRepository,
            hardwareMetadataRepository = hardwareMetadataRepository,
            highPerformanceMode = highPerformanceMode
        )
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

        if (highPerformanceMode && wakeLock?.isHeld != true) {
            wakeLock?.acquire()
        }
        
        sensorService?.start()
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, locationListener)
        }
        
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        registerReceiver(bluetoothReceiver, IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND))
        
        scope.launch {
            while (isScanning) {
                flushPendingWrites()
                delay(BATCH_FLUSH_INTERVAL_MS)
            }
        }

        scope.launch {
            while (isScanning) {
                if (hasValidLocation) {
                    scanWiFi()
                }
                delay(wifiScanIntervalMs)
            }
        }

        scope.launch {
            while (isScanning) {
                if (hasValidLocation) {
                    scanBluetooth()
                }
                delay(bluetoothScanIntervalMs)
            }
        }

        scope.launch {
            while (isScanning) {
                if (hasValidLocation) {
                    scanCellular()
                }
                delay(cellularScanIntervalMs)
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
                            val signal = cellInfo.cellSignalStrength
                            val existing = cellularTowerRepository
                                .getTowerByCellId(identity.cid)
                                .first()
                            
                            // 30-second deduplication
                            if (existing == null || (now - existing.lastSeen) >= 30000) {
                                cellTotalCount++
                                if (existing == null) cellUniqueCount++
                                
                                enqueueCellularTower(CellularTower(
                                    cellId = identity.cid,
                                    lac = identity.lac,
                                    mcc = identity.mcc,
                                    mnc = identity.mnc,
                                    signalStrength = signal.level,
                                    signalQuality = signal.dbm,
                                    rawDbm = signal.dbm,
                                    rawAsuLevel = signal.asuLevel,
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
                            val signal = cellInfo.cellSignalStrength
                            val existing = cellularTowerRepository
                                .getTowerByCellId(identity.ci)
                                .first()
                            
                            // 30-second deduplication
                            if (existing == null || (now - existing.lastSeen) >= 30000) {
                                cellTotalCount++
                                if (existing == null) cellUniqueCount++
                                
                                enqueueCellularTower(CellularTower(
                                    cellId = identity.ci,
                                    lac = identity.tac,
                                    mcc = identity.mcc,
                                    mnc = identity.mnc,
                                    psc = identity.pci,
                                    signalStrength = signal.level,
                                    signalQuality = signal.dbm,
                                    rawDbm = signal.dbm,
                                    rawAsuLevel = signal.asuLevel,
                                    rsrp = signal.rsrp,
                                    rsrq = signal.rsrq,
                                    rssnr = signal.rssnr,
                                    cqi = signal.cqi,
                                    timingAdvance = signal.timingAdvance,
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
        runBlocking {
            flushPendingWrites()
        }
        updateMetricsSnapshot()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
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
        updateMetricsSnapshot()
        val notification = createNotification()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }

    private suspend fun enqueueWifiNetwork(network: WifiNetwork) {
        trimQueueIfNeeded(
            queue = pendingWifiNetworks,
            limit = WIFI_QUEUE_LIMIT,
            onDrop = { wifiDroppedCount++ }
        )
        pendingWifiNetworks.add(network)
        if (pendingWifiNetworks.size >= WIFI_BATCH_SIZE) {
            flushWifiNetworks()
        }
    }

    private suspend fun enqueueBleDevice(device: BleDevice) {
        trimQueueIfNeeded(
            queue = pendingBleDevices,
            limit = BLE_QUEUE_LIMIT,
            onDrop = { bleDroppedCount++ }
        )
        pendingBleDevices.add(device)
        if (pendingBleDevices.size >= BLE_BATCH_SIZE) {
            flushBleDevices()
        }
    }

    private suspend fun enqueueBluetoothDevice(device: BluetoothDevice) {
        trimQueueIfNeeded(
            queue = pendingBluetoothDevices,
            limit = BT_QUEUE_LIMIT,
            onDrop = { bluetoothDroppedCount++ }
        )
        pendingBluetoothDevices.add(device)
        if (pendingBluetoothDevices.size >= BT_BATCH_SIZE) {
            flushBluetoothDevices()
        }
    }

    private suspend fun enqueueCellularTower(tower: CellularTower) {
        trimQueueIfNeeded(
            queue = pendingCellularTowers,
            limit = CELL_QUEUE_LIMIT,
            onDrop = { cellDroppedCount++ }
        )
        pendingCellularTowers.add(tower)
        if (pendingCellularTowers.size >= CELL_BATCH_SIZE) {
            flushCellularTowers()
        }
    }

    private suspend fun flushPendingWrites() {
        lastFlushStartedAt = System.currentTimeMillis()
        flushWifiNetworks()
        flushBleDevices()
        flushBluetoothDevices()
        flushCellularTowers()
        lastFlushCompletedAt = System.currentTimeMillis()
        lastFlushDurationMs = lastFlushCompletedAt - lastFlushStartedAt
        updateMetricsSnapshot()
    }

    private fun updateMetricsSnapshot() {
        latestCounts = ScanCounts(
            wifiUnique = wifiUniqueCount,
            wifiTotal = wifiTotalCount,
            btUnique = btUniqueCount,
            btTotal = btTotalCount,
            cellUnique = cellUniqueCount,
            cellTotal = cellTotalCount,
            isScanning = isScanning,
            wifiDropped = wifiDroppedCount,
            bleDropped = bleDroppedCount,
            bluetoothDropped = bluetoothDroppedCount,
            cellDropped = cellDroppedCount,
            lastFlushDurationMs = lastFlushDurationMs
        )
    }

    private suspend fun flushWifiNetworks() {
        val batch = drainQueue(pendingWifiNetworks)
        if (batch.isNotEmpty()) {
            wifiNetworkRepository.insertNetworks(batch)
        }
    }

    private suspend fun flushBleDevices() {
        val batch = drainQueue(pendingBleDevices)
        if (batch.isNotEmpty()) {
            bleDeviceRepository.insertDevices(batch)
        }
    }

    private suspend fun flushBluetoothDevices() {
        val batch = drainQueue(pendingBluetoothDevices)
        if (batch.isNotEmpty()) {
            bluetoothDeviceRepository.insertDevices(batch)
        }
    }

    private suspend fun flushCellularTowers() {
        val batch = drainQueue(pendingCellularTowers)
        if (batch.isNotEmpty()) {
            cellularTowerRepository.insertTowers(batch)
        }
    }

    private fun <T> drainQueue(queue: ConcurrentLinkedQueue<T>): List<T> {
        val items = mutableListOf<T>()
        while (true) {
            val item = queue.poll() ?: break
            items.add(item)
        }
        return items
    }

    private fun <T> trimQueueIfNeeded(
        queue: ConcurrentLinkedQueue<T>,
        limit: Int,
        onDrop: () -> Unit
    ) {
        while (queue.size >= limit) {
            if (queue.poll() == null) break
            onDrop()
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "scanner")
            .setContentTitle("ShadowCheck ${if (isScanning && hasValidLocation) "Scanning" else "Waiting for GPS"}")
            .setContentText(
                "WiFi: $wifiUniqueCount/$wifiTotalCount(${pendingWifiNetworks.size}) " +
                        "BT: $btUniqueCount/$btTotalCount(${pendingBleDevices.size + pendingBluetoothDevices.size}) " +
                        "Cell: $cellUniqueCount/$cellTotalCount(${pendingCellularTowers.size}) " +
                        "Flush:${lastFlushDurationMs}ms " +
                        if (highPerformanceMode) "HP" else "BAL"
            )
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun configureScanCadence() {
        if (highPerformanceMode) {
            wifiScanIntervalMs = HIGH_PERF_WIFI_SCAN_INTERVAL_MS
            bluetoothScanIntervalMs = HIGH_PERF_BLUETOOTH_SCAN_INTERVAL_MS
            cellularScanIntervalMs = HIGH_PERF_CELLULAR_SCAN_INTERVAL_MS
        } else {
            wifiScanIntervalMs = BALANCED_WIFI_SCAN_INTERVAL_MS
            bluetoothScanIntervalMs = BALANCED_BLUETOOTH_SCAN_INTERVAL_MS
            cellularScanIntervalMs = BALANCED_CELLULAR_SCAN_INTERVAL_MS
        }
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
        in 5955..7115 -> (freq - 5950) / 5
        else -> 0
    }

    private fun extractOui(bssid: String?): String {
        if (bssid.isNullOrBlank()) return ""
        val parts = bssid.split(":")
        return if (parts.size >= 3) {
            parts.take(3).joinToString(":") { it.uppercase() }
        } else {
            ""
        }
    }

    private fun wifiStandardToString(standard: Int): String {
        return when (standard) {
            1 -> "legacy"
            4 -> "802.11n"
            5 -> "802.11ac"
            6 -> "802.11ax"
            7 -> "802.11ad"
            8 -> "802.11be"
            else -> ""
        }
    }

    private fun serializeManufacturerData(
        manufacturerData: SparseArray<ByteArray>?
    ): String {
        if (manufacturerData == null || manufacturerData.size() == 0) {
            return ""
        }

        return buildString {
            for (index in 0 until manufacturerData.size()) {
                if (index > 0) append(";")
                val manufacturerId = manufacturerData.keyAt(index)
                val payload = manufacturerData.valueAt(index)
                append(manufacturerId)
                append(":")
                append(payload.joinToString("") { byte -> "%02X".format(byte) })
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopScanning()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        scope.cancel()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val BATCH_FLUSH_INTERVAL_MS = 1_000L
        private const val WIFI_BATCH_SIZE = 50
        private const val BLE_BATCH_SIZE = 100
        private const val BT_BATCH_SIZE = 50
        private const val CELL_BATCH_SIZE = 50
        private const val WIFI_QUEUE_LIMIT = 500
        private const val BLE_QUEUE_LIMIT = 1_000
        private const val BT_QUEUE_LIMIT = 500
        private const val CELL_QUEUE_LIMIT = 500
        private const val BALANCED_WIFI_SCAN_INTERVAL_MS = 5_000L
        private const val BALANCED_BLUETOOTH_SCAN_INTERVAL_MS = 6_000L
        private const val BALANCED_CELLULAR_SCAN_INTERVAL_MS = 5_000L
        private const val HIGH_PERF_WIFI_SCAN_INTERVAL_MS = 2_500L
        private const val HIGH_PERF_BLUETOOTH_SCAN_INTERVAL_MS = 3_500L
        private const val HIGH_PERF_CELLULAR_SCAN_INTERVAL_MS = 2_500L
        const val EXTRA_HIGH_PERFORMANCE = "high_performance"

        @Volatile
        private var latestCounts = ScanCounts(0, 0, 0, 0, 0, 0, false, 0, 0, 0, 0, 0)

        fun getCounts(context: Context): ScanCounts {
            return latestCounts
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
    val isScanning: Boolean,
    val wifiDropped: Int,
    val bleDropped: Int,
    val bluetoothDropped: Int,
    val cellDropped: Int,
    val lastFlushDurationMs: Long
)
