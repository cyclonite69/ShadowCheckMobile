package com.shadowcheck.mobile.wifi.data.remote

import com.shadowcheck.mobile.wifi.data.remote.dto.WigleWifiSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WiGLEApiService {
    @GET("api/v2/network/search")
    suspend fun searchNetworks(
        @Header("Authorization") apiKey: String,
        @Query("onlymine") onlymine: Boolean = true,
        @Query("freenet") freenet: Boolean = false,
        @Query("paynet") paynet: Boolean = false
    ): Response<WigleWifiSearchResponse>
}
