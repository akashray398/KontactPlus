package com.akash.kontactplus.feature.relationship.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionInsightsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "insight_prefs")

class ConnectionInsightsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectionInsightsRepository {

    private val KEY_INSIGHTS_ENABLED = booleanPreferencesKey("insights_enabled")
    private val KEY_CALL_LOG_INSIGHTS_ENABLED = booleanPreferencesKey("call_log_insights_enabled")
    private val KEY_CALL_LOG_DISCLOSURE_ACCEPTED = booleanPreferencesKey("call_log_disclosure_accepted")
    private val KEY_SHOW_MISSED_CALLS = booleanPreferencesKey("show_missed_calls")
    private val KEY_SHOW_IMPORTANT_DATES = booleanPreferencesKey("show_important_dates")
    private val KEY_SHOW_REMINDERS = booleanPreferencesKey("show_reminders")
    private val KEY_SNOOZE_DAYS = intPreferencesKey("snooze_days")

    override fun areConnectionInsightsEnabled(): Flow<Boolean> = context.dataStore.data.map { it[KEY_INSIGHTS_ENABLED] ?: true }
    override suspend fun setConnectionInsightsEnabled(enabled: Boolean) { context.dataStore.edit { it[KEY_INSIGHTS_ENABLED] = enabled } }

    override fun areCallHistoryInsightsEnabled(): Flow<Boolean> = context.dataStore.data.map { it[KEY_CALL_LOG_INSIGHTS_ENABLED] ?: false }
    override suspend fun setCallHistoryInsightsEnabled(enabled: Boolean) { context.dataStore.edit { it[KEY_CALL_LOG_INSIGHTS_ENABLED] = enabled } }

    override fun hasAcceptedCallHistoryDisclosure(): Flow<Boolean> = context.dataStore.data.map { it[KEY_CALL_LOG_DISCLOSURE_ACCEPTED] ?: false }
    override suspend fun setCallHistoryDisclosureAccepted(accepted: Boolean) { context.dataStore.edit { it[KEY_CALL_LOG_DISCLOSURE_ACCEPTED] = accepted } }

    override fun shouldShowMissedCallSuggestions(): Flow<Boolean> = context.dataStore.data.map { it[KEY_SHOW_MISSED_CALLS] ?: false }
    override suspend fun setShowMissedCallSuggestions(show: Boolean) { context.dataStore.edit { it[KEY_SHOW_MISSED_CALLS] = show } }

    override fun shouldShowImportantDateSuggestions(): Flow<Boolean> = context.dataStore.data.map { it[KEY_SHOW_IMPORTANT_DATES] ?: true }
    override suspend fun setShowImportantDateSuggestions(show: Boolean) { context.dataStore.edit { it[KEY_SHOW_IMPORTANT_DATES] = show } }

    override fun shouldShowReminderSuggestions(): Flow<Boolean> = context.dataStore.data.map { it[KEY_SHOW_REMINDERS] ?: true }
    override suspend fun setShowReminderSuggestions(show: Boolean) { context.dataStore.edit { it[KEY_SHOW_REMINDERS] = show } }

    override fun getDefaultSnoozeDays(): Flow<Int> = context.dataStore.data.map { it[KEY_SNOOZE_DAYS] ?: 1 }
    override suspend fun setDefaultSnoozeDays(days: Int) { context.dataStore.edit { it[KEY_SNOOZE_DAYS] = days } }
}
