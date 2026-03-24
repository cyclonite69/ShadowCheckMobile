package com.shadowcheck.mobile.di

import android.content.Context
import androidx.room.Room
import com.shadowcheck.mobile.wifi.data.local.WifiDatabase
import com.shadowcheck.mobile.wifi.data.local.dao.WifiNetworkDao
import com.shadowcheck.mobile.wifi.data.remote.WiGLEApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WifiDataModule {

    @Provides
    @Singleton
    fun provideWifiDatabase(@ApplicationContext context: Context): WifiDatabase {
        return Room.databaseBuilder(
            context,
            WifiDatabase::class.java,
            "shadowcheck_wifi.db"
        ).build()
    }

    @Provides
    fun provideWifiNetworkDao(database: WifiDatabase): WifiNetworkDao {
        return database.wifiNetworkDao()
    }

    @Provides
    @Singleton
    @Named("wifi")
    fun provideWifiOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("wifi")
    fun provideWifiRetrofit(@Named("wifi") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.wigle.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWiGLEApiService(@Named("wifi") retrofit: Retrofit): WiGLEApiService {
        return retrofit.create(WiGLEApiService::class.java)
    }
}
