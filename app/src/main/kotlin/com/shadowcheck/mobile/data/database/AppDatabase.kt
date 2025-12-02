package com.shadowcheck.mobile.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shadowcheck.mobile.data.database.dao.BluetoothDeviceDao
import com.shadowcheck.mobile.data.database.dao.CellularTowerDao
import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import com.shadowcheck.mobile.data.database.model.BluetoothDeviceEntity
import com.shadowcheck.mobile.data.database.model.CellularTowerEntity
import com.shadowcheck.mobile.data.database.model.WifiNetworkEntity

@Database(
    entities = [
        WifiNetworkEntity::class,
        BluetoothDeviceEntity::class,
        CellularTowerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wifiNetworkDao(): WifiNetworkDao
    abstract fun bluetoothDeviceDao(): BluetoothDeviceDao
    abstract fun cellularTowerDao(): CellularTowerDao
}
