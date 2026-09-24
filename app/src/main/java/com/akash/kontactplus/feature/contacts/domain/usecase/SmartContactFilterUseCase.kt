package com.akash.kontactplus.feature.contacts.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.relationship.domain.model.ContactFact
import com.akash.kontactplus.feature.relationship.domain.model.ContactRelationship
import com.akash.kontactplus.feature.relationship.domain.model.RelationshipTag
import javax.inject.Inject

class SmartContactFilterUseCase @Inject constructor() {

    fun filterContacts(
        contacts: List<Contact>,
        query: String,
        relationshipsMap: Map<String, ContactRelationship> = emptyMap(),
        factsMap: Map<String, List<ContactFact>> = emptyMap(),
        tagsMap: Map<String, List<RelationshipTag>> = emptyMap()
    ): List<Contact> {
        val trimmedQuery = query.trim().lowercase()
        if (trimmedQuery.isBlank()) return contacts

        return contacts.filter { contact ->
            val lookupKey = contact.lookupKey

            // 1. Name or Phone match
            val nameMatch = contact.displayName.lowercase().contains(trimmedQuery)
            val phoneMatch = contact.phoneNumbers.any { it.contains(trimmedQuery) }
            if (nameMatch || phoneMatch) return@filter true

            // 2. Private Note match
            val rel = relationshipsMap[lookupKey]
            if (rel?.privateNote?.lowercase()?.contains(trimmedQuery) == true) return@filter true

            // 3. Facts / Company / Role match ("works at", "likes", facts)
            val facts = factsMap[lookupKey] ?: emptyList()
            if (facts.any { it.fact.lowercase().contains(trimmedQuery) }) return@filter true

            // 4. Tags match
            val tags = tagsMap[lookupKey] ?: rel?.tags ?: emptyList()
            if (tags.any { it.name.lowercase().contains(trimmedQuery) }) return@filter true

            false
        }
    }
}
