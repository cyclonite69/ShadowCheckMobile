package com.shadowcheck.mobile.data.repository

import android.util.Log
import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.data.remote.WiGLEApiService
import com.shadowcheck.mobile.data.remote.dto.toEntity
import com.shadowcheck.mobile.di.IoDispatcher
import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiNetworkRepositoryImpl @Inject constructor(
    private val wifiDao: WifiNetworkDao,
    private val wigleService: WiGLEApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WifiNetworkRepository {

    override fun getAllNetworks(): Flow<List<WifiNetwork>> {
        return wifiDao.getAllNetworks().map { entities ->
            entities.map { it.toDomainModel() }
        }.distinctUntilChanged().flowOn(dispatcher)
    }

    override fun getNetworkById(ssid: String): Flow<WifiNetwork?> {
        return wifiDao.getNetworkBySsid(ssid).map { it?.toDomainModel() }.flowOn(dispatcher)
    }

    override fun getNetworksByBssid(bssid: String): Flow<List<WifiNetwork>> {
        return wifiDao.getNetworksByBssid(bssid).map { entities ->
            entities.map { it.toDomainModel() }
        }.flowOn(dispatcher)
    }

    override suspend fun insertNetwork(network: WifiNetwork): Long = withContext(dispatcher) {
        wifiDao.insertNetwork(network.toEntity())
    }

    override suspend fun updateNetwork(network: WifiNetwork) = withContext(dispatcher) {
        wifiDao.updateNetwork(network.toEntity())
    }

    override suspend fun deleteNetwork(ssid: String) = withContext(dispatcher) {
        wifiDao.deleteNetwork(ssid)
    }

    override fun searchNetworks(query: String): Flow<List<WifiNetwork>> {
        val formattedQuery = "%${query.replace(' ', '%')}%"
        return wifiDao.searchNetworks(formattedQuery).map { entities ->
            entities.map { it.toDomainModel() }
        }.flowOn(dispatcher)
    }

    override suspend fun syncWithWiGLE(apiKey: String) = withContext(dispatcher) {
        try {
            val response = wigleService.searchNetworks(apiKey = "Basic $apiKey")
            if (response.isSuccessful) {
                response.body()?.results?.let { dtos ->
                    val entities = dtos.map { it.toEntity() }
                    wifiDao.insertAll(entities)
                }
            } else {
                Log.e("WifiNetworkRepo", "WiGLE API Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("WifiNetworkRepo", "Failed to sync with WiGLE", e)
            // Do not propagate network errors for this operation
        }
    }
}
