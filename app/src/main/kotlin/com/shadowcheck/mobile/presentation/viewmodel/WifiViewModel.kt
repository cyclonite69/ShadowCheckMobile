package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCase
import com.shadowcheck.mobile.domain.usecase.SearchWifiNetworksUseCase
import com.shadowcheck.mobile.domain.usecase.SyncWiGLEUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WifiViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase,
    private val searchWifiNetworksUseCase: SearchWifiNetworksUseCase,
    private val syncWiGLEUseCase: SyncWiGLEUseCase
) : ViewModel() {

    private val _networks = MutableStateFlow<List<WifiNetwork>>(emptyList())
    val networks: StateFlow<List<WifiNetwork>> = _networks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadNetworks()
    }

    fun loadNetworks() {
        getAllWifiNetworksUseCase()
            .onEach { result ->
                _networks.value = result
            }
            .launchIn(viewModelScope)
    }

    fun search(query: String) {
        searchWifiNetworksUseCase(query)
            .onEach { result ->
                _networks.value = result
            }
            .launchIn(viewModelScope)
    }

    fun syncData(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                syncWiGLEUseCase(apiKey)
                // Refresh data after sync
                loadNetworks()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
