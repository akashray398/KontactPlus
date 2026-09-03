package com.akash.kontactplus.feature.telecom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.theme.SpaceLarge
import com.akash.kontactplus.core.designsystem.theme.SpaceMedium
import com.akash.kontactplus.core.designsystem.theme.SpaceSmall
import com.akash.kontactplus.feature.dialpad.presentation.DialpadButton

@Composable
fun DtmfKeypad(
    digits: String,
    onDigitPressed: (Char) -> Unit,
    onDigitReleased: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.active_call_dtmf_title),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.active_call_dtmf_close)
                    )
                }
            }

            Text(
                text = digits,
                style = MaterialTheme.typography.displaySmall.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = SpaceLarge),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                minLines = 1
            )

            val keys = listOf(
                '1' to "", '2' to "ABC", '3' to "DEF",
                '4' to "GHI", '5' to "JKL", '6' to "MNO",
                '7' to "PQRS", '8' to "TUV", '9' to "WXYZ",
                '*' to "", '0' to "+", '#' to ""
            )

            Column(verticalArrangement = Arrangement.spacedBy(SpaceMedium)) {
                for (i in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (j in 0 until 3) {
                            val (digit, letters) = keys[i * 3 + j]
                            DialpadButton(
                                primary = digit.toString(),
                                letters = letters,
                                onClick = { onDigitPressed(digit) },
                                onLongClick = null, // DTMF doesn't typically use long clicks
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(SpaceLarge))
        }
    }
}
