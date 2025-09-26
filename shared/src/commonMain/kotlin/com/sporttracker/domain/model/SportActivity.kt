package com.sporttracker.domain.model

import kotlin.time.ExperimentalTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@OptIn(ExperimentalTime::class)
@Serializable
data class SportActivity(
    val id: String = generateId(),
    val name: String,
    val location: String,
    val durationMinutes: Int,
    val activityType: ActivityType = ActivityType.OTHER,
    val storageType: StorageType,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val lastModified: Long = Clock.System.now().toEpochMilliseconds(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val isDeleted: Boolean = false,
    val userId: String? = null
)

@Serializable
enum class StorageType {
    LOCAL,
    REMOTE
}

@Serializable
enum class SyncStatus {
    SYNCED,
    PENDING,
    ERROR
}

@OptIn(ExperimentalTime::class)
private fun generateId(): String {
    return Clock.System.now().toEpochMilliseconds().toString()
}