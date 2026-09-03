package com.akash.kontactplus.feature.ai.domain.model

data class AiDraftContext(
    val actionType: AiActionType,
    val tone: AiTone,
    val userInstruction: String = "",
    val selectedText: String = "",
    val contactAlias: String = "Contact",
    val relationshipContext: String? = null,
    val importantDateDescription: String? = null
)
