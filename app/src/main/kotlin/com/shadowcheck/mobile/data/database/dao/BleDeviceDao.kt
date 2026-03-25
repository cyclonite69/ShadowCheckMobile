package com.shadowcheck.mobile.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shadowcheck.mobile.data.database.model.BleDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BleDeviceDao {
    @Query("SELECT * FROM ble_devices ORDER BY timestamp DESC")
    fun getAllDevices(): Flow<List<BleDeviceEntity>>

    @Query("SELECT * FROM ble_devices WHERE macAddress = :macAddress LIMIT 1")
    fun getDeviceByMacAddress(macAddress: String): Flow<BleDeviceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: BleDeviceEntity): Long

    @Update
    suspend fun updateDevice(device: BleDeviceEntity)

    @Query("DELETE FROM ble_devices WHERE macAddress = :macAddress")
    suspend fun deleteDevice(macAddress: String)
}
