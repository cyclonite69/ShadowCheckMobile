package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.data.repository.ScanSessionRepositoryImpl
import com.shadowcheck.mobile.domain.repository.ScanSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionDataModule {

    @Binds
    @Singleton
    abstract fun bindScanSessionRepository(
        impl: ScanSessionRepositoryImpl
    ): ScanSessionRepository
}
