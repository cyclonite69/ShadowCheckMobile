package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.data.repository.LocationSampleRepositoryImpl
import com.shadowcheck.mobile.domain.repository.LocationSampleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationDataModule {

    @Binds
    @Singleton
    abstract fun bindLocationSampleRepository(
        impl: LocationSampleRepositoryImpl
    ): LocationSampleRepository
}
