package com.akash.kontactplus.feature.ai.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.ai.domain.model.AiGenerationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiResultScreen(
    result: AiGenerationResult,
    onBackClick: () -> Unit,
    onCopyClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ai_result_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (result) {
                is AiGenerationResult.Success -> {
                    Text(text = stringResource(R.string.ai_generated_label), style = MaterialTheme.typography.labelMedium)
                    
                    OutlinedTextField(
                        value = result.text,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                        label = { Text(stringResource(R.string.ai_review_before_sending)) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onCopyClick(result.text) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.ai_copy))
                        }
                        Button(
                            onClick = { onShareClick(result.text) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.ai_share))
                        }
                    }
                }
                AiGenerationResult.Offline -> {
                    ErrorState(stringResource(R.string.ai_offline))
                }
                AiGenerationResult.RateLimited -> {
                    ErrorState(stringResource(R.string.ai_rate_limited))
                }
                else -> {
                    ErrorState(stringResource(R.string.ai_failed))
                }
            }
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}
