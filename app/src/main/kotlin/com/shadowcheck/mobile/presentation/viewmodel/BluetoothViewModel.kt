package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.BluetoothDevice
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetNearbyBluetoothDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase,
    private val getNearbyBluetoothDevicesUseCase: GetNearbyBluetoothDevicesUseCase
) : ViewModel() {

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices.asStateFlow()

    init {
        loadAllDevices()
    }

    fun loadAllDevices() {
        getAllBluetoothDevicesUseCase()
            .onEach { result ->
                _devices.value = result
            }
            .launchIn(viewModelScope)
    }

    fun findNearbyDevices(rssiThreshold: Int = -70) {
        getNearbyBluetoothDevicesUseCase(rssiThreshold)
            .onEach { result ->
                _devices.value = result
            }
            .launchIn(viewModelScope)
    }
}