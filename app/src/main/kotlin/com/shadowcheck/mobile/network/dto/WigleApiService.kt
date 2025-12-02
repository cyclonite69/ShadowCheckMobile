package com.shadowcheck.mobile.network.dto

import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class WigleApiService(
    private val apiName: String,
    private val apiToken: String
) {
    
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", Credentials.basic(apiName, apiToken))
                .build()
            chain.proceed(request)
        }
        .build()
    
    private val api: WigleApi = Retrofit.Builder()
        .baseUrl("https://api.wigle.net/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WigleApi::class.java)
    
    suspend fun getUserStats(): WigleUserStats {
        return api.getUserStats()
    }
    
    suspend fun searchNetworks(
        ssid: String? = null,
        ssidLike: String? = null,
        netId: String? = null,
        latRange1: Double? = null,
        latRange2: Double? = null,
        longRange1: Double? = null,
        longRange2: Double? = null,
        variance: Double? = null,
        resultsPerPage: Int = 100
    ): WigleNetworkSearchResponse {
        return api.searchNetworksV2(
            ssid, ssidLike, netId,
            latRange1, latRange2,
            longRange1, longRange2,
            variance, resultsPerPage
        )
    }
    
    suspend fun uploadFile(file: File, donate: Boolean = false): WigleUploadResponse {
        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        )
        
        val donateBody = (if (donate) "on" else "off")
            .toRequestBody("text/plain".toMediaTypeOrNull())
        
        return api.uploadFile(filePart, donateBody)
    }
    
    suspend fun getTransactions(pageStart: Int = 0): WigleTransactionStatus {
        return api.getTransactions(pageStart)
    }
    
    suspend fun downloadTransactionKml(transId: String): ByteArray {
        return api.getTransactionKml(transId).bytes()
    }
    
    companion object {
        fun create(apiName: String, apiToken: String): WigleApiService {
            return WigleApiService(apiName, apiToken)
        }
    }
}
