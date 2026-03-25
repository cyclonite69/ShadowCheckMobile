package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.HardwareMetadata

interface HardwareMetadataRepository {
    suspend fun getByMac(macAddress: String): HardwareMetadata?
    suspend fun upsert(metadata: HardwareMetadata)
}
