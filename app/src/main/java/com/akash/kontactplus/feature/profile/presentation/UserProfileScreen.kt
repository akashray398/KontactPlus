package com.akash.kontactplus.feature.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.ContactAvatar
import com.akash.kontactplus.core.designsystem.component.KontactCard
import com.akash.kontactplus.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    uiState: UserProfileUiState,
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToAiTools: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onUpdateNameAndPhone: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = uiState.userName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = uiState.phoneNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpaceMedium, vertical = SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Glowing Avatar Header
            GlowingProfileAvatar(
                displayName = uiState.userName,
                isVerified = uiState.isVerifiedLocal
            )

            Spacer(modifier = Modifier.height(SpaceLarge))

            // 2. Dual Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                AnimatedActionButton(
                    text = "Edit profile",
                    icon = Icons.Default.Edit,
                    containerColor = Color(0xFF1D4ED8), // Vibrant Royal Blue
                    contentColor = Color.White,
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f)
                )

                AnimatedActionButton(
                    text = "Privacy Verified",
                    icon = Icons.Default.VerifiedUser,
                    containerColor = Color(0xFF1E293B), // Dark Slate
                    contentColor = Color(0xFF38BDF8), // Bright Cyan
                    onClick = onNavigateToPrivacy,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(SpaceLarge))

            // 3. Colorful Feature List Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileFeatureCard(
                    title = "AI Relationship Memory",
                    subtitle = "${uiState.memoryFactsCount} facts & contextual takeaways saved",
                    icon = Icons.Default.Psychology,
                    iconBgColor = Color(0xFF2563EB), // Blue
                    onClick = onNavigateToAiTools
                )

                ProfileFeatureCard(
                    title = "Relationship Health",
                    subtitle = "${uiState.activeFollowUps} active follow-up schedules due",
                    icon = Icons.Default.Favorite,
                    iconBgColor = Color(0xFFDC2626), // Crimson Red
                    badgeText = if (uiState.activeFollowUps > 0) "${uiState.activeFollowUps} Due" else null,
                    badgeColor = Color(0xFFEF4444),
                    onClick = onNavigateToInsights
                )

                ProfileFeatureCard(
                    title = "Conversation Intelligence",
                    subtitle = "Draft smart notes & follow-ups effortlessly",
                    icon = Icons.Default.AutoAwesome,
                    iconBgColor = Color(0xFFEA580C), // Vibrant Amber
                    onClick = onNavigateToAiTools
                )

                ProfileFeatureCard(
                    title = "Contact Fusion & Duplicate Cleanup",
                    subtitle = "${uiState.totalContacts} total contacts indexed locally",
                    icon = Icons.Default.Merge,
                    iconBgColor = Color(0xFF7C3AED), // Violet
                    onClick = { /* Nav to Contacts */ }
                )

                ProfileFeatureCard(
                    title = "Privacy & Local Encryption",
                    subtitle = "100% on-device data processing guaranteed",
                    icon = Icons.Default.Shield,
                    iconBgColor = Color(0xFF059669), // Emerald Teal
                    onClick = onNavigateToPrivacy
                )
            }

            Spacer(modifier = Modifier.height(SpaceLarge))

            // 4. Animated Kontact++ Stats Section
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                KontactStatsSection(uiState = uiState)
            }

            Spacer(modifier = Modifier.height(SpaceLarge))
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = uiState.userName,
            currentPhone = uiState.phoneNumber,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName, newPhone ->
                onUpdateNameAndPhone(newName, newPhone)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun GlowingProfileAvatar(
    displayName: String,
    isVerified: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(top = SpaceSmall)
    ) {
        // Glowing Outer Ring
        Box(
            modifier = Modifier
                .size(118.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFF06B6D4), // Cyan
                            Color(0xFF8B5CF6), // Violet
                            Color(0xFF3B82F6), // Blue
                            Color(0xFF06B6D4)
                        )
                    )
                )
        )

        // Inner Avatar Box
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            ContactAvatar(
                displayName = displayName,
                size = 102.dp
            )
        }

        // Verified Star Badge Overlay
        if (isVerified) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = 2.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Verified",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedActionButton(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1.0f, label = "button_scale")

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .scale(buttonScale),
        shape = RoundedCornerShape(25.dp),
        color = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SpaceSmall),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    badgeText: String? = null,
    badgeColor: Color = Color.Red,
    onClick: () -> Unit
) {
    KontactCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMedium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(SpaceMedium))

            // Title & Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Optional Badge or Arrow
            if (badgeText != null) {
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun KontactStatsSection(uiState: UserProfileUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Kontact++ Stats",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.semantics { heading() }
            )
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share stats",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        KontactCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(SpaceMedium)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    icon = Icons.Default.People,
                    value = uiState.totalContacts.toString(),
                    label = "Contacts",
                    iconColor = Color(0xFF3B82F6)
                )

                StatItem(
                    icon = Icons.Default.Psychology,
                    value = uiState.memoryFactsCount.toString(),
                    label = "AI Facts",
                    iconColor = Color(0xFF8B5CF6)
                )

                StatItem(
                    icon = Icons.Default.Schedule,
                    value = uiState.timeSavedText,
                    label = "Time Saved",
                    iconColor = Color(0xFF10B981)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    iconColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentPhone: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile Details") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, phone) }) {
                Text("Save")
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
private fun UserProfileScreenPreview() {
    KontactPlusTheme {
        UserProfileScreen(
            uiState = UserProfileUiState(
                userName = "Akash Patel",
                phoneNumber = "+91 98765 43210",
                totalContacts = 142,
                activeFollowUps = 3,
                memoryFactsCount = 18
            ),
            onBackClick = {},
            onEditProfileClick = {},
            onSettingsClick = {},
            onNavigateToAiTools = {},
            onNavigateToInsights = {},
            onNavigateToPrivacy = {},
            onUpdateNameAndPhone = { _, _ -> }
        )
    }
}
