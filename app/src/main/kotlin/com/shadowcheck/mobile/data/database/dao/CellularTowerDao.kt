package com.shadowcheck.mobile.data.database.dao

import androidx.room.*
import com.shadowcheck.mobile.data.database.model.CellularTowerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CellularTowerDao {
    @Query("SELECT * FROM cellular_towers ORDER BY timestamp DESC")
    fun getAllTowers(): Flow<List<CellularTowerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTower(tower: CellularTowerEntity): Long

    @Update
    suspend fun updateTower(tower: CellularTowerEntity)
}
