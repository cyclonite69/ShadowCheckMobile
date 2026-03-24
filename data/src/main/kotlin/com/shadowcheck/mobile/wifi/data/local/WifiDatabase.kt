package com.shadowcheck.mobile.wifi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shadowcheck.mobile.wifi.data.local.dao.WifiNetworkDao
import com.shadowcheck.mobile.wifi.data.local.entity.WifiNetworkEntity

@Database(
    entities = [WifiNetworkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WifiDatabase : RoomDatabase() {
    abstract fun wifiNetworkDao(): WifiNetworkDao
}
