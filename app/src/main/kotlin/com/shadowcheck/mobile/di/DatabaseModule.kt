package com.shadowcheck.mobile.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import com.shadowcheck.mobile.data.database.AppDatabase
import com.shadowcheck.mobile.data.database.dao.BluetoothDeviceDao
import com.shadowcheck.mobile.data.database.dao.CellularTowerDao
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
        // Placeholder for database migrations
        return arrayOf()
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
    fun provideCellularTowerDao(database: AppDatabase): CellularTowerDao {
        return database.cellularTowerDao()
    }
}
