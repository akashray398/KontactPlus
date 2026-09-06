package com.akash.kontactplus.feature.relationship.domain.repository

import com.akash.kontactplus.feature.relationship.domain.model.ContactInteractionSummary
import kotlinx.coroutines.flow.Flow

interface InteractionInsightsRepository {
    fun observeInteractionSummaries(): Flow<Map<String, ContactInteractionSummary>>
    suspend fun refresh()
}
