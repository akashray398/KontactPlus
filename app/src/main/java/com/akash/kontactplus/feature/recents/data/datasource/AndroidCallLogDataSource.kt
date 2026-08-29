package com.akash.kontactplus.feature.recents.data.datasource

import android.content.ContentResolver
import android.content.Context
import android.provider.CallLog
import com.akash.kontactplus.feature.recents.domain.model.RecentCall
import com.akash.kontactplus.feature.recents.domain.model.RecentCallType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Android-specific implementation of [CallLogDataSource] using [ContentResolver].
 */
class AndroidCallLogDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : CallLogDataSource {

    override suspend fun getRecentCalls(limit: Int): List<RecentCall> = withContext(Dispatchers.IO) {
        val resolver: ContentResolver = context.contentResolver
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.NEW
        )

        val cursor = resolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            "${CallLog.Calls.DATE} DESC LIMIT $limit"
        )

        cursor?.use { c ->
            val idIndex = c.getColumnIndex(CallLog.Calls._ID)
            val numberIndex = c.getColumnIndex(CallLog.Calls.NUMBER)
            val nameIndex = c.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val typeIndex = c.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = c.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = c.getColumnIndex(CallLog.Calls.DURATION)
            val newIndex = c.getColumnIndex(CallLog.Calls.NEW)

            val calls = mutableListOf<RecentCall>()

            while (c.moveToNext()) {
                val id = c.getLong(idIndex)
                val number = c.getString(numberIndex) ?: ""
                val cachedName = c.getString(nameIndex)
                val typeInt = c.getInt(typeIndex)
                val date = c.getLong(dateIndex)
                val duration = c.getLong(durationIndex)
                val isNew = c.getInt(newIndex) == 1

                calls.add(
                    RecentCall(
                        id = id,
                        phoneNumber = number,
                        cachedName = cachedName,
                        type = mapCallType(typeInt),
                        timestampMillis = date,
                        durationSeconds = duration,
                        isNew = isNew
                    )
                )
            }
            calls
        } ?: emptyList()
    }

    private fun mapCallType(type: Int): RecentCallType {
        return when (type) {
            CallLog.Calls.INCOMING_TYPE -> RecentCallType.Incoming
            CallLog.Calls.OUTGOING_TYPE -> RecentCallType.Outgoing
            CallLog.Calls.MISSED_TYPE -> RecentCallType.Missed
            CallLog.Calls.REJECTED_TYPE -> RecentCallType.Rejected
            CallLog.Calls.BLOCKED_TYPE -> RecentCallType.Blocked
            CallLog.Calls.VOICEMAIL_TYPE -> RecentCallType.Voicemail
            else -> RecentCallType.Unknown
        }
    }
}
