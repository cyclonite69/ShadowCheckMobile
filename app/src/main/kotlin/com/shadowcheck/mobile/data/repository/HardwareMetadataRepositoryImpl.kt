package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.core.di.IoDispatcher
import com.shadowcheck.mobile.core.model.HardwareMetadata
import com.shadowcheck.mobile.data.database.dao.HardwareMetadataDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.domain.repository.HardwareMetadataRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class HardwareMetadataRepositoryImpl @Inject constructor(
    private val hardwareMetadataDao: HardwareMetadataDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : HardwareMetadataRepository {

    override suspend fun getByMac(macAddress: String): HardwareMetadata? = withContext(dispatcher) {
        hardwareMetadataDao.getByMacAddress(macAddress)?.toDomainModel()
    }

    override suspend fun upsert(metadata: HardwareMetadata) = withContext(dispatcher) {
        hardwareMetadataDao.insert(metadata.toEntity())
    }
}
