package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNearbyBluetoothDevicesUseCase @Inject constructor(
    private val repository: BluetoothDeviceRepository
) {
    operator fun invoke(rssiThreshold: Int = -70): Flow<List<BluetoothDevice>> {
        return repository.getNearbyDevices(rssiThreshold)
            .map { devices ->
                devices.sortedByDescending { it.rssi }
            }
    }
}
