package com.shadowcheck.mobile.presentation.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.BluetoothDevice
import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.core.model.WifiNetwork
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCase
import com.shadowcheck.mobile.domain.usecase.SearchWifiNetworksUseCase
import com.shadowcheck.mobile.domain.usecase.SyncWiGLEUseCase
import com.shadowcheck.mobile.models.BluetoothFilters
import com.shadowcheck.mobile.models.WiFiFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

data class MainUiState(
    val isScanning: Boolean = false,
    val wifiCount: Int = 0,
    val cellCount: Int = 0,
    val btCount: Int = 0,
    val currentScreen: String = "home",
    val mapProvider: String = "mapbox",
    val wifiNetworks: List<WifiNetwork> = emptyList(),
    val btDevices: List<BluetoothDevice> = emptyList(),
    val cellTowers: List<CellularTower> = emptyList(),
    val currentLocation: Location? = null,
    val gpsStatus: String = "No GPS",
    val satelliteCount: Int = 0,
    val searchQuery: String = "",
    val filterStrength: Int = -100,
    val selectedNetworkBssid: String? = null,
    val scanIntervalSeconds: Int = 3,
    val wifiFilters: WiFiFilters = WiFiFilters(),
    val btFilters: BluetoothFilters = BluetoothFilters(),
    val showWiFiOnMap: Boolean = false,
    val showBluetoothOnMap: Boolean = false,
    val showFilters: Boolean = false,
    val selectedWifiNetworks: Set<String> = emptySet(),
    val selectedBtDevices: Set<String> = emptySet(),
    val showNetworkList: Boolean = false,
    val isLoading: Boolean = false,
    val show3D: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase,
    private val searchWifiNetworksUseCase: SearchWifiNetworksUseCase,
    private val syncWiGLEUseCase: SyncWiGLEUseCase,
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase,
    private val getAllCellularTowersUseCase: GetAllCellularTowersUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeNetworks()
    }

    private fun observeNetworks() {
        getAllWifiNetworksUseCase()
            .onEach { networks ->
                _uiState.update { it.copy(wifiNetworks = networks, wifiCount = networks.size) }
            }
            .catch { e -> Log.e("MainViewModel", "Error observing wifi networks", e) }
            .launchIn(viewModelScope)

        getAllBluetoothDevicesUseCase()
            .onEach { devices ->
                _uiState.update { it.copy(btDevices = devices, btCount = devices.size) }
            }
            .catch { e -> Log.e("MainViewModel", "Error observing bluetooth devices", e) }
            .launchIn(viewModelScope)

        getAllCellularTowersUseCase()
            .onEach { towers ->
                _uiState.update { it.copy(cellTowers = towers, cellCount = towers.size) }
            }
            .catch { e -> Log.e("MainViewModel", "Error observing cellular towers", e) }
            .launchIn(viewModelScope)
    }
    
    fun onSearchQueryChanged(query: String) {
        updateSearchQuery(query)
        if (query.length < 2) {
            observeNetworks() // a blank query should return all networks
        } else {
            searchWifiNetworksUseCase(query)
                .onEach { networks ->
                    _uiState.update { it.copy(wifiNetworks = networks, wifiCount = networks.size) }
                }
                .catch { e -> Log.e("MainViewModel", "Error searching wifi networks", e) }
                .launchIn(viewModelScope)
        }
    }

    fun syncWithWiGLE(apiKey: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                withTimeout(30_000) {
                    syncWiGLEUseCase(apiKey)
                        .onSuccess { count ->
                            // Optionally, we could show a toast with the number of synced networks.
                            // For now, we just reload the networks.
                            Log.i("MainViewModel", "Synced $count networks from WiGLE.")
                        }
                        .onFailure { error ->
                            Log.e("MainViewModel", "Failed to sync with WiGLE", error)
                        }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Sync timed out or failed", e)
            } finally {
                setLoading(false)
            }
        }
    }

    fun updateScanning(isScanning: Boolean) = _uiState.update { it.copy(isScanning = isScanning) }
    fun updateLocation(location: Location?) = _uiState.update { it.copy(currentLocation = location) }
    fun updateGpsStatus(status: String, count: Int = 0) = _uiState.update { it.copy(gpsStatus = status, satelliteCount = count) }
    fun navigateTo(screen: String) = _uiState.update { it.copy(currentScreen = screen) }
    fun updateSearchQuery(query: String) = _uiState.update { it.copy(searchQuery = query) }
    fun updateFilterStrength(strength: Int) = _uiState.update { it.copy(filterStrength = strength) }
    fun selectNetwork(bssid: String?) = _uiState.update { it.copy(selectedNetworkBssid = bssid) }
    fun updateScanInterval(seconds: Int) = _uiState.update { it.copy(scanIntervalSeconds = seconds) }
    fun updateMapProvider(provider: String) = _uiState.update { it.copy(mapProvider = provider) }
    fun updateWiFiFilters(filters: WiFiFilters) = _uiState.update { it.copy(wifiFilters = filters) }
    fun updateBtFilters(filters: BluetoothFilters) = _uiState.update { it.copy(btFilters = filters) }
    fun toggleWiFiOnMap() = _uiState.update { it.copy(showWiFiOnMap = !it.showWiFiOnMap) }
    fun toggleBluetoothOnMap() = _uiState.update { it.copy(showBluetoothOnMap = !it.showBluetoothOnMap) }
    fun toggleFilters() = _uiState.update { it.copy(showFilters = !it.showFilters) }
    fun toggleNetworkList() = _uiState.update { it.copy(showNetworkList = !it.showNetworkList) }
    fun toggle3D() = _uiState.update { it.copy(show3D = !it.show3D) }
    fun toggleWiFiSelection(bssid: String) = _uiState.update {
        it.copy(selectedWifiNetworks = if (bssid in it.selectedWifiNetworks) it.selectedWifiNetworks - bssid else it.selectedWifiNetworks + bssid)
    }
    fun toggleBtSelection(address: String) = _uiState.update {
        it.copy(selectedBtDevices = if (address in it.selectedBtDevices) it.selectedBtDevices - address else it.selectedBtDevices + address)
    }
    fun selectAllWiFi() = _uiState.update { it.copy(selectedWifiNetworks = it.wifiNetworks.map { n -> n.bssid }.toSet()) }
    fun deselectAllWiFi() = _uiState.update { it.copy(selectedWifiNetworks = emptySet()) }
    fun selectAllBt() = _uiState.update { it.copy(selectedBtDevices = it.btDevices.map { d -> d.macAddress }.toSet()) }
    fun deselectAllBt() = _uiState.update { it.copy(selectedBtDevices = emptySet()) }
    fun setLoading(loading: Boolean) = _uiState.update { it.copy(isLoading = loading) }
}
