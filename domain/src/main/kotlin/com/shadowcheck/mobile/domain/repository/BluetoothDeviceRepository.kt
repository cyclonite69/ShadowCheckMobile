package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BluetoothDeviceRepository {
    fun getAllDevices(): Flow<List<BluetoothDevice>>
    fun getDeviceByMacAddress(macAddress: String): Flow<BluetoothDevice?>
    suspend fun insertDevice(device: BluetoothDevice): Long
    suspend fun insertDevices(devices: List<BluetoothDevice>)
    suspend fun updateDevice(device: BluetoothDevice)
    suspend fun deleteDevice(device: BluetoothDevice)
    fun getNearbyDevices(rssiThreshold: Int): Flow<List<BluetoothDevice>>
}
