package com.akash.kontactplus.feature.relationship.domain.model

enum class FactCategory {
    Personal,
    Professional,
    Hobby,
    Preference,
    Conversation
}

data class ContactFact(
    val id: Long = 0,
    val lookupKey: String,
    val fact: String,
    val category: FactCategory = FactCategory.Personal,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
