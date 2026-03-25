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
        return arrayOf(MIGRATION_1_2, MIGRATION_2_3)
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
}
