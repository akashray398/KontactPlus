package com.akash.kontactplus.feature.contacts.presentation

import androidx.annotation.StringRes
import com.akash.kontactplus.feature.ai.domain.usecase.ConversationAnalysisResult
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.relationship.domain.model.*
import com.akash.kontactplus.feature.relationship.domain.usecase.MemoryReplayBriefing

/**
 * Immutable state for the Contact Details screen.
 */
sealed interface ContactDetailsUiState {
    data object Loading : ContactDetailsUiState
    data class Success(
        val contact: Contact,
        val isFavourite: Boolean = false,
        val relationship: ContactRelationship? = null,
        val health: RelationshipHealth? = null,
        val facts: List<ContactFact> = emptyList(),
        val timeline: List<RelationshipTimelineItem> = emptyList(),
        val memoryReplay: MemoryReplayBriefing? = null,
        val isAnalyzingConversation: Boolean = false,
        val analysisResult: ConversationAnalysisResult? = null,
        val isFavouriteActionInProgress: Boolean = false,
        @StringRes val favouriteActionErrorRes: Int? = null
    ) : ContactDetailsUiState
    data object NotFound : ContactDetailsUiState
    data class Error(@StringRes val messageRes: Int) : ContactDetailsUiState
}
