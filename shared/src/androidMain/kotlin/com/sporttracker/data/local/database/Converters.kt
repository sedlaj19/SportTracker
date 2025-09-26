package com.sporttracker.data.local.database

import androidx.room.TypeConverter
import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.model.SyncStatus

class Converters {
    @TypeConverter
    fun fromActivityType(type: ActivityType): String {
        return type.name
    }

    @TypeConverter
    fun toActivityType(type: String): ActivityType {
        return ActivityType.valueOf(type)
    }

    @TypeConverter
    fun fromStorageType(type: StorageType): String {
        return type.name
    }

    @TypeConverter
    fun toStorageType(type: String): StorageType {
        return StorageType.valueOf(type)
    }

    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSyncStatus(status: String): SyncStatus {
        return SyncStatus.valueOf(status)
    }
}