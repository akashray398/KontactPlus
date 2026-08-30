package com.akash.kontactplus.feature.dialpad.domain.model

/**
 * Represents a contact suggestion while dialing.
 */
data class DialpadSuggestion(
    val lookupKey: String,
    val displayName: String,
    val phoneNumber: String,
    val photoUri: String? = null
)
