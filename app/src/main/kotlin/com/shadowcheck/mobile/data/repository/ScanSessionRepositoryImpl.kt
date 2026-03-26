package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.core.di.IoDispatcher
import com.shadowcheck.mobile.core.model.ScanSession
import com.shadowcheck.mobile.data.database.dao.ScanSessionDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.domain.repository.ScanSessionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class ScanSessionRepositoryImpl @Inject constructor(
    private val scanSessionDao: ScanSessionDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ScanSessionRepository {

    override fun getLatestSession(): Flow<ScanSession?> {
        return scanSessionDao.getLatestSession()
            .map { it?.toDomainModel() }
            .flowOn(dispatcher)
    }

    override fun getAllSessions(): Flow<List<ScanSession>> {
        return scanSessionDao.getAllSessions()
            .map { sessions -> sessions.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override suspend fun upsertSession(session: ScanSession) = withContext(dispatcher) {
        scanSessionDao.upsertSession(session.toEntity())
    }

    override suspend fun endSession(sessionId: String, endedAt: Long) = withContext(dispatcher) {
        scanSessionDao.endSession(sessionId, endedAt)
    }
}
