package com.sporttracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.usecase.SaveActivityUseCase
import com.sporttracker.domain.usecase.UpdateActivityUseCase
import com.sporttracker.domain.usecase.DeleteActivityUseCase
import com.sporttracker.domain.repository.SportActivityRepository
import com.sporttracker.presentation.model.AddEditActivityUiState
import com.sporttracker.presentation.model.AddEditEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddEditActivityViewModel(
    private val saveActivityUseCase: SaveActivityUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val deleteActivityUseCase: DeleteActivityUseCase,
    private val repository: SportActivityRepository
) : ViewModel() {

    private var activityId: String? = null

    private val _uiState = MutableStateFlow(AddEditActivityUiState())
    val uiState: StateFlow<AddEditActivityUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddEditEvent>()
    val events: SharedFlow<AddEditEvent> = _events.asSharedFlow()

    fun setActivityId(id: String?) {
        activityId = id
        id?.let { loadActivity(it) }
    }

    private fun loadActivity(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getActivityById(id)
                .onSuccess { activity ->
                    activity?.let {
                        _uiState.update { state ->
                            state.copy(
                                id = it.id,
                                name = it.name,
                                location = it.location,
                                durationMinutes = it.durationMinutes,
                                selectedActivityType = it.activityType,
                                selectedStorage = it.storageType,
                                isLoading = false,
                                isEditMode = true
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AddEditEvent.ShowError(error.message ?: "Chyba při načítání"))
                }
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(
                name = name,
                validationErrors = it.validationErrors - "name"
            )
        }
    }

    fun updateLocation(location: String) {
        _uiState.update {
            it.copy(
                location = location,
                validationErrors = it.validationErrors - "location"
            )
        }
    }

    fun updateDuration(minutes: Int) {
        _uiState.update {
            it.copy(
                durationMinutes = minutes,
                validationErrors = it.validationErrors - "duration"
            )
        }
    }

    fun updateActivityType(activityType: ActivityType) {
        _uiState.update { it.copy(selectedActivityType = activityType) }
    }

    fun updateStorageType(storageType: StorageType) {
        _uiState.update { it.copy(selectedStorage = storageType) }
    }

    fun deleteActivity() {
        val state = _uiState.value
        val currentId = state.id
        
        if (!state.isEditMode || currentId.isNullOrBlank()) {
            return // Cannot delete activity that doesn't exist
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            deleteActivityUseCase(currentId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AddEditEvent.ShowSuccess("Aktivita smazána"))
                    _events.emit(AddEditEvent.NavigateBack)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AddEditEvent.ShowError(error.message ?: "Chyba při mazání"))
                }
        }
    }

    fun saveActivity() {
        val state = _uiState.value
        val validationErrors = mutableMapOf<String, String>()

        if (state.name.isBlank()) {
            validationErrors["name"] = "Název je povinný"
        }
        if (state.location.isBlank()) {
            validationErrors["location"] = "Místo je povinné"
        }
        if (state.durationMinutes <= 0) {
            validationErrors["duration"] = "Délka musí být větší než 0"
        }

        if (validationErrors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = validationErrors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val activity = if (state.isEditMode && state.id != null) {
                // Update existing activity
                SportActivity(
                    id = state.id,
                    name = state.name,
                    location = state.location,
                    durationMinutes = state.durationMinutes,
                    activityType = state.selectedActivityType,
                    storageType = state.selectedStorage
                )
            } else {
                // Create new activity
                SportActivity(
                    name = state.name,
                    location = state.location,
                    durationMinutes = state.durationMinutes,
                    activityType = state.selectedActivityType,
                    storageType = state.selectedStorage
                )
            }

            val result = if (state.isEditMode) {
                updateActivityUseCase(activity)
            } else {
                saveActivityUseCase(activity)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AddEditEvent.ShowSuccess(
                        if (state.isEditMode) "Aktivita upravena" else "Aktivita uložena"
                    ))
                    _events.emit(AddEditEvent.NavigateBack)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AddEditEvent.ShowError(error.message ?: "Chyba při ukládání"))
                }
        }
    }
}