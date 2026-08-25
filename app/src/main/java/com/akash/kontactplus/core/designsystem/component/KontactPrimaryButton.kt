package com.akash.kontactplus.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.designsystem.theme.MinimumTouchTarget

/**
 * A primary action button following Kontact++ design system.
 *
 * @param onClick Callback when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button.
 * @param content The content of the button, typically a [androidx.compose.material3.Text] and/or an icon.
 */
@Composable
fun KontactPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = MinimumTouchTarget),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.outline
        ),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun KontactPrimaryButtonPreview() {
    KontactPlusTheme {
        KontactPrimaryButton(onClick = {}) {
            androidx.compose.material3.Text("Primary Button")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KontactPrimaryButtonDisabledPreview() {
    KontactPlusTheme {
        KontactPrimaryButton(onClick = {}, enabled = false) {
            androidx.compose.material3.Text("Disabled Button")
        }
    }
}
