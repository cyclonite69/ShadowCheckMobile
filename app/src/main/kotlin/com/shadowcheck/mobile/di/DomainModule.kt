package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.domain.model.SurveillanceDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideSurveillanceDetector(): SurveillanceDetector {
        return SurveillanceDetector()
    }
}
