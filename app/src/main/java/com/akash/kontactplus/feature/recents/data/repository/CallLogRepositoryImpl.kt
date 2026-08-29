package com.akash.kontactplus.feature.recents.data.repository

import com.akash.kontactplus.feature.recents.data.datasource.CallLogDataSource
import com.akash.kontactplus.feature.recents.domain.model.RecentCall
import com.akash.kontactplus.feature.recents.domain.repository.CallLogRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * Data-level implementation of [CallLogRepository].
 */
class CallLogRepositoryImpl @Inject constructor(
    private val dataSource: CallLogDataSource
) : CallLogRepository {

    override suspend fun getRecentCalls(limit: Int): Result<List<RecentCall>> {
        if (limit <= 0) return Result.success(emptyList())
        
        return try {
            val calls = dataSource.getRecentCalls(limit)
            Result.success(calls)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
