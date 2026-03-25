package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.data.repository.GeofenceRepositoryImpl
import com.shadowcheck.mobile.domain.repository.GeofenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GeofenceModule {

    @Binds
    @Singleton
    abstract fun bindGeofenceRepository(
        impl: GeofenceRepositoryImpl
    ): GeofenceRepository
}
