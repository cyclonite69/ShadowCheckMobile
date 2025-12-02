package com.shadowcheck.mobile.rebuilt.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import com.shadowcheck.mobile.data.HardwareMetadata
import com.shadowcheck.mobile.data.SensorReading
import com.shadowcheck.mobile.data.ShadowCheckDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.NetworkInterface

class SensorCollectionService(private val context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val database = ShadowCheckDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private var currentLocation: Location? = null
    private val sensorListeners = mutableMapOf<Int, Boolean>()
    
    fun start() {
        captureDeviceHardware()
        registerSensors()
    }
    
    fun stop() {
        sensorManager.unregisterListener(this)
        sensorListeners.clear()
    }
    
    fun updateLocation(location: Location) {
        currentLocation = location
    }
    
    private fun registerSensors() {
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
                    SensorManager.SENSOR_DELAY_NORMAL
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
            timestamp = System.currentTimeMillis()
        )
        
        scope.launch {
            database.sensorReadingDao().insert(reading)
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    
    private fun captureDeviceHardware() {
        scope.launch {
            val macAddress = getMacAddress()
            val existing = database.hardwareMetadataDao().getByMac(macAddress)
            
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
                database.hardwareMetadataDao().insert(metadata)
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
