package com.akash.kontactplus.feature.recents.domain.model

/**
 * Domain model representing a recent call in the log.
 */
data class RecentCall(
    val id: Long,
    val phoneNumber: String,
    val cachedName: String? = null,
    val contactLookupKey: String? = null,
    val resolvedDisplayName: String? = null,
    val type: RecentCallType,
    val timestampMillis: Long,
    val durationSeconds: Long,
    val isNew: Boolean
)
