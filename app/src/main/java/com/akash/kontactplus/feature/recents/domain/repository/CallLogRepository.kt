package com.akash.kontactplus.feature.recents.domain.repository

import com.akash.kontactplus.feature.recents.domain.model.RecentCall

/**
 * Domain-level repository interface for call log operations.
 */
interface CallLogRepository {
    /**
     * Retrieves recent calls from the system.
     * @param limit Maximum number of records to return.
     * @return A [Result] containing a list of [RecentCall]s.
     */
    suspend fun getRecentCalls(limit: Int): Result<List<RecentCall>>
}
