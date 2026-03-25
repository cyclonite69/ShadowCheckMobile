package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.CellularTower

@Entity(tableName = "cellular_towers")
data class CellularTowerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cellId: Int,
    val lac: Int,
    val mcc: Int,
    val mnc: Int,
    val signalStrength: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

fun CellularTowerEntity.toDomainModel(): CellularTower = CellularTower(
    cellId = cellId,
    mcc = mcc,
    mnc = mnc,
    lac = lac,
    signalStrength = signalStrength,
    timestamp = timestamp,
    latitude = latitude,
    longitude = longitude
)

fun CellularTower.toEntity(): CellularTowerEntity = CellularTowerEntity(
    cellId = cellId,
    lac = lac,
    mcc = mcc,
    mnc = mnc,
    signalStrength = signalStrength,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp
)
