package com.shadowcheck.mobile.core.model

data class HeatmapPoint(
    val x: Double,
    val y: Double,
    val intensity: Double,
    val timestamp: Long
)

data class HeatmapLayer(
    val type: String,
    val points: List<HeatmapPoint>
)
