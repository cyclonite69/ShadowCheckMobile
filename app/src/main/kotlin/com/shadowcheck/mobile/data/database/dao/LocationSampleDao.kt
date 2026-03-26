package com.shadowcheck.mobile.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shadowcheck.mobile.data.database.model.LocationSampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationSampleDao {
    @Query("SELECT * FROM location_samples ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSamples(limit: Int): Flow<List<LocationSampleEntity>>

    @Query("SELECT * FROM location_samples WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getSamplesForSession(sessionId: String): Flow<List<LocationSampleEntity>>

    @Query("SELECT * FROM location_samples WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    fun getSamplesBetween(startTime: Long, endTime: Long): Flow<List<LocationSampleEntity>>

    @Insert
    suspend fun insertSample(sample: LocationSampleEntity)

    @Insert
    suspend fun insertSamples(samples: List<LocationSampleEntity>)

    @Query("DELETE FROM location_samples WHERE timestamp < :cutoffTime")
    suspend fun deleteOlderThan(cutoffTime: Long)
}
