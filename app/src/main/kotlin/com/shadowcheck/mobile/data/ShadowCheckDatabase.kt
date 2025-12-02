package com.shadowcheck.mobile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WifiNetwork::class,
        BluetoothDevice::class,
        BleDevice::class,
        CellularTower::class,
        SensorReading::class,
        HardwareMetadata::class,
        RadioManufacturer::class,
        Geofence::class,
        NetworkNote::class,
        DeviceTag::class,
        ApiToken::class,
        ApiUsage::class,
        MediaAttachment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShadowCheckDatabase : RoomDatabase() {
    abstract fun wifiNetworkDao(): WifiNetworkDao
    abstract fun bluetoothDeviceDao(): BluetoothDeviceDao
    abstract fun bleDeviceDao(): BleDeviceDao
    abstract fun cellularTowerDao(): CellularTowerDao
    abstract fun sensorReadingDao(): SensorReadingDao
    abstract fun hardwareMetadataDao(): HardwareMetadataDao
    abstract fun radioManufacturerDao(): RadioManufacturerDao
    abstract fun geofenceDao(): GeofenceDao
    abstract fun networkNoteDao(): NetworkNoteDao
    abstract fun deviceTagDao(): DeviceTagDao
    abstract fun apiTokenDao(): ApiTokenDao

    companion object {
        @Volatile
        private var INSTANCE: ShadowCheckDatabase? = null

        fun getDatabase(context: Context): ShadowCheckDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShadowCheckDatabase::class.java,
                    "shadowcheck_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
