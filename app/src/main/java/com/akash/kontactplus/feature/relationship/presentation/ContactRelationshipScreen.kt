package com.akash.kontactplus.feature.relationship.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.SpaceLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.relationship.domain.model.ImportantDateType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContactRelationshipScreen(
    uiState: ContactRelationshipUiState,
    onNoteChanged: (String) -> Unit,
    onSaveNote: () -> Unit,
    onAddTag: (String, String) -> Unit,
    onRemoveTag: (Long) -> Unit,
    onAddDate: (String, LocalDate, ImportantDateType, Boolean) -> Unit,
    onDeleteDate: (Long) -> Unit,
    onAddReminder: (String, String, Instant) -> Unit,
    onCompleteReminder: (String) -> Unit,
    onCancelReminder: (String) -> Unit,
    onAiToolsClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddTagDialog by remember { mutableStateOf(false) }
    var showAddDateDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.relationship_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onAiToolsClick) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = stringResource(R.string.ai_tools_title))
                    }
                    if (uiState.hasUnsavedChanges) {
                        IconButton(onClick = onSaveNote) {
                            Icon(Icons.Default.Save, contentDescription = stringResource(R.string.relationship_note_save))
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(SpaceMedium)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Text(
                text = uiState.contact?.displayName ?: stringResource(R.string.contacts_unnamed),
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(SpaceLarge))
            
            // --- PRIVATE NOTE SECTION ---
            RelationshipSectionHeader(
                title = stringResource(R.string.relationship_private_note),
                icon = Icons.Default.Save
            )
            
            OutlinedTextField(
                value = uiState.noteInput,
                onValueChange = onNoteChanged,
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                placeholder = { Text(stringResource(R.string.relationship_private_note_hint)) },
                supportingText = {
                    Text(
                        text = "${uiState.noteInput.length} / 5000",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            )
            
            if (uiState.hasUnsavedChanges) {
                Text(
                    text = stringResource(R.string.relationship_note_unsaved),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(SpaceLarge))

            // --- TAGS SECTION ---
            RelationshipSectionHeader(
                title = stringResource(R.string.relationship_tags),
                icon = Icons.Default.Tag,
                onAddClick = { showAddTagDialog = true }
            )
            if (uiState.relationship.tags.isEmpty()) {
                Text(
                    "No tags assigned", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall)
                ) {
                    uiState.relationship.tags.forEach { tag ->
                        InputChip(
                            selected = true,
                            onClick = { onRemoveTag(tag.id) },
                            label = { Text(tag.name) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceLarge))

            // --- DATES SECTION ---
            RelationshipSectionHeader(
                title = stringResource(R.string.relationship_important_dates),
                icon = Icons.Default.Event,
                onAddClick = { showAddDateDialog = true }
            )
            if (uiState.relationship.importantDates.isEmpty()) {
                Text(
                    "Keep track of birthdays or anniversaries", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                uiState.relationship.importantDates.forEach { date ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(SpaceMedium).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = date.title, style = MaterialTheme.typography.titleSmall)
                                Text(text = date.localDate.toString(), style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onDeleteDate(date.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceLarge))

            // --- REMINDERS SECTION ---
            RelationshipSectionHeader(
                title = stringResource(R.string.relationship_reminders),
                icon = Icons.Default.Notifications,
                onAddClick = { showAddReminderDialog = true }
            )
            if (uiState.relationship.reminders.isEmpty()) {
                Text(
                    "Never forget to follow up", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                uiState.relationship.reminders.forEach { reminder ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(SpaceMedium).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = reminder.title, style = MaterialTheme.typography.titleSmall)
                                Text(text = reminder.status.name, style = MaterialTheme.typography.labelSmall)
                            }
                            Row {
                                if (reminder.status.name == "Scheduled") {
                                    IconButton(onClick = { onCompleteReminder(reminder.id) }) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                                IconButton(onClick = { onCancelReminder(reminder.id) }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(SpaceLarge))
        }
    }

    if (showAddTagDialog) {
        AddTagDialog(
            onDismiss = { showAddTagDialog = false },
            onConfirm = { name ->
                onAddTag(name, "cyan")
                showAddTagDialog = false
            }
        )
    }

    if (showAddDateDialog) {
        AddDateDialog(
            onDismiss = { showAddDateDialog = false },
            onConfirm = { title, date, type, repeats ->
                onAddDate(title, date, type, repeats)
                showAddDateDialog = false
            }
        )
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            onDismiss = { showAddReminderDialog = false },
            onConfirm = { title, note, time ->
                onAddReminder(title, note, time)
                showAddReminderDialog = false
            }
        )
    }
}

@Composable
fun AddTagDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var tagName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Tag") },
        text = {
            TextField(value = tagName, onValueChange = { tagName = it }, label = { Text("Tag Name") })
        },
        confirmButton = {
            Button(onClick = { onConfirm(tagName) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDateDialog(onDismiss: () -> Unit, onConfirm: (String, LocalDate, ImportantDateType, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var repeatsYearly by remember { mutableStateOf(true) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    } ?: selectedDate
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Important Date") },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Date: ${selectedDate}")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = repeatsYearly, onCheckedChange = { repeatsYearly = it })
                    Text("Repeats Yearly")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(title, selectedDate, ImportantDateType.Custom, repeatsYearly)
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(onDismiss: () -> Unit, onConfirm: (String, String, Instant) -> Unit) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedInstant by remember { mutableStateOf(Instant.now().plusSeconds(3600)) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedInstant = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it)
                    } ?: selectedInstant
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Reminder") },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                TextField(value = note, onValueChange = { note = it }, label = { Text("Note") })
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Date: ${selectedInstant.atZone(ZoneId.systemDefault()).toLocalDate()}")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(title, note, selectedInstant)
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun RelationshipSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onAddClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = SpaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (onAddClick != null) {
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}
