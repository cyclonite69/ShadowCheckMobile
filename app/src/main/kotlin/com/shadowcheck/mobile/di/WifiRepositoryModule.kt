package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.wifi.data.repository.WifiNetworkRepositoryImpl
import com.shadowcheck.mobile.wifi.domain.repository.WifiNetworkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WifiRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWifiNetworkRepository(
        impl: WifiNetworkRepositoryImpl
    ): WifiNetworkRepository
}
