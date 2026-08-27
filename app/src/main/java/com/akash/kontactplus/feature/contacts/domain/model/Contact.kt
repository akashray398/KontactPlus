package com.akash.kontactplus.feature.contacts.domain.model

/**
 * Domain model representing a contact in the system.
 * This model is independent of the Android framework.
 */
data class Contact(
    val id: Long,
    val lookupKey: String,
    val displayName: String,
    val phoneNumbers: List<String> = emptyList(),
    val photoUri: String? = null
)
