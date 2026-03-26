package com.shadowcheck.mobile.data.database.dao

import androidx.room.*
import com.shadowcheck.mobile.data.database.model.BluetoothDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BluetoothDeviceDao {
    @Query("SELECT * FROM bluetooth_devices ORDER BY timestamp DESC")
    fun getAllDevices(): Flow<List<BluetoothDeviceEntity>>

    @Query("SELECT * FROM bluetooth_devices WHERE macAddress = :macAddress ORDER BY timestamp ASC")
    fun getDevicesByMacAddress(macAddress: String): Flow<List<BluetoothDeviceEntity>>

    @Query("SELECT * FROM bluetooth_devices WHERE macAddress = :macAddress LIMIT 1")
    fun getDeviceByMacAddress(macAddress: String): Flow<BluetoothDeviceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: BluetoothDeviceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<BluetoothDeviceEntity>)

    @Update
    suspend fun updateDevice(device: BluetoothDeviceEntity)

    @Query("DELETE FROM bluetooth_devices WHERE macAddress = :macAddress")
    suspend fun deleteDevice(macAddress: String)
}
