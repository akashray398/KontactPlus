package com.akash.kontactplus.feature.relationship.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipDao {

    // Contact Relationship
    @Query("SELECT * FROM contact_relationships WHERE lookupKey = :lookupKey")
    fun observeRelationship(lookupKey: String): Flow<ContactRelationshipEntity?>

    @Upsert
    suspend fun upsertRelationship(relationship: ContactRelationshipEntity)

    @Query("UPDATE contact_relationships SET privateNote = :note, updatedAtEpochMillis = :updatedAt WHERE lookupKey = :lookupKey")
    suspend fun updatePrivateNote(lookupKey: String, note: String, updatedAt: Long)

    @Query("DELETE FROM contact_relationships WHERE lookupKey = :lookupKey")
    suspend fun deleteRelationship(lookupKey: String)

    // Tags
    @Query("SELECT * FROM relationship_tags ORDER BY name ASC")
    fun observeAllTags(): Flow<List<RelationshipTagEntity>>

    @Query("""
        SELECT tags.* FROM relationship_tags AS tags
        JOIN contact_tag_cross_ref AS ref ON tags.id = ref.tagId
        WHERE ref.lookupKey = :lookupKey
        ORDER BY tags.name ASC
    """)
    fun observeTagsForContact(lookupKey: String): Flow<List<RelationshipTagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: RelationshipTagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun assignTagToContact(crossRef: ContactTagCrossRef)

    @Query("DELETE FROM contact_tag_cross_ref WHERE lookupKey = :lookupKey AND tagId = :tagId")
    suspend fun removeTagFromContact(lookupKey: String, tagId: Long)

    @Query("DELETE FROM relationship_tags WHERE id = :tagId")
    suspend fun deleteTag(tagId: Long)

    // Important Dates
    @Query("SELECT * FROM important_dates WHERE lookupKey = :lookupKey ORDER BY dateEpochDay ASC")
    fun observeDatesForContact(lookupKey: String): Flow<List<ImportantDateEntity>>

    @Query("SELECT * FROM important_dates ORDER BY dateEpochDay ASC")
    fun observeAllDates(): Flow<List<ImportantDateEntity>>

    @Upsert
    suspend fun upsertImportantDate(date: ImportantDateEntity)

    @Query("DELETE FROM important_dates WHERE id = :id")
    suspend fun deleteImportantDate(id: Long)

    // Reminders
    @Query("SELECT * FROM relationship_reminders WHERE lookupKey = :lookupKey ORDER BY scheduledAtEpochMillis ASC")
    fun observeRemindersForContact(lookupKey: String): Flow<List<RelationshipReminderEntity>>

    @Query("SELECT * FROM relationship_reminders WHERE status = 'Scheduled' ORDER BY scheduledAtEpochMillis ASC")
    fun observeScheduledReminders(): Flow<List<RelationshipReminderEntity>>

    @Query("SELECT * FROM relationship_reminders WHERE status = 'Overdue' OR (status = 'Scheduled' AND scheduledAtEpochMillis < :now) ORDER BY scheduledAtEpochMillis ASC")
    fun observeOverdueReminders(now: Long): Flow<List<RelationshipReminderEntity>>

    @Query("SELECT * FROM relationship_reminders WHERE id = :id")
    suspend fun getReminderById(id: String): RelationshipReminderEntity?

    @Upsert
    suspend fun upsertReminder(reminder: RelationshipReminderEntity)

    @Query("UPDATE relationship_reminders SET workRequestId = :workRequestId WHERE id = :id")
    suspend fun updateWorkRequestId(id: String, workRequestId: String?)

    @Query("UPDATE relationship_reminders SET status = 'Completed', completedAtEpochMillis = :completedAt WHERE id = :id")
    suspend fun markReminderCompleted(id: String, completedAt: Long)

    @Query("UPDATE relationship_reminders SET status = 'Cancelled' WHERE id = :id")
    suspend fun markReminderCancelled(id: String)

    @Query("DELETE FROM relationship_reminders WHERE id = :id")
    suspend fun deleteReminder(id: String)
}
