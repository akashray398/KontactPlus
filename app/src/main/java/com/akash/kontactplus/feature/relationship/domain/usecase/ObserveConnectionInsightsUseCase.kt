package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.logic.ConnectionInsightEngine
import com.akash.kontactplus.feature.relationship.domain.model.ConnectionInsight
import com.akash.kontactplus.feature.relationship.domain.repository.*
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class ObserveConnectionInsightsUseCase @Inject constructor(
    private val connectionInsightsRepository: ConnectionInsightsRepository,
    private val interactionInsightsRepository: InteractionInsightsRepository,
    private val relationshipRepository: RelationshipRepository,
    private val engine: ConnectionInsightEngine
) {
    operator fun invoke(): Flow<List<ConnectionInsight>> {
        return connectionInsightsRepository.areConnectionInsightsEnabled().flatMapLatest { enabled ->
            if (!enabled) return@flatMapLatest flowOf(emptyList())

            combine(
                relationshipRepository.observeAllEnabledFollowUpPreferences(),
                interactionInsightsRepository.observeInteractionSummaries(),
                relationshipRepository.observeAllUpcomingDates(),
                relationshipRepository.observeScheduledReminders(),
                relationshipRepository.observeNonActiveSuggestionActions()
            ) { prefs, summaries, dates, reminders, actions ->
                engine.calculateInsights(
                    now = Instant.now(),
                    zoneId = ZoneId.systemDefault(),
                    followUpPreferences = prefs,
                    interactionSummaries = summaries,
                    importantDates = dates,
                    reminders = reminders,
                    suggestionActions = actions
                )
            }
        }
    }
}
