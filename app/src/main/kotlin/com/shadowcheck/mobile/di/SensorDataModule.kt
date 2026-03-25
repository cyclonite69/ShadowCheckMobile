package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.data.repository.HardwareMetadataRepositoryImpl
import com.shadowcheck.mobile.data.repository.SensorReadingRepositoryImpl
import com.shadowcheck.mobile.domain.repository.HardwareMetadataRepository
import com.shadowcheck.mobile.domain.repository.SensorReadingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorDataModule {

    @Binds
    @Singleton
    abstract fun bindSensorReadingRepository(
        impl: SensorReadingRepositoryImpl
    ): SensorReadingRepository

    @Binds
    @Singleton
    abstract fun bindHardwareMetadataRepository(
        impl: HardwareMetadataRepositoryImpl
    ): HardwareMetadataRepository
}
