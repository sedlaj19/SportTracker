package com.sporttracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ActivityEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SportTrackerDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}