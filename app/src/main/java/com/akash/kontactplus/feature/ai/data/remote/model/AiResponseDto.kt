package com.akash.kontactplus.feature.ai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AiResponseDto(
    val requestId: String,
    val text: String,
    val modelLabel: String? = null,
    val finishReason: String? = null
)
