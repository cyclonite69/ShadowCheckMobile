package com.shadowcheck.mobile.domain.usecase.bluetooth

import com.shadowcheck.mobile.domain.model.BluetoothDevice
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBluetoothDevicesUseCase @Inject constructor(
    private val repository: BluetoothDeviceRepository
) {
    operator fun invoke(): Flow<List<BluetoothDevice>> = repository.getAllDevices()
}

class GetNearbyBluetoothDevicesUseCase @Inject constructor(
    private val repository: BluetoothDeviceRepository
) {
    operator fun invoke(rssiThreshold: Int): Flow<List<BluetoothDevice>> = repository.getNearbyDevices(rssiThreshold)
}

class InsertBluetoothDeviceUseCase @Inject constructor(
    private val repository: BluetoothDeviceRepository
) {
    suspend operator fun invoke(device: BluetoothDevice) = repository.insertDevice(device)
}
