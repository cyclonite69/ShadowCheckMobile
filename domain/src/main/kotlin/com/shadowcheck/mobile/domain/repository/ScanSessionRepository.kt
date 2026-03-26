package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.ScanSession
import kotlinx.coroutines.flow.Flow

interface ScanSessionRepository {
    fun getLatestSession(): Flow<ScanSession?>
    fun getAllSessions(): Flow<List<ScanSession>>
    suspend fun upsertSession(session: ScanSession)
    suspend fun endSession(sessionId: String, endedAt: Long)
}
