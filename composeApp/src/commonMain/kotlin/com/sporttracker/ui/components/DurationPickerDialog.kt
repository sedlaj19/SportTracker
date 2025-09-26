package com.sporttracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationPickerDialog(
    currentMinutes: Int = 0,
    onDurationSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHours by remember { mutableStateOf(currentMinutes / 60) }
    var selectedMinutes by remember { mutableStateOf(currentMinutes % 60) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Vyberte délku trvání",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hours picker
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Hodiny",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NumberPicker(
                        value = selectedHours,
                        onValueChange = { selectedHours = it },
                        range = 0..23,
                        modifier = Modifier.fillMaxHeight()
                    )
                }

                Text(
                    text = ":",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Minutes picker
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Minuty",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NumberPicker(
                        value = selectedMinutes,
                        onValueChange = { selectedMinutes = it },
                        range = 0..59,
                        step = 5,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val totalMinutes = selectedHours * 60 + selectedMinutes
                    if (totalMinutes > 0) {
                        onDurationSelected(totalMinutes)
                    }
                }
            ) {
                Text("Potvrdit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Zrušit")
            }
        }
    )
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    step: Int = 1,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val items = remember(range, step) {
        range.step(step).toList()
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = items.indexOf(value).coerceAtLeast(0)
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Selection indicator
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.medium
        ) {}

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 76.dp)
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = item == value

                Surface(
                    onClick = {
                        onValueChange(item)
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    },
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = item.toString().padStart(2, '0'),
                            style = if (isSelected) {
                                MaterialTheme.typography.headlineMedium
                            } else {
                                MaterialTheme.typography.bodyLarge
                            },
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DurationDisplay(
    minutes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hours = minutes / 60
    val mins = minutes % 60

    val displayText = when {
        hours > 0 && mins > 0 -> "$hours h $mins min"
        hours > 0 -> "$hours h"
        mins > 0 -> "$mins min"
        else -> "Klikněte pro výběr"
    }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Délka trvání",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                text = "⏱️",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}