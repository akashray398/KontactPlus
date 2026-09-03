package com.akash.kontactplus.feature.ai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AiRequestDto(
    val action: String,
    val tone: String,
    val instruction: String,
    val selectedText: String = "",
    val contactAlias: String = "Contact",
    val context: String? = null,
    val locale: String = "en-IN"
)
