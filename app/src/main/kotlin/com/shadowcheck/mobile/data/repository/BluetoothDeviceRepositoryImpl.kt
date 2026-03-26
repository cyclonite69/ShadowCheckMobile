package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.core.di.IoDispatcher
import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.data.database.dao.BluetoothDeviceDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothDeviceRepositoryImpl @Inject constructor(
    private val bluetoothDao: BluetoothDeviceDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : BluetoothDeviceRepository {

    override fun getAllDevices(): Flow<List<BluetoothDevice>> {
        return bluetoothDao.getAllDevices().map { entities ->
            entities.map { it.toDomainModel() }
        }.flowOn(dispatcher)
    }

    override fun getDevicesByMacAddress(macAddress: String): Flow<List<BluetoothDevice>> {
        return bluetoothDao.getDevicesByMacAddress(macAddress)
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override fun getDeviceByMacAddress(macAddress: String): Flow<BluetoothDevice?> {
        return bluetoothDao.getDeviceByMacAddress(macAddress).map { it?.toDomainModel() }.flowOn(dispatcher)
    }

    override suspend fun insertDevice(device: BluetoothDevice): Long = withContext(dispatcher) {
        val deviceWithTimestamp = device.toEntity().copy(timestamp = System.currentTimeMillis())
        bluetoothDao.insertDevice(deviceWithTimestamp)
    }

    override suspend fun insertDevices(devices: List<BluetoothDevice>) = withContext(dispatcher) {
        bluetoothDao.insertDevices(
            devices.map { device ->
                device.toEntity().copy(timestamp = System.currentTimeMillis())
            }
        )
    }

    override suspend fun updateDevice(device: BluetoothDevice) = withContext(dispatcher) {
        val deviceWithTimestamp = device.toEntity().copy(timestamp = System.currentTimeMillis())
        bluetoothDao.updateDevice(deviceWithTimestamp)
    }

    override suspend fun deleteDevice(device: BluetoothDevice) = withContext(dispatcher) {
        bluetoothDao.deleteDevice(device.macAddress)
    }

    override fun getNearbyDevices(rssiThreshold: Int): Flow<List<BluetoothDevice>> {
        return bluetoothDao.getAllDevices().map { devices ->
            devices.filter { it.rssi >= rssiThreshold }.map { it.toDomainModel() }
        }.flowOn(dispatcher)
    }
}
