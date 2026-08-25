package com.akash.kontactplus.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme

/**
 * A custom card following Kontact++ design system.
 *
 * @param modifier The modifier to be applied to the card.
 * @param onClick Optional callback when the card is clicked.
 * @param content The content to be displayed within the card.
 */
@Composable
fun KontactCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KontactCardPreview() {
    KontactPlusTheme {
        KontactCard {
            Column {
                androidx.compose.material3.Text("Non-clickable Card Content")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KontactCardClickablePreview() {
    KontactPlusTheme(darkTheme = true) {
        KontactCard(onClick = {}) {
            Column {
                androidx.compose.material3.Text("Clickable Card Content")
            }
        }
    }
}
