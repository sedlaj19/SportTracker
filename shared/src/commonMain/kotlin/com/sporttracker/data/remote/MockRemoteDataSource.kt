package com.sporttracker.data.remote

import com.sporttracker.data.datasource.RemoteDataSource
import com.sporttracker.domain.model.SportActivity
import kotlinx.coroutines.delay

class MockRemoteDataSource : RemoteDataSource {
    private val remoteActivities = mutableListOf<SportActivity>()

    override suspend fun getActivities(userId: String): Result<List<SportActivity>> {
        delay(500) // Simulate network delay
        return Result.success(remoteActivities.filter { it.userId == userId })
    }

    override suspend fun saveActivity(userId: String, activity: SportActivity): Result<Unit> {
        delay(500)
        remoteActivities.add(activity.copy(userId = userId))
        return Result.success(Unit)
    }

    override suspend fun updateActivity(userId: String, activity: SportActivity): Result<Unit> {
        delay(500)
        val index = remoteActivities.indexOfFirst { it.id == activity.id }
        if (index != -1) {
            remoteActivities[index] = activity.copy(userId = userId)
        }
        return Result.success(Unit)
    }

    override suspend fun deleteActivity(userId: String, activityId: String): Result<Unit> {
        delay(500)
        remoteActivities.removeAll { it.id == activityId && it.userId == userId }
        return Result.success(Unit)
    }

    override suspend fun syncActivities(userId: String, activities: List<SportActivity>): Result<Unit> {
        delay(1000)
        // In real implementation, this would handle conflict resolution
        return Result.success(Unit)
    }
}