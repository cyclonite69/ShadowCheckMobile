package com.shadowcheck.mobile.network.dto

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface WigleApi {
    
    @GET("api/v2/stats/user")
    suspend fun getUserStats(): WigleUserStats
    
    @GET("api/v2/network/search")
    suspend fun searchNetworksV2(
        @Query("ssid") ssid: String? = null,
        @Query("ssidlike") ssidLike: String? = null,
        @Query("netid") netId: String? = null,
        @Query("latrange1") latRange1: Double? = null,
        @Query("latrange2") latRange2: Double? = null,
        @Query("longrange1") longRange1: Double? = null,
        @Query("longrange2") longRange2: Double? = null,
        @Query("variance") variance: Double? = null,
        @Query("resultsPerPage") resultsPerPage: Int = 100
    ): WigleNetworkSearchResponse
    
    @GET("api/v3/network/search")
    suspend fun searchNetworksV3(
        @Query("ssid") ssid: String? = null,
        @Query("ssidlike") ssidLike: String? = null,
        @Query("netid") netId: String? = null,
        @Query("latrange1") latRange1: Double? = null,
        @Query("latrange2") latRange2: Double? = null,
        @Query("longrange1") longRange1: Double? = null,
        @Query("longrange2") longRange2: Double? = null,
        @Query("variance") variance: Double? = null,
        @Query("resultsPerPage") resultsPerPage: Int = 100,
        @Query("search_after") searchAfter: Long? = null
    ): WigleNetworkSearchResponse
    
    @POST("api/v2/file/upload")
    @Multipart
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("donate") donate: RequestBody
    ): WigleUploadResponse
    
    @GET("api/v2/file/transactions")
    suspend fun getTransactions(
        @Query("pagestart") pageStart: Int = 0
    ): WigleTransactionStatus
    
    @GET("api/v2/file/kml/{transid}")
    suspend fun getTransactionKml(
        @Path("transid") transId: String
    ): ResponseBody
}
