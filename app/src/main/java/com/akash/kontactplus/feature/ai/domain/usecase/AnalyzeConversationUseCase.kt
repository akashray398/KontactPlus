package com.akash.kontactplus.feature.ai.domain.usecase

import com.akash.kontactplus.feature.ai.domain.model.AiActionType
import com.akash.kontactplus.feature.ai.domain.model.AiDraftContext
import com.akash.kontactplus.feature.ai.domain.model.AiGenerationResult
import com.akash.kontactplus.feature.ai.domain.repository.AiRepository
import com.akash.kontactplus.feature.relationship.domain.model.ContactFact
import com.akash.kontactplus.feature.relationship.domain.model.FactCategory
import com.akash.kontactplus.feature.relationship.domain.model.RelationshipReminder
import com.akash.kontactplus.feature.relationship.domain.model.ReminderStatus
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

data class ConversationAnalysisResult(
    val extractedFacts: List<ContactFact>,
    val extractedReminders: List<RelationshipReminder>,
    val summary: String
)

class AnalyzeConversationUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val relationshipRepository: RelationshipRepository
) {
    suspend operator fun invoke(
        lookupKey: String,
        contactName: String,
        rawNotes: String
    ): Result<ConversationAnalysisResult> = runCatching {
        if (rawNotes.isBlank()) {
            return Result.success(ConversationAnalysisResult(emptyList(), emptyList(), ""))
        }

        val facts = mutableListOf<ContactFact>()
        val reminders = mutableListOf<RelationshipReminder>()
        var summary = ""

        // Try AI generation first
        val aiContext = AiDraftContext(
            actionType = AiActionType.SummarizeSelectedNote,
            tone = com.akash.kontactplus.feature.ai.domain.model.AiTone.Professional,
            userInstruction = "Extract key personal/professional facts and follow-up reminders from this note for $contactName",
            selectedText = rawNotes,
            contactAlias = contactName
        )

        val aiResult = try { aiRepository.generateText(aiContext) } catch (_: Exception) { null }

        summary = if (aiResult is AiGenerationResult.Success) {
            aiResult.text
        } else {
            "Call / Meeting note with $contactName"
        }

        // On-device pattern extraction (local-first fallback & enhancement)
        val lines = rawNotes.lines().map { it.trim() }.filter { it.isNotBlank() }
        for (line in lines) {
            val lower = line.lowercase()
            when {
                lower.startsWith("remind") || lower.contains("follow up") || lower.contains("todo") -> {
                    reminders.add(
                        RelationshipReminder(
                            id = UUID.randomUUID().toString(),
                            lookupKey = lookupKey,
                            title = line.take(60),
                            note = line,
                            scheduledAt = Instant.now().plus(3, ChronoUnit.DAYS),
                            status = ReminderStatus.Scheduled
                        )
                    )
                }
                lower.contains("works at") || lower.contains("company") || lower.contains("role") -> {
                    facts.add(
                        ContactFact(
                            lookupKey = lookupKey,
                            fact = line,
                            category = FactCategory.Professional
                        )
                    )
                }
                lower.contains("likes") || lower.contains("loves") || lower.contains("hobby") || lower.contains("plays") -> {
                    facts.add(
                        ContactFact(
                            lookupKey = lookupKey,
                            fact = line,
                            category = FactCategory.Hobby
                        )
                    )
                }
                lower.contains("birthday") || lower.contains("wife") || lower.contains("husband") || lower.contains("son") || lower.contains("daughter") -> {
                    facts.add(
                        ContactFact(
                            lookupKey = lookupKey,
                            fact = line,
                            category = FactCategory.Personal
                        )
                    )
                }
                else -> {
                    if (line.length in 5..120) {
                        facts.add(
                            ContactFact(
                                lookupKey = lookupKey,
                                fact = line,
                                category = FactCategory.Conversation
                            )
                        )
                    }
                }
            }
        }

        // Auto-save extracted facts and reminders to database
        facts.forEach { fact -> relationshipRepository.saveFact(fact) }
        reminders.forEach { reminder -> relationshipRepository.scheduleReminder(reminder) }

        ConversationAnalysisResult(
            extractedFacts = facts,
            extractedReminders = reminders,
            summary = summary
        )
    }
}
