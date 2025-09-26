package com.sporttracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.usecase.DeleteActivityUseCase
import com.sporttracker.domain.usecase.GetActivitiesUseCase
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.presentation.model.ActivityListEvent
import com.sporttracker.presentation.model.ActivityListUiState
import com.sporttracker.presentation.model.FilterType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ActivityListViewModel(
    private val getActivitiesUseCase: GetActivitiesUseCase,
    private val deleteActivityUseCase: DeleteActivityUseCase,
    private val repository: SportActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityListUiState())
    val uiState: StateFlow<ActivityListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ActivityListEvent>()
    val events: SharedFlow<ActivityListEvent> = _events.asSharedFlow()

    private val currentFilter = MutableStateFlow(FilterType.ALL)

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            combine(
                getActivitiesUseCase(),
                currentFilter
            ) { activities, filter ->
                val filteredActivities = when (filter) {
                    FilterType.ALL -> activities
                    FilterType.LOCAL -> activities.filter { it.storageType == StorageType.LOCAL }
                    FilterType.REMOTE -> activities.filter { it.storageType == StorageType.REMOTE }
                }
                ActivityListUiState(
                    activities = filteredActivities,
                    filter = filter,
                    isLoading = false
                )
            }.catch { error ->
                _uiState.update {
                    it.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setFilter(filter: FilterType) {
        currentFilter.value = filter
        _uiState.update { it.copy(filter = filter) }
    }

    fun deleteActivity(activity: SportActivity) {
        viewModelScope.launch {
            deleteActivityUseCase(activity.id)
                .onSuccess {
                    _events.emit(ActivityListEvent.ShowSuccess("Aktivita smazána"))
                }
                .onFailure { error ->
                    _events.emit(ActivityListEvent.ShowError(error.message ?: "Chyba při mazání"))
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            repository.syncActivities()
                .onSuccess {
                    _uiState.update { it.copy(isRefreshing = false) }
                    _events.emit(ActivityListEvent.ShowSuccess("Synchronizováno"))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isRefreshing = false) }
                    _events.emit(ActivityListEvent.ShowError(error.message ?: "Chyba synchronizace"))
                }
        }
    }

    fun onAddClick() {
        viewModelScope.launch {
            _events.emit(ActivityListEvent.NavigateToAdd)
        }
    }

    fun onEditClick(activityId: String) {
        viewModelScope.launch {
            _events.emit(ActivityListEvent.NavigateToEdit(activityId))
        }
    }
}