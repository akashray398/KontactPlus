package com.akash.kontactplus.feature.relationship.data.repository

import com.akash.kontactplus.feature.relationship.data.local.*
import com.akash.kontactplus.feature.relationship.domain.model.*
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionSuggestionAction
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionSuggestionState
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class RelationshipRepositoryImpl @Inject constructor(
    private val dao: RelationshipDao
) : RelationshipRepository {

    override fun observeRelationship(lookupKey: String): Flow<ContactRelationship?> {
        return combine(
            dao.observeRelationship(lookupKey),
            dao.observeTagsForContact(lookupKey),
            dao.observeDatesForContact(lookupKey),
            dao.observeRemindersForContact(lookupKey)
        ) { entity, tags, dates, reminders ->
            ContactRelationship(
                lookupKey = lookupKey,
                privateNote = entity?.privateNote ?: "",
                tags = tags.map { it.toDomain() },
                importantDates = dates.map { it.toDomain() },
                reminders = reminders.map { it.toDomain() },
                updatedAtEpochMillis = entity?.updatedAtEpochMillis ?: 0L
            )
        }
    }

    override fun observeAllTags(): Flow<List<RelationshipTag>> {
        return dao.observeAllTags().map { list -> list.map { it.toDomain() } }
    }

    override fun observeAllUpcomingDates(): Flow<List<ImportantDate>> {
        return dao.observeAllDates().map { list -> list.map { it.toDomain() } }
    }

    override fun observeScheduledReminders(): Flow<List<RelationshipReminder>> {
        return dao.observeScheduledReminders().map { list -> list.map { it.toDomain() } }
    }

    override fun observeOverdueReminders(): Flow<List<RelationshipReminder>> {
        return dao.observeOverdueReminders(System.currentTimeMillis()).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun savePrivateNote(lookupKey: String, note: String): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        val entity = ContactRelationshipEntity(
            lookupKey = lookupKey,
            privateNote = note.trim(),
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now
        )
        dao.upsertRelationship(entity)
    }

    override suspend fun createTag(name: String, colorKey: String): Result<Long> = runCatching {
        val entity = RelationshipTagEntity(
            name = name.trim(),
            normalizedName = name.trim().lowercase(),
            colorKey = colorKey,
            createdAtEpochMillis = System.currentTimeMillis()
        )
        dao.insertTag(entity)
    }

    override suspend fun assignTagToContact(lookupKey: String, tagId: Long): Result<Unit> = runCatching {
        dao.assignTagToContact(ContactTagCrossRef(lookupKey, tagId))
    }

    override suspend fun removeTagFromContact(lookupKey: String, tagId: Long): Result<Unit> = runCatching {
        dao.removeTagFromContact(lookupKey, tagId)
    }

    override suspend fun saveImportantDate(importantDate: ImportantDate): Result<Unit> = runCatching {
        dao.upsertImportantDate(importantDate.toEntity())
    }

    override suspend fun deleteImportantDate(id: Long): Result<Unit> = runCatching {
        dao.deleteImportantDate(id)
    }

    override suspend fun scheduleReminder(reminder: RelationshipReminder): Result<Unit> = runCatching {
        dao.upsertReminder(reminder.toEntity())
    }

    override suspend fun updateReminderWorkId(reminderId: String, workId: String?): Result<Unit> = runCatching {
        dao.updateWorkRequestId(reminderId, workId)
    }

    override suspend fun completeReminder(reminderId: String): Result<Unit> = runCatching {
        dao.markReminderCompleted(reminderId, System.currentTimeMillis())
    }

    override suspend fun cancelReminder(reminderId: String): Result<Unit> = runCatching {
        dao.markReminderCancelled(reminderId)
    }

    override fun observeFollowUpPreference(lookupKey: String): Flow<ContactFollowUpPreference?> {
        return dao.observeFollowUpPreference(lookupKey).map { it?.toDomain() }
    }

    override fun observeAllEnabledFollowUpPreferences(): Flow<List<ContactFollowUpPreference>> {
        return dao.observeEnabledFollowUpPreferences().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveFollowUpPreference(preference: ContactFollowUpPreference): Result<Unit> = runCatching {
        dao.upsertFollowUpPreference(preference.toEntity())
    }

    override fun observeSuggestionActionsForContact(lookupKey: String): Flow<List<ConnectionSuggestionAction>> {
        return dao.observeSuggestionActionsForContact(lookupKey).map { list -> list.map { it.toDomain() } }
    }

    override fun observeNonActiveSuggestionActions(): Flow<List<ConnectionSuggestionAction>> {
        return dao.observeNonActiveSuggestionActions().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveSuggestionAction(action: ConnectionSuggestionAction): Result<Unit> = runCatching {
        dao.upsertSuggestionAction(action.toEntity())
    }

    override suspend fun deleteSuggestionAction(key: String): Result<Unit> = runCatching {
        dao.deleteSuggestionAction(key)
    }

    override suspend fun clearAllSuggestionActions(): Result<Unit> = runCatching {
        dao.clearAllSuggestionActions()
    }

    override suspend fun deleteAllRelationshipData(): Result<Unit> = runCatching {
        dao.deleteAllData()
    }

    // Mappings
    private fun RelationshipTagEntity.toDomain() = RelationshipTag(id, name, colorKey)
    private fun ImportantDateEntity.toDomain() = ImportantDate(
        id, lookupKey, title, LocalDate.ofEpochDay(dateEpochDay), 
        ImportantDateType.valueOf(type), repeatsYearly
    )
    private fun RelationshipReminderEntity.toDomain() = RelationshipReminder(
        id, lookupKey, title, note, Instant.ofEpochMilli(scheduledAtEpochMillis), 
        ReminderStatus.valueOf(status)
    )
    private fun ContactFollowUpPreferenceEntity.toDomain() = ContactFollowUpPreference(
        lookupKey, FollowUpCadence.valueOf(cadenceType), customIntervalDays, enabled
    )
    private fun ConnectionSuggestionActionEntity.toDomain() = ConnectionSuggestionAction(
        suggestionKey, lookupKey, ruleType, sourceTimestampMillis, 
        ConnectionSuggestionState.valueOf(state), snoozedUntilEpochMillis?.let { Instant.ofEpochMilli(it) }, createdAtEpochMillis
    )

    private fun ImportantDate.toEntity() = ImportantDateEntity(
        id, lookupKey, title, localDate.toEpochDay(), type.name, repeatsYearly, 
        System.currentTimeMillis(), System.currentTimeMillis()
    )
    private fun RelationshipReminder.toEntity() = RelationshipReminderEntity(
        id, lookupKey, title, note, scheduledAt.toEpochMilli(), status.name, 
        null, System.currentTimeMillis(), System.currentTimeMillis(), null
    )
    private fun ContactFollowUpPreference.toEntity() = ContactFollowUpPreferenceEntity(
        lookupKey, cadence.name, customIntervalDays, enabled, 
        System.currentTimeMillis(), System.currentTimeMillis()
    )
    private fun ConnectionSuggestionAction.toEntity() = ConnectionSuggestionActionEntity(
        suggestionKey, lookupKey, ruleType, sourceTimestampMillis, 
        state.name, snoozedUntil?.toEpochMilli(), createdAtEpochMillis, System.currentTimeMillis()
    )
}
