package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.logic.RelationshipHealthEngine
import com.akash.kontactplus.feature.relationship.domain.model.ContactFact
import com.akash.kontactplus.feature.relationship.domain.model.ContactInteractionSummary
import com.akash.kontactplus.feature.relationship.domain.model.ImportantDate
import com.akash.kontactplus.feature.relationship.domain.model.RelationshipHealth
import com.akash.kontactplus.feature.relationship.domain.model.RelationshipReminder
import com.akash.kontactplus.feature.relationship.domain.repository.InteractionInsightsRepository
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class MemoryReplayBriefing(
    val lookupKey: String,
    val contactName: String,
    val whyDoIKnowThisPerson: String,
    val health: RelationshipHealth,
    val keyFacts: List<ContactFact>,
    val openReminders: List<RelationshipReminder>,
    val upcomingDates: List<ImportantDate>,
    val privateNoteSummary: String
)

class GetMemoryReplayUseCase @Inject constructor(
    private val relationshipRepository: RelationshipRepository,
    private val interactionInsightsRepository: InteractionInsightsRepository,
    private val healthEngine: RelationshipHealthEngine
) {
    operator fun invoke(lookupKey: String, contactName: String): Flow<MemoryReplayBriefing> {
        return combine(
            relationshipRepository.observeRelationship(lookupKey),
            relationshipRepository.observeFactsForContact(lookupKey),
            relationshipRepository.observeFollowUpPreference(lookupKey),
            interactionInsightsRepository.observeInteractionSummaries()
        ) { relationship, facts, followUpPref, summaries ->
            val interactionSummary: ContactInteractionSummary? = summaries[lookupKey]
            val health = healthEngine.calculateHealth(
                lookupKey = lookupKey,
                interactionSummary = interactionSummary,
                followUpPref = followUpPref,
                factsCount = facts.size,
                hasPrivateNote = relationship?.privateNote?.isNotBlank() == true,
                upcomingDatesCount = relationship?.importantDates?.size ?: 0,
                pendingRemindersCount = relationship?.reminders?.count { it.status == com.akash.kontactplus.feature.relationship.domain.model.ReminderStatus.Scheduled } ?: 0
            )

            val tagsText = relationship?.tags?.joinToString { it.name } ?: ""
            val whyDoIKnow = buildString {
                append("Relationship context for $contactName. ")
                if (tagsText.isNotBlank()) {
                    append("Categorized under [$tagsText]. ")
                }
                if (relationship?.privateNote?.isNotBlank() == true) {
                    append("Notes: ${relationship.privateNote.take(100)}. ")
                }
                if (facts.isNotEmpty()) {
                    val professionalFact = facts.firstOrNull { it.category == com.akash.kontactplus.feature.relationship.domain.model.FactCategory.Professional }
                    if (professionalFact != null) {
                        append("Work / Role: ${professionalFact.fact}. ")
                    }
                }
                if (length == "Relationship context for $contactName. ".length) {
                    append("Standard contact in your phone directory.")
                }
            }

            MemoryReplayBriefing(
                lookupKey = lookupKey,
                contactName = contactName,
                whyDoIKnowThisPerson = whyDoIKnow,
                health = health,
                keyFacts = facts.take(5),
                openReminders = relationship?.reminders?.filter { it.status == com.akash.kontactplus.feature.relationship.domain.model.ReminderStatus.Scheduled } ?: emptyList(),
                upcomingDates = relationship?.importantDates ?: emptyList(),
                privateNoteSummary = relationship?.privateNote ?: ""
            )
        }
    }
}
