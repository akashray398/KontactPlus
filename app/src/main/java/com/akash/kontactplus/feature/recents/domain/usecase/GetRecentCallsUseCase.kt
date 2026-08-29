package com.akash.kontactplus.feature.recents.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.recents.domain.model.RecentCall
import com.akash.kontactplus.feature.recents.domain.repository.CallLogRepository
import javax.inject.Inject

/**
 * Use case to retrieve recent calls with optional contact resolution.
 */
class GetRecentCallsUseCase @Inject constructor(
    private val callLogRepository: CallLogRepository,
    private val contactsRepository: ContactsRepository
) {
    /**
     * Executes the use case.
     * @param limit Maximum number of records to return.
     * @return A [Result] containing the list of [RecentCall]s.
     */
    suspend operator fun invoke(limit: Int = 200): Result<List<RecentCall>> {
        val result = callLogRepository.getRecentCalls(limit)
        
        return if (result.isSuccess) {
            val calls = result.getOrNull() ?: emptyList()
            
            // Try to resolve contact names from the contact repository efficiently
            val contactsResult = contactsRepository.getContacts()
            val contactMap = contactsResult.getOrNull()?.associateBy { it.phoneNumbers.map { num -> normalize(num) } }
            
            // This associative strategy is a bit tricky since one contact can have multiple numbers.
            // Let's create a better map: NormalizedNumber -> Contact
            val numberMap = mutableMapOf<String, com.akash.kontactplus.feature.contacts.domain.model.Contact>()
            contactsResult.getOrNull()?.forEach { contact ->
                contact.phoneNumbers.forEach { number ->
                    numberMap[normalize(number)] = contact
                }
            }

            val resolvedCalls = calls.map { call ->
                val normalizedNumber = normalize(call.phoneNumber)
                val contact = numberMap[normalizedNumber]
                call.copy(
                    contactLookupKey = contact?.lookupKey,
                    resolvedDisplayName = contact?.displayName ?: call.cachedName
                )
            }
            Result.success(resolvedCalls)
        } else {
            result
        }
    }

    private fun normalize(number: String): String {
        return number.filter { it.isDigit() || it == '+' }
    }
}
