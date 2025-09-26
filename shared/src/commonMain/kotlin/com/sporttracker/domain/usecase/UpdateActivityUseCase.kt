package com.sporttracker.domain.usecase

import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.repository.SportActivityRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Clock

@OptIn(ExperimentalTime::class)
class UpdateActivityUseCase(
    private val repository: SportActivityRepository
) {
    suspend operator fun invoke(activity: SportActivity): Result<Unit> {
        // Validate activity
        if (activity.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Activity name cannot be empty"))
        }
        if (activity.location.isBlank()) {
            return Result.failure(IllegalArgumentException("Activity location cannot be empty"))
        }
        if (activity.durationMinutes <= 0) {
            return Result.failure(IllegalArgumentException("Duration must be greater than 0"))
        }

        // Update with new timestamp
        val updatedActivity = activity.copy(
            lastModified = Clock.System.now().toEpochMilliseconds()
        )

        return repository.updateActivity(updatedActivity)
    }
}