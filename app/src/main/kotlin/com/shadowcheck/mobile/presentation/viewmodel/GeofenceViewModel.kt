package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.core.model.Geofence
import com.shadowcheck.mobile.domain.repository.GeofenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GeofenceUiState(
    val geofences: List<Geofence> = emptyList()
)

@HiltViewModel
class GeofenceViewModel @Inject constructor(
    private val geofenceRepository: GeofenceRepository
) : ViewModel() {

    val uiState: StateFlow<GeofenceUiState> = geofenceRepository.getGeofences()
        .map { geofences -> GeofenceUiState(geofences = geofences) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GeofenceUiState()
        )

    fun saveGeofence(geofence: Geofence) {
        viewModelScope.launch {
            geofenceRepository.upsertGeofence(geofence)
        }
    }

    fun deleteGeofence(geofence: Geofence) {
        viewModelScope.launch {
            geofenceRepository.deleteGeofence(geofence)
        }
    }

    fun toggleGeofence(geofence: Geofence) {
        saveGeofence(geofence.copy(isActive = !geofence.isActive))
    }
}
