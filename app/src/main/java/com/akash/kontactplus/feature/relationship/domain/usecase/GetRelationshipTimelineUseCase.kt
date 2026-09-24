package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.recents.domain.repository.CallLogRepository
import com.akash.kontactplus.feature.relationship.domain.model.*
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class GetRelationshipTimelineUseCase @Inject constructor(
    private val relationshipRepository: RelationshipRepository,
    private val callLogRepository: CallLogRepository
) {
    operator fun invoke(lookupKey: String, contactPhoneNumbers: List<String>): Flow<List<RelationshipTimelineItem>> {
        val callsFlow = flow {
            val calls = callLogRepository.getRecentCalls(limit = 100).getOrDefault(emptyList())
            val normalizedNumbers = contactPhoneNumbers.map { it.replace(Regex("[^0-9+]"), "") }
            val matchingCalls = calls.filter { recentCall ->
                val normNumber = recentCall.phoneNumber.replace(Regex("[^0-9+]"), "")
                normNumber.isNotEmpty() && normalizedNumbers.any { it.endsWith(normNumber) || normNumber.endsWith(it) }
            }
            emit(matchingCalls)
        }

        return combine(
            relationshipRepository.observeRelationship(lookupKey),
            relationshipRepository.observeFactsForContact(lookupKey),
            callsFlow
        ) { relationship, facts, calls ->
            val timeline = mutableListOf<RelationshipTimelineItem>()

            // 1. Facts
            facts.forEach { fact ->
                timeline.add(
                    RelationshipTimelineItem.FactEvent(
                        id = "fact_${fact.id}",
                        timestamp = Instant.ofEpochMilli(fact.createdAtEpochMillis),
                        title = "Memory Fact Added (${fact.category.name})",
                        description = fact.fact,
                        category = fact.category
                    )
                )
            }

            // 2. Private Note
            if (relationship?.privateNote?.isNotBlank() == true) {
                timeline.add(
                    RelationshipTimelineItem.NoteEvent(
                        id = "note_${relationship.updatedAtEpochMillis}",
                        timestamp = Instant.ofEpochMilli(relationship.updatedAtEpochMillis),
                        title = "Relationship Note",
                        description = relationship.privateNote
                    )
                )
            }

            // 3. Important Dates
            relationship?.importantDates?.forEach { date ->
                val dateInstant = date.localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                timeline.add(
                    RelationshipTimelineItem.DateEvent(
                        id = "date_${date.id}",
                        timestamp = dateInstant,
                        title = date.title,
                        description = "${date.type.name} on ${date.localDate}",
                        repeatsYearly = date.repeatsYearly
                    )
                )
            }

            // 4. Reminders
            relationship?.reminders?.forEach { reminder ->
                timeline.add(
                    RelationshipTimelineItem.ReminderEvent(
                        id = "reminder_${reminder.id}",
                        timestamp = reminder.scheduledAt,
                        title = reminder.title,
                        description = reminder.note.ifBlank { "Status: ${reminder.status.name}" },
                        isCompleted = reminder.status == ReminderStatus.Completed
                    )
                )
            }

            // 5. Calls
            calls.forEach { call ->
                timeline.add(
                    RelationshipTimelineItem.CallEvent(
                        id = "call_${call.id}",
                        timestamp = Instant.ofEpochMilli(call.timestampMillis),
                        title = "${call.type.name} Call",
                        description = "Duration: ${call.durationSeconds}s • ${call.phoneNumber}",
                        callType = call.type.name
                    )
                )
            }

            timeline.sortedByDescending { it.timestamp }
        }
    }
}
