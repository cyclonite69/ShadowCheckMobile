package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBluetoothDevicesUseCase @Inject constructor(
    private val repository: BluetoothDeviceRepository
) {
    operator fun invoke(): Flow<List<BluetoothDevice>> {
        return repository.getAllDevices()
    }
}
