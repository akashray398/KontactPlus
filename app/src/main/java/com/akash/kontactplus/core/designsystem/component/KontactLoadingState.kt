package com.akash.kontactplus.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme

/**
 * Reusable loading state component.
 */
@Composable
fun KontactLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
private fun KontactLoadingStatePreview() {
    KontactPlusTheme {
        KontactLoadingState()
    }
}
