package com.shadowcheck.mobile.wifi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shadowcheck.mobile.wifi.data.local.entity.WifiNetworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiNetworkDao {
    @Query("SELECT * FROM wifi_networks ORDER BY timestamp DESC")
    fun getAllNetworks(): Flow<List<WifiNetworkEntity>>

    @Query("SELECT * FROM wifi_networks WHERE ssid = :ssid LIMIT 1")
    fun getNetworkBySsid(ssid: String): Flow<WifiNetworkEntity?>

    @Query("SELECT * FROM wifi_networks WHERE bssid = :bssid")
    fun getNetworksByBssid(bssid: String): Flow<List<WifiNetworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetwork(network: WifiNetworkEntity): Long

    @Update
    suspend fun updateNetwork(network: WifiNetworkEntity)

    @Query("DELETE FROM wifi_networks WHERE ssid = :ssid")
    suspend fun deleteNetwork(ssid: String)

    @Query("SELECT * FROM wifi_networks WHERE ssid LIKE :query OR bssid LIKE :query")
    fun searchNetworks(query: String): Flow<List<WifiNetworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(networks: List<WifiNetworkEntity>)
}
