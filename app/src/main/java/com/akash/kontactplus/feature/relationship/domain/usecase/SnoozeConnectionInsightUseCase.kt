package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.model.ConnectionInsight
import com.akash.kontactplus.feature.relationship.domain.repository.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class SnoozeConnectionInsightUseCase @Inject constructor(
    private val repository: RelationshipRepository,
    private val insightsRepository: ConnectionInsightsRepository
) {
    suspend operator fun invoke(insight: ConnectionInsight, days: Int? = null): Result<Unit> {
        val snoozeDays = days ?: 1 // Default 1 day
        val snoozedUntil = Instant.now().plus(snoozeDays.toLong(), ChronoUnit.DAYS)
        
        val action = ConnectionSuggestionAction(
            suggestionKey = insight.id,
            lookupKey = insight.lookupKey,
            ruleType = insight.type.name,
            sourceTimestampMillis = insight.occurredAt?.toEpochMilli(),
            state = ConnectionSuggestionState.Snoozed,
            snoozedUntil = snoozedUntil
        )
        return repository.saveSuggestionAction(action)
    }
}
