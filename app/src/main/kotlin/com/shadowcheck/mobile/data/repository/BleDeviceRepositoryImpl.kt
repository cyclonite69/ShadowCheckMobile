package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.core.di.IoDispatcher
import com.shadowcheck.mobile.core.model.BleDevice
import com.shadowcheck.mobile.data.database.dao.BleDeviceDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.domain.repository.BleDeviceRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class BleDeviceRepositoryImpl @Inject constructor(
    private val bleDeviceDao: BleDeviceDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : BleDeviceRepository {

    override fun getAllDevices(): Flow<List<BleDevice>> {
        return bleDeviceDao.getAllDevices().map { entities ->
            entities.map { it.toDomainModel() }
        }.flowOn(dispatcher)
    }

    override fun getDeviceByMacAddress(macAddress: String): Flow<BleDevice?> {
        return bleDeviceDao.getDeviceByMacAddress(macAddress)
            .map { it?.toDomainModel() }
            .flowOn(dispatcher)
    }

    override suspend fun insertDevice(device: BleDevice): Long = withContext(dispatcher) {
        bleDeviceDao.insertDevice(device.toEntity())
    }

    override suspend fun updateDevice(device: BleDevice) = withContext(dispatcher) {
        bleDeviceDao.updateDevice(device.toEntity())
    }

    override suspend fun deleteDevice(device: BleDevice) = withContext(dispatcher) {
        bleDeviceDao.deleteDevice(device.macAddress)
    }
}
