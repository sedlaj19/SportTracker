package com.sporttracker.domain.repository

import com.sporttracker.domain.model.SportActivity
import kotlinx.coroutines.flow.Flow

interface SportActivityRepository {
    fun observeActivities(): Flow<List<SportActivity>>
    suspend fun saveActivity(activity: SportActivity): Result<Unit>
    suspend fun updateActivity(activity: SportActivity): Result<Unit>
    suspend fun getActivities(): Result<List<SportActivity>>
    suspend fun getActivityById(id: String): Result<SportActivity?>
    suspend fun deleteActivity(id: String): Result<Unit>
    suspend fun syncActivities(): Result<Unit>
}