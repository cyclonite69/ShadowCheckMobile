package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.domain.model.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BluetoothDeviceRepository {
    fun getAllDevices(): Flow<List<BluetoothDevice>>
    fun getDeviceByMacAddress(mac: String): Flow<BluetoothDevice?>
    suspend fun insertDevice(device: BluetoothDevice): Long
    suspend fun updateDevice(device: BluetoothDevice)
    suspend fun deleteDevice(mac: String)

    /**
     * Retrieves a [Flow] of Bluetooth devices that are currently nearby and
     * have a signal strength (RSSI) above a specified threshold.
     *
     * @param rssiThreshold The minimum RSSI value for a device to be considered nearby.
     * @return A [Flow] emitting a list of nearby [BluetoothDevice] objects.
     */
    fun getNearbyDevices(rssiThreshold: Int): Flow<List<BluetoothDevice>>
}
