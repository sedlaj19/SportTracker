package com.sporttracker.domain.usecase

import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.repository.SportActivityRepository

class SaveActivityUseCase(
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

        return repository.saveActivity(activity)
    }
}