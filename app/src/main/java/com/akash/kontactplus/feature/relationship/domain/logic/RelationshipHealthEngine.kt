package com.akash.kontactplus.feature.relationship.domain.logic

import com.akash.kontactplus.feature.relationship.domain.model.*
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class RelationshipHealthEngine @Inject constructor() {

    fun calculateHealth(
        lookupKey: String,
        interactionSummary: ContactInteractionSummary?,
        followUpPref: ContactFollowUpPreference?,
        factsCount: Int,
        hasPrivateNote: Boolean,
        upcomingDatesCount: Int,
        pendingRemindersCount: Int,
        now: Instant = Instant.now(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): RelationshipHealth {
        var baseScore = 50
        val riskReasons = mutableListOf<String>()
        val suggestedActions = mutableListOf<String>()

        val today = now.atZone(zoneId).toLocalDate()
        val lastInteractionDate = interactionSummary?.lastInteractionAt?.atZone(zoneId)?.toLocalDate()
        val daysSinceLastInteraction = if (lastInteractionDate != null) {
            ChronoUnit.DAYS.between(lastInteractionDate, today).coerceAtLeast(0)
        } else null

        // 1. Recency & Cadence Scoring
        val targetCadenceDays = when (followUpPref?.cadence) {
            FollowUpCadence.Weekly -> 7
            FollowUpCadence.EveryTwoWeeks -> 14
            FollowUpCadence.Monthly -> 30
            FollowUpCadence.EveryThreeMonths -> 90
            FollowUpCadence.Custom -> followUpPref.customIntervalDays ?: 30
            else -> 30 // default 30 days
        }

        if (daysSinceLastInteraction != null) {
            val ratio = daysSinceLastInteraction.toDouble() / targetCadenceDays
            when {
                ratio <= 0.5 -> baseScore += 30
                ratio <= 1.0 -> baseScore += 15
                ratio <= 1.5 -> {
                    baseScore -= 10
                    riskReasons.add("Past due follow-up ($daysSinceLastInteraction days since last interaction)")
                    suggestedActions.add("Send a quick text or call to re-engage")
                }
                else -> {
                    baseScore -= 30
                    riskReasons.add("Significantly inactive ($daysSinceLastInteraction days without interaction)")
                    suggestedActions.add("Reach out soon to prevent relationship decay")
                }
            }
        } else {
            baseScore -= 15
            riskReasons.add("No recorded call or interaction history")
            suggestedActions.add("Make your first call to establish connection history")
        }

        // 2. Engagement & Memory depth (Facts, Notes, Dates)
        if (factsCount > 0) baseScore += (factsCount * 3).coerceAtMost(15)
        if (hasPrivateNote) baseScore += 5
        if (upcomingDatesCount > 0) baseScore += 5

        // 3. Unreturned Missed Calls Penalty
        val lastMissed = interactionSummary?.lastMissedCallAt
        val lastOutgoing = interactionSummary?.lastOutgoingCallAt
        if (lastMissed != null && (lastOutgoing == null || lastOutgoing.isBefore(lastMissed))) {
            baseScore -= 15
            riskReasons.add("Unreturned missed call")
            suggestedActions.add("Return the missed call")
        }

        val finalScore = baseScore.coerceIn(0, 100)
        val status = when {
            finalScore >= 80 -> RelationshipHealthStatus.Thriving
            finalScore >= 60 -> RelationshipHealthStatus.Good
            finalScore >= 40 -> RelationshipHealthStatus.NeedsAttention
            else -> RelationshipHealthStatus.AtRisk
        }

        val explanation = buildString {
            append("Score $finalScore% - $status. ")
            if (daysSinceLastInteraction != null) {
                append("Last contact $daysSinceLastInteraction days ago. ")
            }
            if (factsCount > 0) {
                append("$factsCount memory facts saved.")
            }
        }

        return RelationshipHealth(
            lookupKey = lookupKey,
            score = finalScore,
            status = status,
            explanation = explanation,
            riskReasons = riskReasons,
            suggestedActions = suggestedActions,
            daysSinceLastInteraction = daysSinceLastInteraction
        )
    }
}
