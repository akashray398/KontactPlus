package com.akash.kontactplus.feature.contacts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.*
import com.akash.kontactplus.core.designsystem.theme.*
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.relationship.domain.model.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    uiState: ContactDetailsUiState,
    onBackClick: () -> Unit,
    onPhoneNumberClick: (String) -> Unit,
    onFavouriteClick: () -> Unit,
    onManageRelationship: () -> Unit,
    onAddFact: (String, FactCategory) -> Unit = { _, _ -> },
    onDeleteFact: (Long) -> Unit = {},
    onAnalyzeConversation: (String) -> Unit = {},
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.contact_details_title),
                        modifier = Modifier.semantics { heading() }
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.contact_details_back)
                        )
                    }
                },
                actions = {
                    if (uiState is ContactDetailsUiState.Success) {
                        IconButton(
                            onClick = onFavouriteClick,
                            enabled = !uiState.isFavouriteActionInProgress
                        ) {
                            Icon(
                                imageVector = if (uiState.isFavourite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = stringResource(
                                    if (uiState.isFavourite) R.string.favourite_remove else R.string.favourite_add
                                ),
                                tint = if (uiState.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (uiState) {
                ContactDetailsUiState.Loading -> {
                    KontactLoadingState()
                }
                is ContactDetailsUiState.Success -> {
                    SuccessState(
                        uiState = uiState,
                        onPhoneNumberClick = onPhoneNumberClick,
                        onManageRelationship = onManageRelationship,
                        onAddFact = onAddFact,
                        onDeleteFact = onDeleteFact,
                        onAnalyzeConversation = onAnalyzeConversation
                    )
                }
                ContactDetailsUiState.NotFound -> {
                    KontactEmptyState(
                        title = stringResource(R.string.contact_details_not_found_title),
                        description = stringResource(R.string.contact_details_not_found_description),
                        icon = Icons.Default.SearchOff,
                        action = {
                            KontactPrimaryButton(onClick = onBackClick) {
                                Text(stringResource(R.string.contact_details_back))
                            }
                        }
                    )
                }
                is ContactDetailsUiState.Error -> {
                    KontactErrorState(
                        title = stringResource(R.string.contact_details_error_title),
                        description = stringResource(uiState.messageRes),
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessState(
    uiState: ContactDetailsUiState.Success,
    onPhoneNumberClick: (String) -> Unit,
    onManageRelationship: () -> Unit,
    onAddFact: (String, FactCategory) -> Unit,
    onDeleteFact: (Long) -> Unit,
    onAnalyzeConversation: (String) -> Unit
) {
    val contact = uiState.contact
    var showAddFactDialog by remember { mutableStateOf(false) }
    var noteInputForAnalysis by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContactAvatar(
            displayName = contact.displayName.ifBlank { stringResource(R.string.contacts_unnamed) },
            size = ContactAvatarLarge
        )
        
        Spacer(modifier = Modifier.height(SpaceMedium))
        
        Text(
            text = contact.displayName.ifBlank { stringResource(R.string.contacts_unnamed) },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(SpaceMedium))

        // 1. Relationship Health Card
        uiState.health?.let { health ->
            RelationshipHealthCard(health = health)
            Spacer(modifier = Modifier.height(SpaceMedium))
        }

        // 2. Memory Replay / Why Do I Know Card
        uiState.memoryReplay?.let { replay ->
            MemoryReplayCard(replay = replay)
            Spacer(modifier = Modifier.height(SpaceMedium))
        }

        // 3. AI Relationship Memory / Facts Section
        AiFactsSection(
            facts = uiState.facts,
            onAddFactClick = { showAddFactDialog = true },
            onDeleteFact = onDeleteFact
        )

        Spacer(modifier = Modifier.height(SpaceMedium))

        // 4. Conversation Intelligence Analyzer
        ConversationAnalyzerCard(
            noteText = noteInputForAnalysis,
            onNoteTextChange = { noteInputForAnalysis = it },
            isAnalyzing = uiState.isAnalyzingConversation,
            onAnalyze = { 
                if (noteInputForAnalysis.isNotBlank()) {
                    onAnalyzeConversation(noteInputForAnalysis)
                }
            }
        )

        Spacer(modifier = Modifier.height(SpaceMedium))

        // 5. Relationship Summary
        KontactCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onManageRelationship
        ) {
            Column(modifier = Modifier.padding(SpaceMedium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.relationship_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Edit, 
                        contentDescription = "Edit relationship details", 
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                if (uiState.relationship?.privateNote?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    Text(
                        text = uiState.relationship.privateNote,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3
                    )
                }

                if (uiState.relationship?.tags?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    Text(
                        text = uiState.relationship.tags.joinToString { it.name },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(SpaceMedium))

        // 6. Unified Timeline
        if (uiState.timeline.isNotEmpty()) {
            RelationshipTimelineSection(timeline = uiState.timeline)
            Spacer(modifier = Modifier.height(SpaceMedium))
        }
        
        Text(
            text = stringResource(R.string.contact_details_phone_numbers),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().semantics { heading() },
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(SpaceSmall))
        
        if (contact.phoneNumbers.isEmpty()) {
            Text(
                text = stringResource(R.string.contact_details_no_phone_numbers),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            contact.phoneNumbers.forEach { number ->
                PhoneNumberItem(
                    number = number,
                    onDialClick = { onPhoneNumberClick(number) }
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }

    if (showAddFactDialog) {
        AddFactDialog(
            onDismiss = { showAddFactDialog = false },
            onConfirm = { fact, category ->
                onAddFact(fact, category)
                showAddFactDialog = false
            }
        )
    }
}

@Composable
private fun RelationshipHealthCard(health: RelationshipHealth) {
    val badgeColor = when (health.status) {
        RelationshipHealthStatus.Thriving -> Color(0xFF2E7D32)
        RelationshipHealthStatus.Good -> Color(0xFF1976D2)
        RelationshipHealthStatus.NeedsAttention -> Color(0xFFED6C02)
        RelationshipHealthStatus.AtRisk -> Color(0xFFD32F2F)
    }

    KontactCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(SpaceMedium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Relationship Health",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${health.status.name} (${health.score}%)",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(SpaceSmall))
            LinearProgressIndicator(
                progress = { health.score / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = badgeColor
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
            Text(
                text = health.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (health.suggestedActions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(
                    text = "Next Best Action: ${health.suggestedActions.first()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MemoryReplayCard(replay: com.akash.kontactplus.feature.relationship.domain.usecase.MemoryReplayBriefing) {
    KontactCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(SpaceMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Memory Replay",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = "Pre-Call Memory Replay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(SpaceSmall))
            Text(
                text = replay.whyDoIKnowThisPerson,
                style = MaterialTheme.typography.bodyMedium
            )
            if (replay.openReminders.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(
                    text = "Pending Action: ${replay.openReminders.first().title}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AiFactsSection(
    facts: List<ContactFact>,
    onAddFactClick: () -> Unit,
    onDeleteFact: (Long) -> Unit
) {
    KontactCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(SpaceMedium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Memory Facts",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = "Relationship Memory (${facts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onAddFactClick) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Fact")
                }
            }
            if (facts.isEmpty()) {
                Text(
                    text = "No facts saved yet. Tap + to add facts like hobbies, company, or family details.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(SpaceSmall))
                facts.forEach { fact ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = fact.fact, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = fact.category.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { onDeleteFact(fact.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete Fact",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationAnalyzerCard(
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    isAnalyzing: Boolean,
    onAnalyze: () -> Unit
) {
    KontactCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(SpaceMedium)) {
            Text(
                text = "Conversation Intelligence",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Paste conversation notes, meeting summary, or key takeaways...") },
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
            Button(
                onClick = onAnalyze,
                enabled = noteText.isNotBlank() && !isAnalyzing,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Extract Facts & Reminders")
                }
            }
        }
    }
}

@Composable
private fun RelationshipTimelineSection(timeline: List<RelationshipTimelineItem>) {
    KontactCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(SpaceMedium)) {
            Text(
                text = "Relationship Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
            timeline.take(8).forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    val icon = when (item) {
                        is RelationshipTimelineItem.CallEvent -> Icons.Default.Call
                        is RelationshipTimelineItem.FactEvent -> Icons.Default.AutoAwesome
                        is RelationshipTimelineItem.NoteEvent -> Icons.Default.Note
                        is RelationshipTimelineItem.DateEvent -> Icons.Default.Event
                        is RelationshipTimelineItem.ReminderEvent -> Icons.Default.Notifications
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(SpaceMedium))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(text = item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val formattedDate = item.timestamp.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm"))
                        Text(text = formattedDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddFactDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, FactCategory) -> Unit
) {
    var factText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FactCategory.Personal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Relationship Memory Fact") },
        text = {
            Column {
                OutlinedTextField(
                    value = factText,
                    onValueChange = { factText = it },
                    label = { Text("Memory Fact (e.g., 'Prefers oat milk latte')") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
                Text("Category:", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FactCategory.values().forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name.take(4)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(factText, selectedCategory) },
                enabled = factText.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PhoneNumberItem(
    number: String,
    onDialClick: () -> Unit
) {
    KontactCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onDialClick
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMedium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.contact_details_open_dialer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call $number",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailsSuccessPreview() {
    KontactPlusTheme {
        ContactDetailsScreen(
            uiState = ContactDetailsUiState.Success(
                Contact(1, "k1", "Akash Patel", listOf("1234567890", "0987654321"))
            ),
            onBackClick = {},
            onPhoneNumberClick = {},
            onFavouriteClick = {},
            onManageRelationship = {},
            onRetry = {}
        )
    }
}
