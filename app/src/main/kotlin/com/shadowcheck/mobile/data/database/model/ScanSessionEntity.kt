package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.ScanSession

@Entity(tableName = "scan_sessions")
data class ScanSessionEntity(
    @PrimaryKey val sessionId: String,
    val startedAt: Long,
    val endedAt: Long? = null,
    val highPerformanceMode: Boolean = false,
    val status: String = "active"
)

fun ScanSessionEntity.toDomainModel(): ScanSession = ScanSession(
    sessionId = sessionId,
    startedAt = startedAt,
    endedAt = endedAt,
    highPerformanceMode = highPerformanceMode,
    status = status
)

fun ScanSession.toEntity(): ScanSessionEntity = ScanSessionEntity(
    sessionId = sessionId,
    startedAt = startedAt,
    endedAt = endedAt,
    highPerformanceMode = highPerformanceMode,
    status = status
)
