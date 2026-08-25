package com.akash.kontactplus.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.akash.kontactplus.core.designsystem.theme.ContactAvatarMedium
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import kotlin.math.abs

/**
 * A circular avatar component that displays a contact's photo or initials.
 *
 * @param displayName The name of the contact to generate initials from.
 * @param modifier The modifier to be applied to the avatar.
 * @param photoUri Optional photo URI (placeholder for now).
 * @param size The size of the avatar.
 */
@Composable
fun ContactAvatar(
    displayName: String,
    modifier: Modifier = Modifier,
    photoUri: String? = null,
    size: Dp = ContactAvatarMedium
) {
    val initials = remember(displayName) {
        getInitials(displayName)
    }
    val backgroundColor = remember(displayName) {
        generateAvatarColor(displayName)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .semantics {
                contentDescription = "Avatar for $displayName"
            },
        contentAlignment = Alignment.Center
    ) {
        if (initials.isNotEmpty()) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        } else {
            Text(
                text = "?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

private fun getInitials(name: String): String {
    if (name.isBlank()) return ""
    val parts = name.trim().split("\\s+".toRegex())
    return when {
        parts.size >= 2 -> {
            "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
        }
        parts.size == 1 -> {
            parts[0].first().uppercase()
        }
        else -> ""
    }
}

private fun generateAvatarColor(name: String): Color {
    val colors = listOf(
        Color(0xFF8B5CF6), // BrandViolet
        Color(0xFF22D3EE), // ElectricCyan
        Color(0xFF22C55E), // SuccessGreen
        Color(0xFFF59E0B), // WarningAmber
        Color(0xFFFF6B6B), // ErrorRed
        Color(0xFFEC4899), // Pink
        Color(0xFF3B82F6), // Blue
        Color(0xFF10B981)  // Emerald
    )
    if (name.isBlank()) return colors[0]
    val hash = name.hashCode()
    val index = abs(hash) % colors.size
    return colors[index]
}

@Preview(showBackground = true)
@Composable
private fun ContactAvatarInitialsPreview() {
    KontactPlusTheme {
        ContactAvatar(displayName = "Akash Patel")
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactAvatarSingleInitialPreview() {
    KontactPlusTheme {
        ContactAvatar(displayName = "Jane")
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactAvatarEmptyPreview() {
    KontactPlusTheme {
        ContactAvatar(displayName = "")
    }
}
