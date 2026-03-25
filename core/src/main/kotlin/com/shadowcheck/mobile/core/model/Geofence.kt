package com.shadowcheck.mobile.core.model

data class Geofence(
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val isActive: Boolean = true,
    val notifyOnEntry: Boolean = true,
    val notifyOnExit: Boolean = true,
    val createdAt: Long
)
