package com.akash.kontactplus.feature.recents.data.datasource

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
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
        try {
            val resolver: ContentResolver = context.contentResolver
            val projection = arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.CACHED_LOOKUP_URI, // To resolve lookup key
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

            cursor?.use { mapCursorToRecentCalls(it) } ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun mapCursorToRecentCalls(cursor: Cursor): List<RecentCall> {
        val idIndex = cursor.getColumnIndex(CallLog.Calls._ID)
        val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
        val nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
        val lookupIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_LOOKUP_URI)
        val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
        val dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
        val newIndex = cursor.getColumnIndex(CallLog.Calls.NEW)

        if (idIndex == -1 || numberIndex == -1 || typeIndex == -1 || dateIndex == -1) return emptyList()

        val calls = mutableListOf<RecentCall>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idIndex)
            val number = cursor.getString(numberIndex) ?: ""
            val cachedName = if (nameIndex != -1) cursor.getString(nameIndex) else null
            val lookupUri = if (lookupIndex != -1) cursor.getString(lookupIndex) else null
            val typeInt = cursor.getInt(typeIndex)
            val date = cursor.getLong(dateIndex)
            val duration = if (durationIndex != -1) cursor.getLong(durationIndex) else 0L
            val isNew = if (newIndex != -1) cursor.getInt(newIndex) == 1 else false

            // Extract lookup key from URI if present
            val lookupKey = lookupUri?.substringAfterLast("/")

            calls.add(
                RecentCall(
                    id = id,
                    phoneNumber = number,
                    cachedName = cachedName,
                    contactLookupKey = lookupKey,
                    type = mapCallType(typeInt),
                    timestampMillis = date,
                    durationSeconds = duration,
                    isNew = isNew
                )
            )
        }
        return calls
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
