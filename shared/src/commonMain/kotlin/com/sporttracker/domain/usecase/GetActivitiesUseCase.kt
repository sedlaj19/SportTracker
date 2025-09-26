package com.sporttracker.domain.usecase

import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.repository.SportActivityRepository
import kotlinx.coroutines.flow.Flow

class GetActivitiesUseCase(
    private val repository: SportActivityRepository
) {
    operator fun invoke(): Flow<List<SportActivity>> {
        return repository.observeActivities()
    }
}