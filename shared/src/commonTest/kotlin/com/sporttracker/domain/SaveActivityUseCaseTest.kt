package com.sporttracker.domain

import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.domain.usecase.SaveActivityUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaveActivityUseCaseTest {

    private val mockRepository = object : SportActivityRepository {
        var shouldFail = false
        var savedActivity: SportActivity? = null

        override fun observeActivities() = flowOf(emptyList<SportActivity>())
        override suspend fun getActivityById(id: String) = Result.success(null)
        override suspend fun getActivities() = Result.success(emptyList<SportActivity>())

        override suspend fun saveActivity(activity: SportActivity): Result<Unit> {
            return if (shouldFail) {
                Result.failure(Exception("Repository error"))
            } else {
                savedActivity = activity
                Result.success(Unit)
            }
        }

        override suspend fun updateActivity(activity: SportActivity) = Result.success(Unit)
        override suspend fun deleteActivity(id: String) = Result.success(Unit)
        override suspend fun syncActivities() = Result.success(Unit)
    }

    private val useCase = SaveActivityUseCase(mockRepository)

    @Test
    fun `invoke should save valid activity successfully`() = runTest {
        // Given
        val validActivity = SportActivity(
            name = "Morning Run",
            location = "Park",
            durationMinutes = 30,
            activityType = ActivityType.RUNNING,
            storageType = StorageType.LOCAL
        )

        // When
        val result = useCase(validActivity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(validActivity, mockRepository.savedActivity)
    }

    @Test
    fun `invoke should fail when activity name is blank`() = runTest {
        // Given
        val invalidActivity = SportActivity(
            name = "",
            location = "Park",
            durationMinutes = 30,
            activityType = ActivityType.RUNNING,
            storageType = StorageType.LOCAL
        )

        // When
        val result = useCase(invalidActivity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Activity name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should fail when activity location is blank`() = runTest {
        // Given
        val invalidActivity = SportActivity(
            name = "Running",
            location = "",
            durationMinutes = 30,
            activityType = ActivityType.RUNNING,
            storageType = StorageType.LOCAL
        )

        // When
        val result = useCase(invalidActivity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Activity location cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should fail when duration is zero or negative`() = runTest {
        // Given
        val invalidActivity = SportActivity(
            name = "Running",
            location = "Park",
            durationMinutes = 0,
            activityType = ActivityType.RUNNING,
            storageType = StorageType.LOCAL
        )

        // When
        val result = useCase(invalidActivity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Duration must be greater than 0", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should propagate repository errors`() = runTest {
        // Given
        val validActivity = SportActivity(
            name = "Running",
            location = "Park",
            durationMinutes = 30,
            activityType = ActivityType.RUNNING,
            storageType = StorageType.LOCAL
        )
        mockRepository.shouldFail = true

        // When
        val result = useCase(validActivity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Repository error", result.exceptionOrNull()?.message)
    }
}