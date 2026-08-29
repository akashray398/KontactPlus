package com.akash.kontactplus.feature.recents.domain.usecase

import com.akash.kontactplus.feature.recents.domain.model.RecentCall
import javax.inject.Inject

/**
 * Use case to filter recent calls based on a search query.
 */
class FilterRecentCallsUseCase @Inject constructor() {

    operator fun invoke(calls: List<RecentCall>, query: String): List<RecentCall> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return calls

        val normalizedQuery = trimmedQuery.filter { it.isDigit() || it == '+' }
        
        return calls.filter { call ->
            call.phoneNumber.filter { it.isDigit() || it == '+' }.contains(normalizedQuery) ||
                (call.resolvedDisplayName ?: "").contains(trimmedQuery, ignoreCase = true) ||
                (call.cachedName ?: "").contains(trimmedQuery, ignoreCase = true)
        }
    }
}
