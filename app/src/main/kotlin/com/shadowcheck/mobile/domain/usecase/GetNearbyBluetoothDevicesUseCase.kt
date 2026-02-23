package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.BluetoothDevice
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to get nearby Bluetooth devices based on a signal strength threshold.
 *
 * @property bluetoothDeviceRepository The repository to fetch Bluetooth device data.
 */
@Singleton
class GetNearbyBluetoothDevicesUseCase @Inject constructor(
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) {
    /**
     * @param rssiThreshold The minimum signal strength to be considered "nearby".
     * @return A [Flow] of [BluetoothDevice] lists, filtered and sorted by signal strength.
     */
    operator fun invoke(rssiThreshold: Int = -80): Flow<List<BluetoothDevice>> {
        return bluetoothDeviceRepository.getNearbyDevices(rssiThreshold).map { devices ->
            devices.sortedByDescending { it.rssi }
        }
    }
}
