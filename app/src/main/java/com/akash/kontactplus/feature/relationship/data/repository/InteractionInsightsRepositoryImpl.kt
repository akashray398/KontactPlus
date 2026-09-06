package com.akash.kontactplus.feature.relationship.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.akash.kontactplus.core.telecom.DialerRoleState
import com.akash.kontactplus.core.telecom.TelecomRoleManager
import com.akash.kontactplus.feature.recents.domain.model.RecentCallType
import com.akash.kontactplus.feature.recents.domain.repository.CallLogRepository
import com.akash.kontactplus.feature.relationship.domain.model.ContactInteractionSummary
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionInsightsRepository
import com.akash.kontactplus.feature.relationship.domain.repository.InteractionInsightsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.time.Instant
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class InteractionInsightsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val callLogRepository: CallLogRepository,
    private val telecomRoleManager: TelecomRoleManager,
    private val connectionInsightsRepository: ConnectionInsightsRepository
) : InteractionInsightsRepository {

    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    override fun observeInteractionSummaries(): Flow<Map<String, ContactInteractionSummary>> {
        return combine(
            connectionInsightsRepository.areCallHistoryInsightsEnabled(),
            connectionInsightsRepository.hasAcceptedCallHistoryDisclosure(),
            refreshSignal
        ) { enabled, accepted, _ ->
            enabled && accepted && hasPermissions()
        }.flatMapLatest { canAnalyze ->
            if (!canAnalyze) {
                flowOf(emptyMap())
            } else {
                flow {
                    val result = callLogRepository.getRecentCalls(1000)
                    val summaries = result.getOrNull()?.let { calls ->
                        val now = Instant.now()
                        val thirtyDaysAgo = now.minus(java.time.Duration.ofDays(30))
                        
                        calls.filter { it.contactLookupKey != null }
                            .groupBy { it.contactLookupKey!! }
                            .mapValues { (lookupKey, contactCalls) ->
                                val sortedCalls = contactCalls.sortedByDescending { it.timestampMillis }
                                val lastInteraction = Instant.ofEpochMilli(sortedCalls.first().timestampMillis)
                                
                                val lastIncoming = sortedCalls.find { it.type == RecentCallType.Incoming }?.timestampMillis?.let { Instant.ofEpochMilli(it) }
                                val lastOutgoing = sortedCalls.find { it.type == RecentCallType.Outgoing }?.timestampMillis?.let { Instant.ofEpochMilli(it) }
                                val lastMissed = sortedCalls.find { it.type == RecentCallType.Missed }?.timestampMillis?.let { Instant.ofEpochMilli(it) }
                                
                                val recentCalls = contactCalls.filter { it.timestampMillis >= thirtyDaysAgo.toEpochMilli() }
                                
                                ContactInteractionSummary(
                                    lookupKey = lookupKey,
                                    lastInteractionAt = lastInteraction,
                                    lastIncomingCallAt = lastIncoming,
                                    lastOutgoingCallAt = lastOutgoing,
                                    lastMissedCallAt = lastMissed,
                                    callCountLast30Days = recentCalls.size,
                                    incomingCountLast30Days = recentCalls.count { it.type == RecentCallType.Incoming },
                                    outgoingCountLast30Days = recentCalls.count { it.type == RecentCallType.Outgoing },
                                    missedCountLast30Days = recentCalls.count { it.type == RecentCallType.Missed },
                                    totalDurationLast30DaysMillis = recentCalls.sumOf { it.durationSeconds * 1000 }
                                )
                            }
                    } ?: emptyMap()
                    emit(summaries)
                }
            }
        }
    }

    override suspend fun refresh() {
        refreshSignal.emit(Unit)
    }

    private fun hasPermissions(): Boolean {
        val hasLogPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        val isDefaultDialer = telecomRoleManager.getDialerRoleState() == DialerRoleState.Held
        return hasLogPermission && isDefaultDialer
    }
}
