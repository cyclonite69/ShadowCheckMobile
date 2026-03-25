package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface BleDeviceRepository {
    fun getAllDevices(): Flow<List<BleDevice>>
    fun getDeviceByMacAddress(macAddress: String): Flow<BleDevice?>
    suspend fun insertDevice(device: BleDevice): Long
    suspend fun updateDevice(device: BleDevice)
    suspend fun deleteDevice(device: BleDevice)
}
