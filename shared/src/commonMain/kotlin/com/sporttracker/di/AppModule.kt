package com.sporttracker.di

import com.sporttracker.data.repository.SportActivityRepositoryImpl
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.domain.usecase.*
import com.sporttracker.presentation.viewmodel.ActivityListViewModel
import com.sporttracker.presentation.viewmodel.AddEditActivityViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    // Repository
    single<SportActivityRepository> {
        val authService = get<com.sporttracker.data.auth.AuthService>()
        SportActivityRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            getUserId = { authService.getCurrentUserId() }
        )
    }

    // Use Cases
    factoryOf(::GetActivitiesUseCase)
    factoryOf(::SaveActivityUseCase)
    factoryOf(::UpdateActivityUseCase)
    factoryOf(::DeleteActivityUseCase)

    // ViewModels
    factory {
        ActivityListViewModel(
            getActivitiesUseCase = get(),
            deleteActivityUseCase = get(),
            repository = get()
        )
    }

    factory {
        AddEditActivityViewModel(
            saveActivityUseCase = get(),
            updateActivityUseCase = get(),
            deleteActivityUseCase = get(),
            repository = get()
        )
    }
}