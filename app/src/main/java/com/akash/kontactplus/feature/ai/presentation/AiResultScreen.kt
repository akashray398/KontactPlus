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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.akash.kontactplus.R
import com.akash.kontactplus.core.designsystem.component.KontactErrorState
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
                title = { 
                    Text(
                        text = stringResource(R.string.ai_result_title),
                        modifier = Modifier.semantics { heading() }
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back"
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
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.ai_copy))
                        }
                        Button(
                            onClick = { onShareClick(result.text) },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.ai_share))
                        }
                    }
                }
                AiGenerationResult.Offline -> {
                    KontactErrorState(
                        title = stringResource(R.string.ai_offline),
                        description = "Please check your internet connection.",
                        onRetry = null
                    )
                }
                AiGenerationResult.RateLimited -> {
                    KontactErrorState(
                        title = stringResource(R.string.ai_rate_limited),
                        description = "Please wait a moment before trying again.",
                        onRetry = null
                    )
                }
                AiGenerationResult.Timeout -> {
                    KontactErrorState(
                        title = "Request timed out",
                        description = "The server took too long to respond. Please try again.",
                        onRetry = null
                    )
                }
                AiGenerationResult.BackendNotConfigured -> {
                    KontactErrorState(
                        title = stringResource(R.string.ai_configuration_missing),
                        description = "The AI backend is not configured for this build.",
                        onRetry = null
                    )
                }
                else -> {
                    KontactErrorState(
                        title = stringResource(R.string.ai_failed),
                        description = "Something went wrong while generating the text.",
                        onRetry = null
                    )
                }
            }
        }
    }
}
