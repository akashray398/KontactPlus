package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.model.ConnectionInsight
import com.akash.kontactplus.feature.relationship.domain.repository.*
import javax.inject.Inject

class DismissConnectionInsightUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke(insight: ConnectionInsight): Result<Unit> {
        val action = ConnectionSuggestionAction(
            suggestionKey = insight.id,
            lookupKey = insight.lookupKey,
            ruleType = insight.type.name,
            sourceTimestampMillis = insight.occurredAt?.toEpochMilli(),
            state = ConnectionSuggestionState.Dismissed,
            snoozedUntil = null
        )
        return repository.saveSuggestionAction(action)
    }
}
