package com.sporttracker.domain

import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.domain.usecase.UpdateActivityUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTime::class)
class UpdateActivityUseCaseTest {

    private val mockRepository = object : SportActivityRepository {
        var shouldFail = false
        var updatedActivity: SportActivity? = null

        override fun observeActivities() = flowOf(emptyList<SportActivity>())
        override suspend fun getActivityById(id: String) = Result.success(null)
        override suspend fun getActivities() = Result.success(emptyList<SportActivity>())
        override suspend fun saveActivity(activity: SportActivity) = Result.success(Unit)

        override suspend fun updateActivity(activity: SportActivity): Result<Unit> {
            return if (shouldFail) {
                Result.failure(Exception("Update failed"))
            } else {
                updatedActivity = activity
                Result.success(Unit)
            }
        }

        override suspend fun deleteActivity(id: String) = Result.success(Unit)
        override suspend fun syncActivities() = Result.success(Unit)
    }

    private val useCase = UpdateActivityUseCase(mockRepository)

    @Test
    fun `invoke should update valid activity successfully`() = runTest {
        // Given
        val originalTimestamp = Clock.System.now().toEpochMilliseconds() - 10000
        val validActivity = SportActivity(
            id = "test-id",
            name = "Updated Run",
            location = "New Park",
            durationMinutes = 45,
            activityType = ActivityType.RUNNING,
            storageType = StorageType.LOCAL,
            lastModified = originalTimestamp
        )

        // When
        val result = useCase(validActivity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("test-id", mockRepository.updatedActivity?.id)
        assertEquals("Updated Run", mockRepository.updatedActivity?.name)
        assertEquals("New Park", mockRepository.updatedActivity?.location)
        assertEquals(45, mockRepository.updatedActivity?.durationMinutes)

        // Should update the timestamp
        assertNotEquals(originalTimestamp, mockRepository.updatedActivity?.lastModified)
        assertTrue((mockRepository.updatedActivity?.lastModified ?: 0) > originalTimestamp)
    }

    @Test
    fun `invoke should fail when activity name is blank`() = runTest {
        // Given
        val invalidActivity = SportActivity(
            id = "test-id",
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
            id = "test-id",
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
            id = "test-id",
            name = "Running",
            location = "Park",
            durationMinutes = -5,
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
            id = "test-id",
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
        assertEquals("Update failed", result.exceptionOrNull()?.message)
    }
}