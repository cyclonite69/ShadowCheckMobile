package com.shadowcheck.mobile.core.model

data class ScanSession(
    val sessionId: String,
    val startedAt: Long,
    val endedAt: Long? = null,
    val highPerformanceMode: Boolean = false,
    val status: String = "active"
)
