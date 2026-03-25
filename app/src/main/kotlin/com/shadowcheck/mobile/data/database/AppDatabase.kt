package com.shadowcheck.mobile.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shadowcheck.mobile.data.database.dao.BleDeviceDao
import com.shadowcheck.mobile.data.database.dao.BluetoothDeviceDao
import com.shadowcheck.mobile.data.database.dao.CellularTowerDao
import com.shadowcheck.mobile.data.database.dao.GeofenceDao
import com.shadowcheck.mobile.data.database.dao.HardwareMetadataDao
import com.shadowcheck.mobile.data.database.dao.SensorReadingDao
import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import com.shadowcheck.mobile.data.database.model.BleDeviceEntity
import com.shadowcheck.mobile.data.database.model.BluetoothDeviceEntity
import com.shadowcheck.mobile.data.database.model.CellularTowerEntity
import com.shadowcheck.mobile.data.database.model.GeofenceEntity
import com.shadowcheck.mobile.data.database.model.HardwareMetadataEntity
import com.shadowcheck.mobile.data.database.model.SensorReadingEntity
import com.shadowcheck.mobile.data.database.model.WifiNetworkEntity

@Database(
    entities = [
        WifiNetworkEntity::class,
        BleDeviceEntity::class,
        BluetoothDeviceEntity::class,
        CellularTowerEntity::class,
        GeofenceEntity::class,
        SensorReadingEntity::class,
        HardwareMetadataEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wifiNetworkDao(): WifiNetworkDao
    abstract fun bleDeviceDao(): BleDeviceDao
    abstract fun bluetoothDeviceDao(): BluetoothDeviceDao
    abstract fun cellularTowerDao(): CellularTowerDao
    abstract fun geofenceDao(): GeofenceDao
    abstract fun sensorReadingDao(): SensorReadingDao
    abstract fun hardwareMetadataDao(): HardwareMetadataDao
}
