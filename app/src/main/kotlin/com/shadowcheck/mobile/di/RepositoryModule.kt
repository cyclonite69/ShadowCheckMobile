package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.data.repository.BluetoothDeviceRepositoryImpl
import com.shadowcheck.mobile.data.repository.CellularTowerRepositoryImpl
import com.shadowcheck.mobile.data.repository.WifiNetworkRepositoryImpl
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWifiNetworkRepository(
        impl: WifiNetworkRepositoryImpl
    ): WifiNetworkRepository

    @Binds
    @Singleton
    abstract fun bindBluetoothDeviceRepository(
        impl: BluetoothDeviceRepositoryImpl
    ): BluetoothDeviceRepository

    @Binds
    @Singleton
    abstract fun bindCellularTowerRepository(
        impl: CellularTowerRepositoryImpl
    ): CellularTowerRepository
}
