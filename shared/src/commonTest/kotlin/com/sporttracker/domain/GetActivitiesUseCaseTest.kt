package com.sporttracker.domain

import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.domain.usecase.GetActivitiesUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetActivitiesUseCaseTest {

    private val mockRepository = object : SportActivityRepository {
        var activitiesList = emptyList<SportActivity>()

        override fun observeActivities() = flowOf(activitiesList)
        override suspend fun getActivityById(id: String) = Result.success(null)
        override suspend fun getActivities() = Result.success(activitiesList)
        override suspend fun saveActivity(activity: SportActivity) = Result.success(Unit)
        override suspend fun updateActivity(activity: SportActivity) = Result.success(Unit)
        override suspend fun deleteActivity(id: String) = Result.success(Unit)
        override suspend fun syncActivities() = Result.success(Unit)
    }

    private val useCase = GetActivitiesUseCase(mockRepository)

    @Test
    fun `invoke should return activities from repository`() = runTest {
        // Given
        val expectedActivities = listOf(
            SportActivity(
                id = "1",
                name = "Morning Run",
                location = "Park",
                durationMinutes = 30,
                activityType = ActivityType.RUNNING,
                storageType = StorageType.LOCAL
            ),
            SportActivity(
                id = "2",
                name = "Evening Bike",
                location = "City",
                durationMinutes = 45,
                activityType = ActivityType.CYCLING,
                storageType = StorageType.REMOTE
            )
        )
        mockRepository.activitiesList = expectedActivities

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(expectedActivities, result[0])
    }

    @Test
    fun `invoke should return empty list when no activities`() = runTest {
        // Given
        mockRepository.activitiesList = emptyList()

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(emptyList(), result[0])
    }
}