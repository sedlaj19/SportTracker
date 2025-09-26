package com.sporttracker.data.datasource

import com.sporttracker.domain.model.SportActivity

interface RemoteDataSource {
    suspend fun getActivities(userId: String): Result<List<SportActivity>>
    suspend fun saveActivity(userId: String, activity: SportActivity): Result<Unit>
    suspend fun updateActivity(userId: String, activity: SportActivity): Result<Unit>
    suspend fun deleteActivity(userId: String, activityId: String): Result<Unit>
    suspend fun syncActivities(userId: String, activities: List<SportActivity>): Result<Unit>
}