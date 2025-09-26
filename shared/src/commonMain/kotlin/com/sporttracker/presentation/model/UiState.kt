package com.sporttracker.presentation.model

import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType

// List Screen UI State
data class ActivityListUiState(
    val activities: List<SportActivity> = emptyList(),
    val filter: FilterType = FilterType.ALL,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

enum class FilterType {
    ALL,
    LOCAL,
    REMOTE
}

// Add/Edit Screen UI State
data class AddEditActivityUiState(
    val id: String? = null,
    val name: String = "",
    val location: String = "",
    val durationMinutes: Int = 0,
    val selectedStorage: StorageType = StorageType.LOCAL,
    val isLoading: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(),
    val isEditMode: Boolean = false
)

// UI Events
sealed interface ActivityListEvent {
    data class ShowError(val message: String) : ActivityListEvent
    data class ShowSuccess(val message: String) : ActivityListEvent
    data object NavigateToAdd : ActivityListEvent
    data class NavigateToEdit(val activityId: String) : ActivityListEvent
}

sealed interface AddEditEvent {
    data object NavigateBack : AddEditEvent
    data class ShowError(val message: String) : AddEditEvent
    data class ShowSuccess(val message: String) : AddEditEvent
}