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
    val timestamp: Long,
    val psc: Int = 0,
    val signalQuality: Int = 0,
    val rawDbm: Int? = null,
    val rawAsuLevel: Int? = null,
    val rsrp: Int? = null,
    val rsrq: Int? = null,
    val rssnr: Int? = null,
    val cqi: Int? = null,
    val timingAdvance: Int? = null,
    val networkType: String = "",
    val operatorName: String = "",
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val firstSeen: Long = timestamp,
    val lastSeen: Long = timestamp,
    val source: String = ""
)

fun CellularTowerEntity.toDomainModel(): CellularTower = CellularTower(
    cellId = cellId,
    mcc = mcc,
    mnc = mnc,
    lac = lac,
    signalStrength = signalStrength,
    timestamp = timestamp,
    latitude = latitude,
    longitude = longitude,
    psc = psc,
    signalQuality = signalQuality,
    rawDbm = rawDbm,
    rawAsuLevel = rawAsuLevel,
    rsrp = rsrp,
    rsrq = rsrq,
    rssnr = rssnr,
    cqi = cqi,
    timingAdvance = timingAdvance,
    networkType = networkType,
    operatorName = operatorName,
    altitude = altitude,
    accuracy = accuracy,
    firstSeen = firstSeen,
    lastSeen = lastSeen,
    source = source
)

fun CellularTower.toEntity(): CellularTowerEntity = CellularTowerEntity(
    cellId = cellId,
    lac = lac,
    mcc = mcc,
    mnc = mnc,
    signalStrength = signalStrength,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    psc = psc,
    signalQuality = signalQuality,
    rawDbm = rawDbm,
    rawAsuLevel = rawAsuLevel,
    rsrp = rsrp,
    rsrq = rsrq,
    rssnr = rssnr,
    cqi = cqi,
    timingAdvance = timingAdvance,
    networkType = networkType,
    operatorName = operatorName,
    altitude = altitude,
    accuracy = accuracy,
    firstSeen = firstSeen,
    lastSeen = lastSeen,
    source = source
)
