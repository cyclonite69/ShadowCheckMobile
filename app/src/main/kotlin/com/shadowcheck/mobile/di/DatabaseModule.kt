package com.shadowcheck.mobile.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shadowcheck.mobile.data.database.AppDatabase
import com.shadowcheck.mobile.data.database.dao.BleDeviceDao
import com.shadowcheck.mobile.data.database.dao.BluetoothDeviceDao
import com.shadowcheck.mobile.data.database.dao.CellularTowerDao
import com.shadowcheck.mobile.data.database.dao.GeofenceDao
import com.shadowcheck.mobile.data.database.dao.HardwareMetadataDao
import com.shadowcheck.mobile.data.database.dao.LocationSampleDao
import com.shadowcheck.mobile.data.database.dao.ScanSessionDao
import com.shadowcheck.mobile.data.database.dao.SensorReadingDao
import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE cellular_towers ADD COLUMN rawDbm INTEGER")
            database.execSQL("ALTER TABLE cellular_towers ADD COLUMN rawAsuLevel INTEGER")
            database.execSQL("ALTER TABLE cellular_towers ADD COLUMN rsrp INTEGER")
            database.execSQL("ALTER TABLE cellular_towers ADD COLUMN rsrq INTEGER")
            database.execSQL("ALTER TABLE cellular_towers ADD COLUMN rssnr INTEGER")
            database.execSQL("ALTER TABLE cellular_towers ADD COLUMN cqi INTEGER")
            database.execSQL("ALTER TABLE cellular_towers ADD COLUMN timingAdvance INTEGER")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE sensor_readings ADD COLUMN eventTimestampNanos INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS location_samples (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    timestamp INTEGER NOT NULL,
                    altitude REAL,
                    accuracy REAL,
                    speed REAL,
                    bearing REAL,
                    provider TEXT NOT NULL DEFAULT '',
                    elapsedRealtimeNanos INTEGER NOT NULL DEFAULT 0,
                    verticalAccuracyMeters REAL
                )
                """.trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_location_samples_timestamp ON location_samples(timestamp)"
            )
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE location_samples ADD COLUMN sessionId TEXT NOT NULL DEFAULT ''"
            )
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS scan_sessions (
                    sessionId TEXT NOT NULL PRIMARY KEY,
                    startedAt INTEGER NOT NULL,
                    endedAt INTEGER,
                    highPerformanceMode INTEGER NOT NULL DEFAULT 0,
                    status TEXT NOT NULL DEFAULT 'active'
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE wifi_networks ADD COLUMN sessionId TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE ble_devices ADD COLUMN sessionId TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE bluetooth_devices ADD COLUMN sessionId TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE cellular_towers ADD COLUMN sessionId TEXT NOT NULL DEFAULT ''")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shadowcheck.db"
        )
        .addMigrations(*getAllMigrations())
        .build()
    }

    private fun getAllMigrations(): Array<Migration> {
        return arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
    }

    @Provides
    fun provideWifiNetworkDao(database: AppDatabase): WifiNetworkDao {
        return database.wifiNetworkDao()
    }

    @Provides
    fun provideBluetoothDeviceDao(database: AppDatabase): BluetoothDeviceDao {
        return database.bluetoothDeviceDao()
    }

    @Provides
    fun provideBleDeviceDao(database: AppDatabase): BleDeviceDao {
        return database.bleDeviceDao()
    }

    @Provides
    fun provideCellularTowerDao(database: AppDatabase): CellularTowerDao {
        return database.cellularTowerDao()
    }

    @Provides
    fun provideGeofenceDao(database: AppDatabase): GeofenceDao {
        return database.geofenceDao()
    }

    @Provides
    fun provideSensorReadingDao(database: AppDatabase): SensorReadingDao {
        return database.sensorReadingDao()
    }

    @Provides
    fun provideHardwareMetadataDao(database: AppDatabase): HardwareMetadataDao {
        return database.hardwareMetadataDao()
    }

    @Provides
    fun provideLocationSampleDao(database: AppDatabase): LocationSampleDao {
        return database.locationSampleDao()
    }

    @Provides
    fun provideScanSessionDao(database: AppDatabase): ScanSessionDao {
        return database.scanSessionDao()
    }
}
