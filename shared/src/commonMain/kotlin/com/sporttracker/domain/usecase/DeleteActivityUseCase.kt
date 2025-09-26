package com.sporttracker.domain.usecase

import com.sporttracker.domain.repository.SportActivityRepository

class DeleteActivityUseCase(
    private val repository: SportActivityRepository
) {
    suspend operator fun invoke(activityId: String): Result<Unit> {
        return repository.deleteActivity(activityId)
    }
}