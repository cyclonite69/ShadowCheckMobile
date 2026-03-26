package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.LocationSample
import com.shadowcheck.mobile.domain.repository.LocationSampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentLocationSamplesUseCase @Inject constructor(
    private val repository: LocationSampleRepository
) {
    operator fun invoke(limit: Int = 1000): Flow<List<LocationSample>> {
        return repository.getRecentSamples(limit)
    }
}
