package com.sporttracker.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.model.SyncStatus

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val location: String,
    val durationMinutes: Int,
    val storageType: StorageType,
    val createdAt: Long,
    val lastModified: Long,
    val syncStatus: SyncStatus,
    val isDeleted: Boolean,
    val userId: String?
)

fun ActivityEntity.toDomainModel(): SportActivity {
    return SportActivity(
        id = id,
        name = name,
        location = location,
        durationMinutes = durationMinutes,
        storageType = storageType,
        createdAt = createdAt,
        lastModified = lastModified,
        syncStatus = syncStatus,
        isDeleted = isDeleted,
        userId = userId
    )
}

fun SportActivity.toActivityEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        name = name,
        location = location,
        durationMinutes = durationMinutes,
        storageType = storageType,
        createdAt = createdAt,
        lastModified = lastModified,
        syncStatus = syncStatus,
        isDeleted = isDeleted,
        userId = userId
    )
}