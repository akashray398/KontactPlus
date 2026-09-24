package com.akash.kontactplus.feature.relationship.domain.model

enum class RelationshipHealthStatus {
    Thriving,    // 80-100
    Good,        // 60-79
    NeedsAttention, // 40-59
    AtRisk       // 0-39
}

data class RelationshipHealth(
    val lookupKey: String,
    val score: Int,
    val status: RelationshipHealthStatus,
    val explanation: String,
    val riskReasons: List<String> = emptyList(),
    val suggestedActions: List<String> = emptyList(),
    val daysSinceLastInteraction: Long? = null
)
