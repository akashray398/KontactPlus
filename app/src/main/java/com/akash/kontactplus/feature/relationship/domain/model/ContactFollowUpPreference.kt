package com.akash.kontactplus.feature.relationship.domain.model

data class ContactFollowUpPreference(
    val lookupKey: String,
    val cadence: FollowUpCadence,
    val customIntervalDays: Int? = null,
    val enabled: Boolean = false
)

enum class FollowUpCadence {
    Weekly,
    EveryTwoWeeks,
    Monthly,
    EveryThreeMonths,
    Custom,
    Disabled
}
