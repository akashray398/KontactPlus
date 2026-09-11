package com.akash.kontactplus.feature.relationship.domain.logic

import com.akash.kontactplus.R
import com.akash.kontactplus.feature.relationship.domain.model.*
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionSuggestionAction
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionSuggestionState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ConnectionInsightEngine @Inject constructor() {

    companion object {
        private const val MISSED_CALL_WINDOW_DAYS = 7L
        private const val IMPORTANT_DATE_WINDOW_DAYS = 30L
    }

    fun calculateInsights(
        now: Instant,
        zoneId: ZoneId,
        followUpPreferences: List<ContactFollowUpPreference>,
        interactionSummaries: Map<String, ContactInteractionSummary>,
        importantDates: List<ImportantDate>,
        reminders: List<RelationshipReminder>,
        suggestionActions: List<ConnectionSuggestionAction>
    ): List<ConnectionInsight> {
        val insights = mutableListOf<ConnectionInsight>()
        val today = now.atZone(zoneId).toLocalDate()

        // 1. Reminders
        insights.addAll(calculateReminderInsights(reminders, now, suggestionActions))

        // 2. Missed Calls
        insights.addAll(calculateMissedCallInsights(interactionSummaries, now, zoneId, suggestionActions))

        // 3. Important Dates
        insights.addAll(calculateImportantDateInsights(importantDates, today, suggestionActions))

        // 4. User-configured Follow-ups (Cadence)
        insights.addAll(calculateFollowUpInsights(followUpPreferences, interactionSummaries, today, zoneId, suggestionActions))

        return insights.sortedWith(
            compareBy<ConnectionInsight> { it.priority }.reversed()
                .thenBy { it.dueAt ?: Instant.MAX }
                .thenBy { it.lookupKey }
        )
    }

    private fun calculateReminderInsights(
        reminders: List<RelationshipReminder>,
        now: Instant,
        suggestionActions: List<ConnectionSuggestionAction>
    ): List<ConnectionInsight> {
        return reminders.filter { it.status == ReminderStatus.Scheduled }
            .map { reminder ->
                val isOverdue = reminder.scheduledAt.isBefore(now)
                ConnectionInsight(
                    id = "reminder_${reminder.id}",
                    lookupKey = reminder.lookupKey,
                    type = if (isOverdue) ConnectionInsightType.ReminderOverdue else ConnectionInsightType.ReminderDue,
                    priority = if (isOverdue) ConnectionInsightPriority.Overdue else ConnectionInsightPriority.Due,
                    titleRes = if (isOverdue) R.string.insight_reminder_overdue_title else R.string.insight_reminder_due_title,
                    explanationRes = R.string.relationship_reminders,
                    explanationArgs = listOf(reminder.title),
                    occurredAt = reminder.scheduledAt,
                    dueAt = reminder.scheduledAt,
                    availableActions = listOf(ConnectionInsightAction.MarkDone, ConnectionInsightAction.ViewContact),
                    source = ConnectionInsightSource.Reminder
                )
            }.filter { insight -> !isActioned(insight.id, suggestionActions, now) }
    }

    private fun calculateMissedCallInsights(
        summaries: Map<String, ContactInteractionSummary>,
        now: Instant,
        zoneId: ZoneId,
        suggestionActions: List<ConnectionSuggestionAction>
    ): List<ConnectionInsight> {
        val windowStart = now.minus(MISSED_CALL_WINDOW_DAYS, ChronoUnit.DAYS)
        return summaries.values.filter { summary ->
            val lastMissed = summary.lastMissedCallAt
            val lastOutgoing = summary.lastOutgoingCallAt
            
            lastMissed != null && 
            lastMissed.isAfter(windowStart) && 
            (lastOutgoing == null || lastOutgoing.isBefore(lastMissed))
        }.map { summary ->
            ConnectionInsight(
                id = "missed_${summary.lookupKey}_${summary.lastMissedCallAt!!.toEpochMilli()}",
                lookupKey = summary.lookupKey,
                type = ConnectionInsightType.UnreturnedMissedCall,
                priority = ConnectionInsightPriority.Due,
                titleRes = R.string.insight_missed_call_title,
                explanationRes = R.string.insight_missed_call_explanation,
                explanationArgs = listOf(summary.lastMissedCallAt.toEpochMilli().toString()), // Presentation will format
                occurredAt = summary.lastMissedCallAt,
                dueAt = summary.lastMissedCallAt,
                availableActions = listOf(ConnectionInsightAction.Call, ConnectionInsightAction.Dismiss),
                source = ConnectionInsightSource.MissedCall
            )
        }.filter { insight -> !isActioned(insight.id, suggestionActions, now) }
    }

    private fun calculateImportantDateInsights(
        dates: List<ImportantDate>,
        today: LocalDate,
        suggestionActions: List<ConnectionSuggestionAction>
    ): List<ConnectionInsight> {
        return dates.mapNotNull { date ->
            val nextOccurrence = calculateNextOccurrence(date.localDate, today, date.repeatsYearly)
            val daysUntil = ChronoUnit.DAYS.between(today, nextOccurrence)
            
            if (daysUntil in 0..IMPORTANT_DATE_WINDOW_DAYS) {
                val priority = if (daysUntil <= 7) ConnectionInsightPriority.Due else ConnectionInsightPriority.Soon
                ConnectionInsight(
                    id = "date_${date.id}_${nextOccurrence}",
                    lookupKey = date.lookupKey,
                    type = ConnectionInsightType.ImportantDateApproaching,
                    priority = priority,
                    titleRes = R.string.insight_date_approaching_title,
                    explanationRes = R.string.insight_date_approaching_explanation,
                    explanationArgs = listOf(date.title, daysUntil.toString()),
                    occurredAt = null,
                    dueAt = nextOccurrence.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    availableActions = listOf(ConnectionInsightAction.DraftMessage, ConnectionInsightAction.ViewContact, ConnectionInsightAction.Dismiss),
                    source = ConnectionInsightSource.ImportantDate
                )
            } else null
        }.filter { insight -> !isActioned(insight.id, suggestionActions, Instant.now()) }
    }

    private fun calculateFollowUpInsights(
        preferences: List<ContactFollowUpPreference>,
        summaries: Map<String, ContactInteractionSummary>,
        today: LocalDate,
        zoneId: ZoneId,
        suggestionActions: List<ConnectionSuggestionAction>
    ): List<ConnectionInsight> {
        return preferences.filter { it.enabled }.mapNotNull { pref ->
            val summary = summaries[pref.lookupKey] ?: return@mapNotNull null
            val lastInteraction = summary.lastInteractionAt?.atZone(zoneId)?.toLocalDate() ?: return@mapNotNull null
            
            val nextFollowUp = calculateNextFollowUp(lastInteraction, pref.cadence, pref.customIntervalDays)
            
            if (today.isAfter(nextFollowUp) || today == nextFollowUp) {
                val isOverdue = today.isAfter(nextFollowUp.plusDays(7))
                val daysSince = ChronoUnit.DAYS.between(lastInteraction, today)
                ConnectionInsight(
                    id = "cadence_${pref.lookupKey}_${nextFollowUp}",
                    lookupKey = pref.lookupKey,
                    type = if (isOverdue) ConnectionInsightType.FollowUpOverdue else ConnectionInsightType.FollowUpDue,
                    priority = if (isOverdue) ConnectionInsightPriority.Overdue else ConnectionInsightPriority.Normal,
                    titleRes = R.string.insight_follow_up_due_title,
                    explanationRes = R.string.insight_follow_up_due_explanation,
                    explanationArgs = listOf(pref.cadence.name.lowercase(), daysSince.toString()),
                    occurredAt = summary.lastInteractionAt,
                    dueAt = nextFollowUp.atStartOfDay(zoneId).toInstant(),
                    availableActions = listOf(ConnectionInsightAction.Call, ConnectionInsightAction.DraftMessage, ConnectionInsightAction.Dismiss),
                    source = ConnectionInsightSource.UserCadence
                )
            } else null
        }.filter { insight -> !isActioned(insight.id, suggestionActions, Instant.now()) }
    }

    private fun isActioned(id: String, actions: List<ConnectionSuggestionAction>, now: Instant): Boolean {
        val action = actions.find { it.suggestionKey == id } ?: return false
        return when (action.state) {
            ConnectionSuggestionState.Dismissed -> true
            ConnectionSuggestionState.Completed -> true
            ConnectionSuggestionState.Snoozed -> action.snoozedUntil != null && action.snoozedUntil.isAfter(now)
            ConnectionSuggestionState.Active -> false
        }
    }

    private fun calculateNextOccurrence(date: LocalDate, today: LocalDate, repeatsYearly: Boolean): LocalDate {
        if (!repeatsYearly) return date
        
        var next = date.withYear(today.year)
        if (next.isBefore(today)) {
            next = next.plusYears(1)
        }
        if (date.monthValue == 2 && date.dayOfMonth == 29 && !next.isLeapYear) {
            next = next.withDayOfMonth(28)
        }
        return next
    }

    private fun calculateNextFollowUp(last: LocalDate, cadence: FollowUpCadence, customDays: Int?): LocalDate {
        return when (cadence) {
            FollowUpCadence.Weekly -> last.plusWeeks(1)
            FollowUpCadence.EveryTwoWeeks -> last.plusWeeks(2)
            FollowUpCadence.Monthly -> last.plusMonths(1)
            FollowUpCadence.EveryThreeMonths -> last.plusMonths(3)
            FollowUpCadence.Custom -> last.plusDays((customDays ?: 30).toLong())
            FollowUpCadence.Disabled -> last.plusYears(100)
        }
    }
}
