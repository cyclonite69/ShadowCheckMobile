package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.HardwareMetadata

@Entity(tableName = "hardware_metadata")
data class HardwareMetadataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val macAddress: String,
    val manufacturer: String = "",
    val model: String = "",
    val deviceType: String = "",
    val capabilities: String = "",
    val notes: String = "",
    val lastUpdated: Long
)

fun HardwareMetadataEntity.toDomainModel(): HardwareMetadata = HardwareMetadata(
    macAddress = macAddress,
    manufacturer = manufacturer,
    model = model,
    deviceType = deviceType,
    capabilities = capabilities,
    notes = notes,
    lastUpdated = lastUpdated
)

fun HardwareMetadata.toEntity(): HardwareMetadataEntity = HardwareMetadataEntity(
    macAddress = macAddress,
    manufacturer = manufacturer,
    model = model,
    deviceType = deviceType,
    capabilities = capabilities,
    notes = notes,
    lastUpdated = lastUpdated
)
