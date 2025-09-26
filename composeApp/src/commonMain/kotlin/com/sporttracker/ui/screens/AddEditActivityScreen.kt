package com.sporttracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.StorageType
import com.sporttracker.presentation.viewmodel.AddEditActivityViewModel
import com.sporttracker.ui.components.DurationPickerDialog
import com.sporttracker.ui.components.DurationDisplay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditActivityScreen(
    activityId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddEditActivityViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDurationPicker by remember { mutableStateOf(false) }

    LaunchedEffect(activityId) {
        viewModel.setActivityId(activityId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is com.sporttracker.presentation.model.AddEditEvent.NavigateBack -> {
                    onNavigateBack()
                }
                is com.sporttracker.presentation.model.AddEditEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is com.sporttracker.presentation.model.AddEditEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditMode) "Upravit aktivitu" else "Nová aktivita")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveActivity() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Uložit")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Typ aktivity",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ActivityType.entries) { type ->
                    ActivityTypeChip(
                        type = type,
                        selected = uiState.selectedActivityType == type,
                        onClick = {
                            viewModel.updateActivityType(type)
                            viewModel.updateDuration(type.defaultDurationMinutes)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Název aktivity") },
                placeholder = { Text("např. ${uiState.selectedActivityType.czechName}") },
                leadingIcon = {
                    Icon(
                        imageVector = uiState.selectedActivityType.getIcon(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                isError = uiState.validationErrors.containsKey("name"),
                supportingText = {
                    uiState.validationErrors["name"]?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.location,
                onValueChange = viewModel::updateLocation,
                label = { Text("Místo") },
                isError = uiState.validationErrors.containsKey("location"),
                supportingText = {
                    uiState.validationErrors["location"]?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            DurationDisplay(
                minutes = uiState.durationMinutes,
                onClick = { showDurationPicker = true }
            )

            if (uiState.validationErrors.containsKey("duration")) {
                Text(
                    text = uiState.validationErrors["duration"] ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Uložit jako:",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedStorage == StorageType.LOCAL,
                    onClick = { viewModel.updateStorageType(StorageType.LOCAL) },
                    label = { Text("📱 Lokálně") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = if (uiState.selectedStorage == StorageType.LOCAL) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = uiState.selectedStorage == StorageType.LOCAL,
                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        borderWidth = 1.dp,
                        selectedBorderWidth = 2.dp
                    )
                )
                FilterChip(
                    selected = uiState.selectedStorage == StorageType.REMOTE,
                    onClick = { viewModel.updateStorageType(StorageType.REMOTE) },
                    label = { Text("☁️ Na cloud") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = if (uiState.selectedStorage == StorageType.REMOTE) {
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = uiState.selectedStorage == StorageType.REMOTE,
                        borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        selectedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        borderWidth = 1.dp,
                        selectedBorderWidth = 2.dp
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.saveActivity() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (uiState.isEditMode) "Upravit" else "Uložit")
                }
            }
        }
    }

    if (showDurationPicker) {
        DurationPickerDialog(
            currentMinutes = uiState.durationMinutes,
            onDurationSelected = { minutes ->
                viewModel.updateDuration(minutes)
                showDurationPicker = false
            },
            onDismiss = {
                showDurationPicker = false
            }
        )
    }
}


fun ActivityType.getIcon(): ImageVector = when (this) {
    ActivityType.RUNNING -> Icons.AutoMirrored.Filled.DirectionsRun
    ActivityType.CYCLING -> Icons.AutoMirrored.Filled.DirectionsBike
    ActivityType.WALKING -> Icons.AutoMirrored.Filled.DirectionsWalk
    ActivityType.SWIMMING -> Icons.Default.Pool
    ActivityType.GYM -> Icons.Default.FitnessCenter
    ActivityType.HIKING -> Icons.Default.Terrain
    ActivityType.YOGA -> Icons.Default.SelfImprovement
    ActivityType.OTHER -> Icons.Default.Sports
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTypeChip(
    type: ActivityType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = type.czechName,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = type.getIcon(),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = modifier
    )
}