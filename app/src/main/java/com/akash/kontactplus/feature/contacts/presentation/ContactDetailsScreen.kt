package com.akash.kontactplus.feature.contacts.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.*
import com.akash.kontactplus.core.designsystem.theme.*
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.relationship.domain.model.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ContactDetailsScreen(
    uiState: ContactDetailsUiState,
    onBackClick: () -> Unit,
    onPhoneNumberClick: (String) -> Unit,
    onFavouriteClick: () -> Unit,
    onEditContactClick: () -> Unit,
    onManageRelationship: () -> Unit,
    onAddFact: (String, FactCategory) -> Unit = { _, _ -> },
    onDeleteFact: (Long) -> Unit = {},
    onAnalyzeConversation: (String) -> Unit = {},
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (uiState) {
                ContactDetailsUiState.Loading -> {
                    KontactLoadingState()
                }
                is ContactDetailsUiState.Success -> {
                    SuccessState(
                        uiState = uiState,
                        onBackClick = onBackClick,
                        onFavouriteClick = onFavouriteClick,
                        onEditContactClick = onEditContactClick,
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
    onBackClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onEditContactClick: () -> Unit,
    onPhoneNumberClick: (String) -> Unit,
    onManageRelationship: () -> Unit,
    onAddFact: (String, FactCategory) -> Unit,
    onDeleteFact: (Long) -> Unit,
    onAnalyzeConversation: (String) -> Unit
) {
    val contact = uiState.contact
    val context = LocalContext.current
    var showAddFactDialog by remember { mutableStateOf(false) }
    var noteInputForAnalysis by remember { mutableStateOf("") }

    val gradientHeaderBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F172A), // Dark navy
            Color(0xFF1E1B4B), // Deep violet
            Color(0xFF0284C7)  // Vibrant cyan
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gradient Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientHeaderBrush)
                .statusBarsPadding()
                .padding(bottom = SpaceLarge)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpaceMedium, vertical = SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.contact_details_back),
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onFavouriteClick,
                        enabled = !uiState.isFavouriteActionInProgress,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavourite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = stringResource(
                                if (uiState.isFavourite) R.string.favourite_remove else R.string.favourite_add
                            ),
                            tint = if (uiState.isFavourite) Color(0xFFFBBF24) else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))

                // Avatar
                ContactAvatar(
                    displayName = contact.displayName.ifBlank { stringResource(R.string.contacts_unnamed) },
                    size = ContactAvatarLarge
                )

                Spacer(modifier = Modifier.height(SpaceMedium))

                // Name
                Text(
                    text = contact.displayName.ifBlank { stringResource(R.string.contacts_unnamed) },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                // Saved status badge
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Saved in contacts",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Quick Action Panel
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceMedium)
                .offset(y = (-16).dp),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = SpaceSmall),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val firstNumber = contact.phoneNumbers.firstOrNull() ?: ""
                QuickActionButton(
                    icon = Icons.Default.Call,
                    label = "Call",
                    contentDesc = "Call contact",
                    enabled = firstNumber.isNotBlank(),
                    onClick = { onPhoneNumberClick(firstNumber) }
                )

                QuickActionButton(
                    icon = if (uiState.isFavourite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    label = "Favourite",
                    contentDesc = if (uiState.isFavourite) "Remove favourite" else "Add favourite",
                    tint = if (uiState.isFavourite) Color(0xFFFBBF24) else MaterialTheme.colorScheme.primary,
                    onClick = onFavouriteClick
                )

                QuickActionButton(
                    icon = Icons.Default.Edit,
                    label = "Edit",
                    contentDesc = "Edit contact",
                    onClick = onEditContactClick
                )

                QuickActionButton(
                    icon = Icons.Default.Psychology,
                    label = "Relationship",
                    contentDesc = "Relationship details",
                    onClick = onManageRelationship
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Phone Numbers Section
            Text(
                text = stringResource(R.string.contact_details_phone_numbers),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { heading() },
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
                    PhoneNumberCard(
                        number = number,
                        onDialClick = { onPhoneNumberClick(number) },
                        onCopyClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("phone_number", number)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }

            Spacer(modifier = Modifier.height(SpaceMedium))

            // 2. Relationship Health Card
            uiState.health?.let { health ->
                RelationshipHealthCard(health = health)
                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            // 3. Memory Replay / Why Do I Know Card
            uiState.memoryReplay?.let { replay ->
                MemoryReplayCard(replay = replay)
                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            // 4. AI Relationship Memory / Facts Section
            AiFactsSection(
                facts = uiState.facts,
                onAddFactClick = { showAddFactDialog = true },
                onDeleteFact = onDeleteFact
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            // 5. Conversation Intelligence Analyzer
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

            // 6. Relationship Summary Card
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
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
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

            // 7. Unified Timeline
            if (uiState.timeline.isNotEmpty()) {
                RelationshipTimelineSection(timeline = uiState.timeline)
                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            Spacer(modifier = Modifier.height(SpaceLarge))
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
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    contentDesc: String,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(64.dp, 64.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .semantics {
                contentDescription = contentDesc
                role = Role.Button
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) tint else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun PhoneNumberCard(
    number: String,
    onDialClick: () -> Unit,
    onCopyClick: () -> Unit
) {
    KontactCard(
        modifier = Modifier.fillMaxWidth()
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tap Call to open keypad",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onCopyClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy number",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDialClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call $number",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
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
                IconButton(
                    onClick = onAddFactClick,
                    modifier = Modifier.size(48.dp)
                ) {
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
                        IconButton(
                            onClick = { onDeleteFact(fact.id) },
                            modifier = Modifier.size(48.dp)
                        ) {
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
                    FactCategory.entries.forEach { category ->
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

@Preview(showBackground = true)
@Composable
private fun ContactDetailsSuccessPreview() {
    KontactPlusTheme {
        ContactDetailsScreen(
            uiState = ContactDetailsUiState.Success(
                Contact(1, "k1", "Akash Yadav", listOf("1234567890", "7564817434"))
            ),
            onBackClick = {},
            onPhoneNumberClick = {},
            onFavouriteClick = {},
            onEditContactClick = {},
            onManageRelationship = {},
            onRetry = {}
        )
    }
}
