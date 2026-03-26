package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.ScanSession
import com.shadowcheck.mobile.domain.repository.ScanSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllScanSessionsUseCase @Inject constructor(
    private val repository: ScanSessionRepository
) {
    operator fun invoke(): Flow<List<ScanSession>> {
        return repository.getAllSessions()
    }
}
