package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.data.repository.BleDeviceRepositoryImpl
import com.shadowcheck.mobile.domain.repository.BleDeviceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BleModule {

    @Binds
    @Singleton
    abstract fun bindBleDeviceRepository(
        impl: BleDeviceRepositoryImpl
    ): BleDeviceRepository
}
