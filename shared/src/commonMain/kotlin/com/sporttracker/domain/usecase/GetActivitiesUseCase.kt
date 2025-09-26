package com.sporttracker.domain.usecase

import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.repository.SportActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetActivitiesUseCase(
    private val repository: SportActivityRepository
) {
    operator fun invoke(): Flow<List<SportActivity>> {
        return repository.observeActivities()
    }

    fun filterByStorage(storageType: StorageType): Flow<List<SportActivity>> {
        return repository.observeActivities().map { activities ->
            activities.filter { it.storageType == storageType }
        }
    }
}