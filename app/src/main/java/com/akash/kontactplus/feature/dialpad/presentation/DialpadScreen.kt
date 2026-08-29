package com.akash.kontactplus.feature.dialpad.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.KontactPlusTheme
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium

@Composable
fun DialpadScreen(
    modifier: Modifier = Modifier,
    prefilledNumber: String? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Dialpad,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(SpaceMedium))
        
        Text(
            text = stringResource(R.string.title_keypad),
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = prefilledNumber ?: stringResource(R.string.description_keypad),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DialpadScreenPreview() {
    KontactPlusTheme {
        DialpadScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun DialpadScreenPrefilledPreview() {
    KontactPlusTheme {
        DialpadScreen(prefilledNumber = "+123456789")
    }
}
