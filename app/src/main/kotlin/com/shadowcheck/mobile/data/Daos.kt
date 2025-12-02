package com.shadowcheck.mobile.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiNetworkDao {
    @Query("SELECT * FROM wifi_networks ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<WifiNetwork>>
    
    @Query("SELECT * FROM wifi_networks GROUP BY bssid ORDER BY MAX(timestamp) DESC")
    fun getDistinctFlow(): Flow<List<WifiNetwork>>
    
    @Query("SELECT * FROM wifi_networks WHERE bssid = :bssid ORDER BY timestamp DESC")
    fun getSightingsByBssid(bssid: String): Flow<List<WifiNetwork>>

    @Query("SELECT * FROM wifi_networks WHERE bssid = :bssid LIMIT 1")
    suspend fun getByBssid(bssid: String): WifiNetwork?
    
    @Query("SELECT COUNT(DISTINCT bssid) FROM wifi_networks")
    suspend fun getUniqueCount(): Int
    
    @Query("SELECT COUNT(*) FROM wifi_networks")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM wifi_networks WHERE bssid = :bssid")
    suspend fun getSightingsCount(bssid: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(network: WifiNetwork): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(networks: List<WifiNetwork>)

    @Delete
    suspend fun delete(network: WifiNetwork)

    @Query("DELETE FROM wifi_networks")
    suspend fun deleteAll()
}

@Dao
interface BluetoothDeviceDao {
    @Query("SELECT * FROM bluetooth_devices ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<BluetoothDevice>>

    @Query("SELECT * FROM bluetooth_devices WHERE address = :address LIMIT 1")
    suspend fun getByAddress(address: String): BluetoothDevice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: BluetoothDevice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<BluetoothDevice>)

    @Delete
    suspend fun delete(device: BluetoothDevice)

    @Query("DELETE FROM bluetooth_devices")
    suspend fun deleteAll()
}

@Dao
interface BleDeviceDao {
    @Query("SELECT * FROM ble_devices ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<BleDevice>>
    
    @Query("SELECT * FROM ble_devices GROUP BY address ORDER BY MAX(timestamp) DESC")
    fun getDistinctFlow(): Flow<List<BleDevice>>
    
    @Query("SELECT * FROM ble_devices WHERE address = :address ORDER BY timestamp DESC")
    fun getSightingsByAddress(address: String): Flow<List<BleDevice>>
    
    @Query("SELECT * FROM ble_devices WHERE address = :address LIMIT 1")
    suspend fun getByAddress(address: String): BleDevice?
    
    @Query("SELECT COUNT(DISTINCT address) FROM ble_devices")
    suspend fun getUniqueCount(): Int
    
    @Query("SELECT COUNT(*) FROM ble_devices")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM ble_devices WHERE address = :address")
    suspend fun getSightingsCount(address: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: BleDevice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<BleDevice>)

    @Query("DELETE FROM ble_devices")
    suspend fun deleteAll()
}

@Dao
interface CellularTowerDao {
    @Query("SELECT * FROM cellular_towers ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<CellularTower>>
    
    @Query("SELECT * FROM cellular_towers GROUP BY cellId ORDER BY MAX(timestamp) DESC")
    fun getDistinctFlow(): Flow<List<CellularTower>>
    
    @Query("SELECT * FROM cellular_towers WHERE cellId = :cellId ORDER BY timestamp DESC")
    fun getSightingsByCellId(cellId: Int): Flow<List<CellularTower>>
    
    @Query("SELECT * FROM cellular_towers WHERE cellId = :cellId LIMIT 1")
    suspend fun getByCellId(cellId: Int): CellularTower?
    
    @Query("SELECT COUNT(DISTINCT cellId) FROM cellular_towers")
    suspend fun getUniqueCount(): Int
    
    @Query("SELECT COUNT(*) FROM cellular_towers")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM cellular_towers WHERE cellId = :cellId")
    suspend fun getSightingsCount(cellId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tower: CellularTower): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(towers: List<CellularTower>)

    @Query("DELETE FROM cellular_towers")
    suspend fun deleteAll()
}

@Dao
interface SensorReadingDao {
    @Query("SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT 100")
    fun getRecentFlow(): Flow<List<SensorReading>>

    @Insert
    suspend fun insert(reading: SensorReading)

    @Query("DELETE FROM sensor_readings WHERE timestamp < :cutoffTime")
    suspend fun deleteOlderThan(cutoffTime: Long)
    
    @Query("DELETE FROM sensor_readings")
    suspend fun deleteAll()
}

@Dao
interface HardwareMetadataDao {
    @Query("SELECT * FROM hardware_metadata WHERE macAddress = :mac LIMIT 1")
    suspend fun getByMac(mac: String): HardwareMetadata?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: HardwareMetadata)
}

@Dao
interface RadioManufacturerDao {
    @Query("SELECT * FROM radio_manufacturers WHERE ouiPrefix = :oui LIMIT 1")
    suspend fun getByOui(oui: String): RadioManufacturer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(manufacturers: List<RadioManufacturer>)
}

@Dao
interface GeofenceDao {
    @Query("SELECT * FROM geofences WHERE isActive = 1")
    fun getActiveFlow(): Flow<List<Geofence>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(geofence: Geofence)

    @Delete
    suspend fun delete(geofence: Geofence)
}

@Dao
interface NetworkNoteDao {
    @Query("SELECT * FROM network_notes WHERE networkId = :networkId AND networkType = :type")
    fun getNotesForNetwork(networkId: String, type: String): Flow<List<NetworkNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NetworkNote)

    @Delete
    suspend fun delete(note: NetworkNote)
}

@Dao
interface DeviceTagDao {
    @Query("SELECT * FROM device_tags WHERE deviceId = :deviceId")
    fun getTagsForDevice(deviceId: String): Flow<List<DeviceTag>>

    @Insert
    suspend fun insert(tag: DeviceTag)

    @Delete
    suspend fun delete(tag: DeviceTag)
}

@Dao
interface ApiTokenDao {
    @Query("SELECT * FROM api_tokens WHERE service = :service LIMIT 1")
    suspend fun getByService(service: String): ApiToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: ApiToken)
}
