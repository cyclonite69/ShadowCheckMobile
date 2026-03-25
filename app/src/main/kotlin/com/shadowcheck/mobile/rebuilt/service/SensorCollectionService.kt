package com.shadowcheck.mobile.rebuilt.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import com.shadowcheck.mobile.core.model.HardwareMetadata
import com.shadowcheck.mobile.core.model.SensorReading
import com.shadowcheck.mobile.domain.repository.HardwareMetadataRepository
import com.shadowcheck.mobile.domain.repository.SensorReadingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.NetworkInterface
import java.util.concurrent.ConcurrentLinkedQueue

class SensorCollectionService(
    private val context: Context,
    private val sensorReadingRepository: SensorReadingRepository,
    private val hardwareMetadataRepository: HardwareMetadataRepository,
    private val highPerformanceMode: Boolean = false
) : SensorEventListener {
    private companion object {
        const val SENSOR_BATCH_FLUSH_INTERVAL_MS = 500L
        const val SENSOR_BATCH_SIZE = 100
        const val SENSOR_QUEUE_LIMIT = 2_000
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private var currentLocation: Location? = null
    private val sensorListeners = mutableMapOf<Int, Boolean>()
    private val pendingSensorReadings = ConcurrentLinkedQueue<SensorReading>()
    private var sensorFlushJob: Job? = null
    private var droppedSensorReadings = 0
    
    fun start() {
        captureDeviceHardware()
        registerSensors()
        sensorFlushJob = scope.launch {
            while (true) {
                flushPendingSensorReadings()
                delay(SENSOR_BATCH_FLUSH_INTERVAL_MS)
            }
        }
    }
    
    fun stop() {
        sensorManager.unregisterListener(this)
        sensorListeners.clear()
        sensorFlushJob?.cancel()
        scope.launch {
            flushPendingSensorReadings()
        }
    }
    
    fun updateLocation(location: Location) {
        currentLocation = location
    }
    
    private fun registerSensors() {
        val sensorDelay = if (highPerformanceMode) {
            SensorManager.SENSOR_DELAY_GAME
        } else {
            SensorManager.SENSOR_DELAY_NORMAL
        }

        val sensors = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            Sensor.TYPE_RELATIVE_HUMIDITY,
            Sensor.TYPE_PROXIMITY,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_ROTATION_VECTOR,
            Sensor.TYPE_STEP_COUNTER,
            Sensor.TYPE_STEP_DETECTOR
        )
        
        sensors.forEach { type ->
            sensorManager.getDefaultSensor(type)?.let { sensor ->
                sensorManager.registerListener(
                    this,
                    sensor,
                    sensorDelay
                )
                sensorListeners[type] = true
            }
        }
    }
    
    override fun onSensorChanged(event: SensorEvent) {
        val location = currentLocation ?: return
        
        val sensorType = when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> "accelerometer"
            Sensor.TYPE_GYROSCOPE -> "gyroscope"
            Sensor.TYPE_MAGNETIC_FIELD -> "magnetometer"
            Sensor.TYPE_LIGHT -> "light"
            Sensor.TYPE_PRESSURE -> "pressure"
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "temperature"
            Sensor.TYPE_RELATIVE_HUMIDITY -> "humidity"
            Sensor.TYPE_PROXIMITY -> "proximity"
            Sensor.TYPE_GRAVITY -> "gravity"
            Sensor.TYPE_LINEAR_ACCELERATION -> "linear_acceleration"
            Sensor.TYPE_ROTATION_VECTOR -> "rotation_vector"
            Sensor.TYPE_STEP_COUNTER -> "step_counter"
            Sensor.TYPE_STEP_DETECTOR -> "step_detector"
            else -> "unknown"
        }
        
        val reading = SensorReading(
            sensorType = sensorType,
            valueX = event.values.getOrNull(0) ?: 0f,
            valueY = event.values.getOrNull(1) ?: 0f,
            valueZ = event.values.getOrNull(2) ?: 0f,
            accuracy = event.accuracy,
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = System.currentTimeMillis(),
            eventTimestampNanos = event.timestamp
        )
        
        scope.launch {
            enqueueSensorReading(reading)
        }
    }

    private suspend fun enqueueSensorReading(reading: SensorReading) {
        trimQueueIfNeeded()
        pendingSensorReadings.add(reading)
        if (pendingSensorReadings.size >= SENSOR_BATCH_SIZE) {
            flushPendingSensorReadings()
        }
    }

    private suspend fun flushPendingSensorReadings() {
        val batch = mutableListOf<SensorReading>()
        while (true) {
            val reading = pendingSensorReadings.poll() ?: break
            batch.add(reading)
        }
        if (batch.isNotEmpty()) {
            sensorReadingRepository.insertReadings(batch)
        }
    }

    private fun trimQueueIfNeeded() {
        while (pendingSensorReadings.size >= SENSOR_QUEUE_LIMIT) {
            if (pendingSensorReadings.poll() == null) break
            droppedSensorReadings++
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    
    private fun captureDeviceHardware() {
        scope.launch {
            val macAddress = getMacAddress()
            val existing = hardwareMetadataRepository.getByMac(macAddress)
            
            if (existing == null || System.currentTimeMillis() - existing.lastUpdated > 86400000) {
                val metadata = HardwareMetadata(
                    macAddress = macAddress,
                    manufacturer = Build.MANUFACTURER,
                    model = "${Build.BRAND} ${Build.MODEL}",
                    deviceType = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})",
                    capabilities = buildCapabilitiesString(),
                    notes = buildDeviceSpecs(),
                    lastUpdated = System.currentTimeMillis()
                )
                hardwareMetadataRepository.upsert(metadata)
            }
        }
    }
    
    private fun getMacAddress(): String {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .firstOrNull { it.name == "wlan0" }
                ?.hardwareAddress
                ?.joinToString(":") { "%02X".format(it) }
                ?: Build.ID
        } catch (e: Exception) {
            Build.ID
        }
    }
    
    private fun buildCapabilitiesString(): String {
        val caps = mutableListOf<String>()
        
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) caps.add("accelerometer")
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) caps.add("gyroscope")
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) caps.add("magnetometer")
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) caps.add("light")
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) caps.add("barometer")
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) caps.add("thermometer")
        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) caps.add("hygrometer")
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) caps.add("proximity")
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) caps.add("pedometer")
        
        return caps.joinToString(", ")
    }
    
    private fun buildDeviceSpecs(): String {
        return buildString {
            append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
            append("Brand: ${Build.BRAND}\n")
            append("Product: ${Build.PRODUCT}\n")
            append("Hardware: ${Build.HARDWARE}\n")
            append("Board: ${Build.BOARD}\n")
            append("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
            append("Build: ${Build.DISPLAY}\n")
            append("Fingerprint: ${Build.FINGERPRINT}\n")
            append("Bootloader: ${Build.BOOTLOADER}\n")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                append("Security Patch: ${Build.VERSION.SECURITY_PATCH}\n")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                append("SOC: ${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}\n")
            }
            append("Supported ABIs: ${Build.SUPPORTED_ABIS.joinToString(", ")}\n")
            append("Sensors: ${sensorManager.getSensorList(Sensor.TYPE_ALL).size} total\n")
            sensorManager.getSensorList(Sensor.TYPE_ALL).forEach { sensor ->
                append("  - ${sensor.name} (${sensor.vendor})\n")
            }
        }
    }
}
