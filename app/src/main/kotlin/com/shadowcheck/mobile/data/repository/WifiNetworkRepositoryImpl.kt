package com.shadowcheck.mobile.data.repository

import android.util.Log
import com.shadowcheck.mobile.core.di.IoDispatcher
import com.shadowcheck.mobile.core.model.WifiNetwork
import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import com.shadowcheck.mobile.data.remote.WiGLEApiService
import com.shadowcheck.mobile.data.remote.dto.toEntity
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
            entities.map { it.toCoreModel() }
        }.distinctUntilChanged().flowOn(dispatcher)
    }

    override fun getNetworkById(ssid: String): Flow<WifiNetwork?> {
        return wifiDao.getNetworkBySsid(ssid).map { it?.toCoreModel() }.flowOn(dispatcher)
    }

    override fun getNetworksByBssid(bssid: String): Flow<List<WifiNetwork>> {
        return wifiDao.getNetworksByBssid(bssid).map { entities ->
            entities.map { it.toCoreModel() }
        }.flowOn(dispatcher)
    }

    override suspend fun insertNetwork(network: WifiNetwork): Long = withContext(dispatcher) {
        wifiDao.insertNetwork(network.toEntityModel())
    }

    override suspend fun updateNetwork(network: WifiNetwork) = withContext(dispatcher) {
        wifiDao.updateNetwork(network.toEntityModel())
    }

    override suspend fun deleteNetwork(ssid: String) = withContext(dispatcher) {
        wifiDao.deleteNetwork(ssid)
    }

    override fun searchNetworks(query: String): Flow<List<WifiNetwork>> {
        val formattedQuery = "%${query.replace(' ', '%')}%"
        return wifiDao.searchNetworks(formattedQuery).map { entities ->
            entities.map { it.toCoreModel() }
        }.flowOn(dispatcher)
    }

    override suspend fun syncWithWiGLE(apiKey: String): Int = withContext(dispatcher) {
        try {
            val response = wigleService.searchNetworks(apiKey = "Basic $apiKey")
            if (response.isSuccessful) {
                response.body()?.results?.let { dtos ->
                    val entities = dtos.map { it.toEntity() }
                    wifiDao.insertAll(entities)
                    return@withContext entities.size
                }
            } else {
                Log.e("WifiNetworkRepo", "WiGLE API Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("WifiNetworkRepo", "Failed to sync with WiGLE", e)
            // Do not propagate network errors for this operation
        }
        return@withContext 0
    }
}

private fun com.shadowcheck.mobile.data.database.model.WifiNetworkEntity.toCoreModel(): WifiNetwork =
    WifiNetwork(
        bssid = bssid,
        ssid = ssid,
        frequency = frequency,
        signalLevel = level,
        capabilities = capabilities,
        timestamp = timestamp
    )

private fun WifiNetwork.toEntityModel(): com.shadowcheck.mobile.data.database.model.WifiNetworkEntity =
    com.shadowcheck.mobile.data.database.model.WifiNetworkEntity(
        bssid = bssid,
        ssid = ssid,
        capabilities = capabilities,
        frequency = frequency,
        level = signalLevel,
        timestamp = timestamp
    )
