package com.sporttracker.domain

import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.domain.usecase.DeleteActivityUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteActivityUseCaseTest {

    private val mockRepository = object : SportActivityRepository {
        var shouldFail = false
        var deletedActivityId: String? = null

        override fun observeActivities() = flowOf(emptyList<SportActivity>())
        override suspend fun getActivityById(id: String) = Result.success(null)
        override suspend fun getActivities() = Result.success(emptyList<SportActivity>())
        override suspend fun saveActivity(activity: SportActivity) = Result.success(Unit)
        override suspend fun updateActivity(activity: SportActivity) = Result.success(Unit)

        override suspend fun deleteActivity(id: String): Result<Unit> {
            return if (shouldFail) {
                Result.failure(Exception("Delete failed"))
            } else {
                deletedActivityId = id
                Result.success(Unit)
            }
        }

        override suspend fun syncActivities() = Result.success(Unit)
    }

    private val useCase = DeleteActivityUseCase(mockRepository)

    @Test
    fun `invoke should delete activity successfully`() = runTest {
        // Given
        val activityId = "test-activity-123"

        // When
        val result = useCase(activityId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(activityId, mockRepository.deletedActivityId)
    }

    @Test
    fun `invoke should propagate repository errors`() = runTest {
        // Given
        val activityId = "test-activity-123"
        mockRepository.shouldFail = true

        // When
        val result = useCase(activityId)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Delete failed", result.exceptionOrNull()?.message)
    }
}