package com.akash.kontactplus.feature.relationship.domain.repository

import com.akash.kontactplus.feature.relationship.domain.model.*
import kotlinx.coroutines.flow.Flow

interface RelationshipRepository {
    fun observeRelationship(lookupKey: String): Flow<ContactRelationship?>
    fun observeAllTags(): Flow<List<RelationshipTag>>
    fun observeAllUpcomingDates(): Flow<List<ImportantDate>>
    fun observeScheduledReminders(): Flow<List<RelationshipReminder>>
    fun observeOverdueReminders(): Flow<List<RelationshipReminder>>

    suspend fun savePrivateNote(lookupKey: String, note: String): Result<Unit>
    suspend fun createTag(name: String, colorKey: String): Result<Long>
    suspend fun assignTagToContact(lookupKey: String, tagId: Long): Result<Unit>
    suspend fun removeTagFromContact(lookupKey: String, tagId: Long): Result<Unit>
    suspend fun saveImportantDate(importantDate: ImportantDate): Result<Unit>
    suspend fun deleteImportantDate(id: Long): Result<Unit>
    suspend fun scheduleReminder(reminder: RelationshipReminder): Result<Unit>
    suspend fun updateReminderWorkId(reminderId: String, workId: String?): Result<Unit>
    suspend fun completeReminder(reminderId: String): Result<Unit>
    suspend fun cancelReminder(reminderId: String): Result<Unit>
}
