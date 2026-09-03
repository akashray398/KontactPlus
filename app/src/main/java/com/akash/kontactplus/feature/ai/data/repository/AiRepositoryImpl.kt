package com.akash.kontactplus.feature.ai.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.akash.kontactplus.feature.ai.data.remote.AiRemoteDataSource
import com.akash.kontactplus.feature.ai.data.remote.model.AiRequestDto
import com.akash.kontactplus.feature.ai.domain.model.AiDraftContext
import com.akash.kontactplus.feature.ai.domain.model.AiGenerationResult
import com.akash.kontactplus.feature.ai.domain.repository.AiRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "ai_prefs")

class AiRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteDataSource: AiRemoteDataSource
) : AiRepository {

    private val KEY_AI_ENABLED = booleanPreferencesKey("ai_enabled")
    private val KEY_DISCLOSURE_ACCEPTED = booleanPreferencesKey("disclosure_accepted")

    override suspend fun generateText(draftContext: AiDraftContext): AiGenerationResult {
        val request = AiRequestDto(
            action = draftContext.actionType.name,
            tone = draftContext.tone.name,
            instruction = draftContext.userInstruction,
            selectedText = draftContext.selectedText,
            contactAlias = draftContext.contactAlias,
            context = draftContext.relationshipContext
        )
        return remoteDataSource.generate(request)
    }

    override fun isAiEnabled(): Flow<Boolean> = context.dataStore.data.map { it[KEY_AI_ENABLED] ?: false }

    override suspend fun setAiEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AI_ENABLED] = enabled }
    }

    override fun hasAcceptedDisclosure(): Flow<Boolean> = context.dataStore.data.map { it[KEY_DISCLOSURE_ACCEPTED] ?: false }

    override suspend fun setAcceptedDisclosure(accepted: Boolean) {
        context.dataStore.edit { it[KEY_DISCLOSURE_ACCEPTED] = accepted }
    }
}
