package com.shadowcheck.mobile.presentation.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.data.*
import com.shadowcheck.mobile.models.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

class MainViewModel(private val database: ShadowCheckDatabase) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeNetworks()
    }

    private fun observeNetworks() {
        viewModelScope.launch {
            database.wifiNetworkDao().getAllFlow().collect { networks ->
                _uiState.update { it.copy(wifiNetworks = networks, wifiCount = networks.size) }
            }
        }
        viewModelScope.launch {
            database.bluetoothDeviceDao().getAllFlow().collect { devices ->
                _uiState.update { it.copy(btDevices = devices, btCount = devices.size) }
            }
        }
        viewModelScope.launch {
            database.cellularTowerDao().getAllFlow().collect { towers ->
                _uiState.update { it.copy(cellTowers = towers, cellCount = towers.size) }
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
    fun selectAllBt() = _uiState.update { it.copy(selectedBtDevices = it.btDevices.map { d -> d.address }.toSet()) }
    fun deselectAllBt() = _uiState.update { it.copy(selectedBtDevices = emptySet()) }
    fun setLoading(loading: Boolean) = _uiState.update { it.copy(isLoading = loading) }
}
