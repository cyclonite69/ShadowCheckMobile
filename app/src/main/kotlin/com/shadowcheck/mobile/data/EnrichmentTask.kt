package com.shadowcheck.mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enrichment_tasks")
data class EnrichmentTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val networkId: String,
    val taskType: String,
    val status: String,
    val priority: Int = 0,
    val retryCount: Int = 0,
    val createdAt: Long,
    val completedAt: Long = 0
)
