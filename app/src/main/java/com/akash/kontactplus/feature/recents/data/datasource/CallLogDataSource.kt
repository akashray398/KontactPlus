package com.akash.kontactplus.feature.recents.data.datasource

import com.akash.kontactplus.feature.recents.domain.model.RecentCall

/**
 * Data source interface for fetching call logs from the device.
 */
interface CallLogDataSource {
    /**
     * Fetches recent calls from the device's CallLog Provider.
     * @param limit Maximum number of records to return.
     * @return A list of [RecentCall] objects.
     */
    suspend fun getRecentCalls(limit: Int): List<RecentCall>
}
