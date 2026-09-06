package com.akash.kontactplus.feature.relationship.domain.repository

import kotlinx.coroutines.flow.Flow

interface ConnectionInsightsRepository {
    fun areConnectionInsightsEnabled(): Flow<Boolean>
    suspend fun setConnectionInsightsEnabled(enabled: Boolean)

    fun areCallHistoryInsightsEnabled(): Flow<Boolean>
    suspend fun setCallHistoryInsightsEnabled(enabled: Boolean)

    fun hasAcceptedCallHistoryDisclosure(): Flow<Boolean>
    suspend fun setCallHistoryDisclosureAccepted(accepted: Boolean)

    fun shouldShowMissedCallSuggestions(): Flow<Boolean>
    suspend fun setShowMissedCallSuggestions(show: Boolean)

    fun shouldShowImportantDateSuggestions(): Flow<Boolean>
    suspend fun setShowImportantDateSuggestions(show: Boolean)

    fun shouldShowReminderSuggestions(): Flow<Boolean>
    suspend fun setShowReminderSuggestions(show: Boolean)

    fun getDefaultSnoozeDays(): Flow<Int>
    suspend fun setDefaultSnoozeDays(days: Int)
}
