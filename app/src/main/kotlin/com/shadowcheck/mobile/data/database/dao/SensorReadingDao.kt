package com.shadowcheck.mobile.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shadowcheck.mobile.data.database.model.SensorReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorReadingDao {
    @Query("SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT 100")
    fun getRecentReadings(): Flow<List<SensorReadingEntity>>

    @Insert
    suspend fun insertReading(reading: SensorReadingEntity)

    @Query("DELETE FROM sensor_readings WHERE timestamp < :cutoffTime")
    suspend fun deleteOlderThan(cutoffTime: Long)

    @Query("DELETE FROM sensor_readings")
    suspend fun deleteAll()
}
