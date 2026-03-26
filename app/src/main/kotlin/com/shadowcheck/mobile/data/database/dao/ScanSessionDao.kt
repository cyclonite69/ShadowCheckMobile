package com.shadowcheck.mobile.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shadowcheck.mobile.data.database.model.ScanSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanSessionDao {
    @Query("SELECT * FROM scan_sessions ORDER BY startedAt DESC LIMIT 1")
    fun getLatestSession(): Flow<ScanSessionEntity?>

    @Query("SELECT * FROM scan_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<ScanSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: ScanSessionEntity)

    @Query("UPDATE scan_sessions SET endedAt = :endedAt, status = 'completed' WHERE sessionId = :sessionId")
    suspend fun endSession(sessionId: String, endedAt: Long)
}
