package com.sporttracker.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sporttracker.domain.model.ActivityType
import com.sporttracker.domain.model.SportActivity
import com.sporttracker.domain.model.StorageType
import com.sporttracker.domain.util.NetworkMonitor
import com.sporttracker.presentation.model.FilterType
import com.sporttracker.presentation.viewmodel.ActivityListViewModel
import com.sporttracker.ui.components.ErrorState
import com.sporttracker.ui.components.LoadingIndicator
import com.sporttracker.ui.components.OfflineIndicator
import com.sporttracker.ui.utils.OrientationMode
import com.sporttracker.ui.utils.getOrientationMode
import org.koin.compose.koinInject

enum class StorageFilterType(val label: String) {
    ALL("Vše"),
    LOCAL("Lokální"),
    REMOTE("Vzdálené")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: ActivityListViewModel = koinInject(),
    networkMonitor: NetworkMonitor = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedActivityFilter by remember { mutableStateOf<ActivityType?>(null) }
    val orientationMode = getOrientationMode()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is com.sporttracker.presentation.model.ActivityListEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is com.sporttracker.presentation.model.ActivityListEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is com.sporttracker.presentation.model.ActivityListEvent.NavigateToAdd -> {
                    onNavigateToAdd()
                }
                is com.sporttracker.presentation.model.ActivityListEvent.NavigateToEdit -> {
                    onNavigateToEdit(event.activityId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text("SportTracker")
                            Text(
                                text = "${uiState.totalActivitiesCount} aktivit",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.refresh() }
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Obnovit"
                            )
                        }
                    }
                )
                if (!isOnline) {
                    OfflineIndicator()
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddClick() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Přidat aktivitu")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        message = "Načítám aktivity...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error ?: "Neznámá chyba",
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.totalActivitiesCount == 0 -> {
                    // No activities at all - show the general empty state
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // Has activities (totalActivitiesCount > 0) - show layout with filter-aware empty states
                    when (orientationMode) {
                        OrientationMode.PORTRAIT -> {
                            PortraitActivityLayout(
                                uiState = uiState,
                                viewModel = viewModel,
                                selectedActivityFilter = selectedActivityFilter,
                                onActivityFilterChange = { selectedActivityFilter = it },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        OrientationMode.LANDSCAPE -> {
                            LandscapeActivityLayout(
                                uiState = uiState,
                                viewModel = viewModel,
                                selectedActivityFilter = selectedActivityFilter,
                                onActivityFilterChange = { selectedActivityFilter = it },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PortraitActivityLayout(
    uiState: com.sporttracker.presentation.model.ActivityListUiState,
    viewModel: ActivityListViewModel,
    selectedActivityFilter: ActivityType?,
    onActivityFilterChange: (ActivityType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Storage type filter (All | Local | Remote) - REQUIRED BY SPECIFICATION
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Typ úložiště",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StorageFilterType.entries.forEach { filterType ->
                        FilterChip(
                            selected = when(uiState.filter) {
                                FilterType.ALL -> filterType == StorageFilterType.ALL
                                FilterType.LOCAL -> filterType == StorageFilterType.LOCAL
                                FilterType.REMOTE -> filterType == StorageFilterType.REMOTE
                            },
                            onClick = {
                                viewModel.setFilter(
                                    when(filterType) {
                                        StorageFilterType.ALL -> FilterType.ALL
                                        StorageFilterType.LOCAL -> FilterType.LOCAL
                                        StorageFilterType.REMOTE -> FilterType.REMOTE
                                    }
                                )
                            },
                            label = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (filterType) {
                                            StorageFilterType.ALL -> Icons.Default.Storage
                                            StorageFilterType.LOCAL -> Icons.Default.PhoneAndroid
                                            StorageFilterType.REMOTE -> Icons.Default.Cloud
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(filterType.label)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (filterType) {
                                    StorageFilterType.LOCAL -> MaterialTheme.colorScheme.primaryContainer
                                    StorageFilterType.REMOTE -> MaterialTheme.colorScheme.secondaryContainer
                                    StorageFilterType.ALL -> MaterialTheme.colorScheme.tertiaryContainer
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Activity type filter using proper ActivityType enum
        if (uiState.activities.isNotEmpty()) {
            val activityTypes = uiState.activities
                .map { it.activityType }
                .distinct()
                .sortedBy { it.czechName }

            if (activityTypes.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedActivityFilter == null,
                            onClick = { onActivityFilterChange(null) },
                            label = { Text("Všechny sporty") }
                        )
                    }
                    items(activityTypes.size) { index ->
                        val type = activityTypes[index]
                        FilterChip(
                            selected = selectedActivityFilter == type,
                            onClick = {
                                onActivityFilterChange(if (selectedActivityFilter == type) null else type)
                            },
                            label = { Text(type.czechName) }
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        // Activities are already filtered by storage type in ViewModel
        // Only apply activity type filter in UI (this is a UI-only enhancement)
        var filteredActivities = uiState.activities

        if (selectedActivityFilter != null) {
            filteredActivities = filteredActivities.filter { activity ->
                activity.activityType == selectedActivityFilter
            }
        }

        // Display counter for filtered results
        if (uiState.filter != FilterType.ALL || selectedActivityFilter != null) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Zobrazeno ${filteredActivities.size} z ${uiState.totalActivitiesCount} aktivit",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredActivities.isEmpty()) {
                // Inline empty state message instead of full screen overlay
                item {
                    FilteredEmptyState(
                        hasAnyActivities = uiState.activities.isNotEmpty(),
                        currentFilter = uiState.filter,
                        selectedActivityType = selectedActivityFilter,
                        onClearFilters = {
                            viewModel.setFilter(FilterType.ALL)
                            onActivityFilterChange(null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                    )
                }
            } else {
                items(
                    items = filteredActivities,
                    key = { it.id }
                ) { activity ->
                    ActivityCard(
                        activity = activity,
                        onClick = { viewModel.onEditClick(activity.id) },
                        onDelete = { viewModel.deleteActivity(activity) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LandscapeActivityLayout(
    uiState: com.sporttracker.presentation.model.ActivityListUiState,
    viewModel: ActivityListViewModel,
    selectedActivityFilter: ActivityType?,
    onActivityFilterChange: (ActivityType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        // Left column - Filter panel (30% width)
        Card(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Filtry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Storage type filters - vertical layout
                Text(
                    text = "Typ úložiště",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                StorageFilterType.entries.forEach { filterType ->
                    FilterChip(
                        selected = when(uiState.filter) {
                            FilterType.ALL -> filterType == StorageFilterType.ALL
                            FilterType.LOCAL -> filterType == StorageFilterType.LOCAL
                            FilterType.REMOTE -> filterType == StorageFilterType.REMOTE
                        },
                        onClick = {
                            viewModel.setFilter(
                                when(filterType) {
                                    StorageFilterType.ALL -> FilterType.ALL
                                    StorageFilterType.LOCAL -> FilterType.LOCAL
                                    StorageFilterType.REMOTE -> FilterType.REMOTE
                                }
                            )
                        },
                        label = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (filterType) {
                                        StorageFilterType.ALL -> Icons.Default.Storage
                                        StorageFilterType.LOCAL -> Icons.Default.PhoneAndroid
                                        StorageFilterType.REMOTE -> Icons.Default.Cloud
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(filterType.label)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (filterType) {
                                StorageFilterType.LOCAL -> MaterialTheme.colorScheme.primaryContainer
                                StorageFilterType.REMOTE -> MaterialTheme.colorScheme.secondaryContainer
                                StorageFilterType.ALL -> MaterialTheme.colorScheme.tertiaryContainer
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Activity type filters - vertical layout
                if (uiState.activities.isNotEmpty()) {
                    val activityTypes = uiState.activities
                        .map { it.activityType }
                        .distinct()
                        .sortedBy { it.czechName }

                    if (activityTypes.isNotEmpty()) {
                        Text(
                            text = "Sporty",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FilterChip(
                            selected = selectedActivityFilter == null,
                            onClick = { onActivityFilterChange(null) },
                            label = { Text("Všechny sporty") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        )

                        activityTypes.forEach { type ->
                            FilterChip(
                                selected = selectedActivityFilter == type,
                                onClick = {
                                    onActivityFilterChange(if (selectedActivityFilter == type) null else type)
                                },
                                label = { Text(type.czechName) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }
                }
                
                // Clear all filters button
                if (uiState.filter != FilterType.ALL || selectedActivityFilter != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.setFilter(FilterType.ALL)
                            onActivityFilterChange(null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Zrušit filtry", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right column - Activities grid (70% width)
        Column(
            modifier = Modifier.weight(0.7f)
        ) {
            // Activities are already filtered by storage type in ViewModel
            // Only apply activity type filter in UI (this is a UI-only enhancement)
            var filteredActivities = uiState.activities

            if (selectedActivityFilter != null) {
                filteredActivities = filteredActivities.filter { activity ->
                    activity.activityType == selectedActivityFilter
                }
            }

            // Display counter for filtered results
            if (uiState.filter != FilterType.ALL || selectedActivityFilter != null) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Zobrazeno ${filteredActivities.size} z ${uiState.totalActivitiesCount} aktivit",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(end = 16.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredActivities.isEmpty()) {
                    // Inline empty state message for landscape mode
                    item(span = { GridItemSpan(2) }) {
                        FilteredEmptyState(
                            hasAnyActivities = uiState.activities.isNotEmpty(),
                            currentFilter = uiState.filter,
                            selectedActivityType = selectedActivityFilter,
                            onClearFilters = {
                                viewModel.setFilter(FilterType.ALL)
                                onActivityFilterChange(null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        )
                    }
                } else {
                    items(
                        items = filteredActivities,
                        key = { it.id }
                    ) { activity ->
                        ActivityCard(
                            activity = activity,
                            onClick = { viewModel.onEditClick(activity.id) },
                            onDelete = { viewModel.deleteActivity(activity) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏃‍♂️ 🚴‍♀️ 🏊‍♂️",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Zatím nemáte žádné aktivity",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Začněte kliknutím na +",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FilteredEmptyState(
    hasAnyActivities: Boolean,
    currentFilter: FilterType,
    selectedActivityType: ActivityType?,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasAnyActivities) {
            // User has activities, but filters result in no matches
            Text(
                text = "🔍",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Žádné aktivity nevyhovují filtru",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val filterDescription = buildString {
                if (currentFilter != FilterType.ALL) {
                    append("Úložiště: ")
                    append(when (currentFilter) {
                        FilterType.LOCAL -> "Lokální"
                        FilterType.REMOTE -> "Vzdálené"
                        FilterType.ALL -> "Všechny"
                    })
                }
                if (selectedActivityType != null) {
                    if (currentFilter != FilterType.ALL) append(" • ")
                    append("Sport: ${selectedActivityType.czechName}")
                }
            }
            
            Text(
                text = "Aktivní filtry: $filterDescription",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onClearFilters
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Zrušit všechny filtry")
            }
        } else {
            // No activities at all
            Text(
                text = "🏃‍♂️ 🚴‍♀️ 🏊‍♂️",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Zatím nemáte žádné aktivity",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Začněte kliknutím na +",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActivityCard(
    activity: SportActivity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    // Color coding based on storage type (REQUIRED BY SPECIFICATION)
    val cardColors = when (activity.storageType) {
        StorageType.LOCAL -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
        StorageType.REMOTE -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        )
    }

    val borderStroke = when (activity.storageType) {
        StorageType.LOCAL -> BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        StorageType.REMOTE -> BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { isExpanded = !isExpanded }
            ),
        colors = if (activity.syncStatus == com.sporttracker.domain.model.SyncStatus.ERROR) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        } else {
            cardColors
        },
        border = borderStroke
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = getActivityIcon(activity.name),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = activity.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = activity.location,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = formatDuration(activity.durationMinutes),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.height(28.dp)
                        )

                        // Storage type indicator with label
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = if (activity.storageType == StorageType.LOCAL) "Lokální" else "Vzdálené",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (activity.storageType == StorageType.LOCAL)
                                        Icons.Default.PhoneAndroid else Icons.Default.Cloud,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.height(28.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (activity.storageType == StorageType.LOCAL)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        )

                        if (activity.syncStatus != com.sporttracker.domain.model.SyncStatus.SYNCED) {
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = when(activity.syncStatus) {
                                            com.sporttracker.domain.model.SyncStatus.PENDING -> "Čeká na sync"
                                            com.sporttracker.domain.model.SyncStatus.ERROR -> "Chyba"
                                            else -> ""
                                        },
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when(activity.syncStatus) {
                                            com.sporttracker.domain.model.SyncStatus.PENDING -> Icons.Default.Sync
                                            com.sporttracker.domain.model.SyncStatus.ERROR -> Icons.Default.SyncProblem
                                            else -> Icons.Default.Check
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = when(activity.syncStatus) {
                                            com.sporttracker.domain.model.SyncStatus.ERROR -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                },
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }

                if (isExpanded) {
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Smazat",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (isExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onClick
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upravit")
                    }
                    TextButton(
                        onClick = { /* TODO: Share */ }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sdílet")
                    }
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Smazat")
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Smazat aktivitu?") },
            text = {
                Text("Opravdu chcete smazat aktivitu '${activity.name}'? Tuto akci nelze vrátit zpět.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Smazat")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Zrušit")
                }
            }
        )
    }
}

@Composable
fun getActivityIcon(name: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        name.contains("běh", ignoreCase = true) || name.contains("run", ignoreCase = true) -> Icons.AutoMirrored.Filled.DirectionsRun
        name.contains("kolo", ignoreCase = true) || name.contains("bike", ignoreCase = true) || name.contains("cycling", ignoreCase = true) -> Icons.AutoMirrored.Filled.DirectionsBike
        name.contains("plavání", ignoreCase = true) || name.contains("swim", ignoreCase = true) -> Icons.Default.Pool
        name.contains("chůze", ignoreCase = true) || name.contains("walk", ignoreCase = true) -> Icons.AutoMirrored.Filled.DirectionsWalk
        name.contains("posilovna", ignoreCase = true) || name.contains("gym", ignoreCase = true) -> Icons.Default.FitnessCenter
        else -> Icons.Default.Sports
    }
}

fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours > 0 -> "${hours}h ${mins}min"
        else -> "${mins}min"
    }
}