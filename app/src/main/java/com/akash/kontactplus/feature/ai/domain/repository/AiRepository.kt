package com.akash.kontactplus.feature.ai.domain.repository

import com.akash.kontactplus.feature.ai.domain.model.AiDraftContext
import com.akash.kontactplus.feature.ai.domain.model.AiGenerationResult
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    suspend fun generateText(context: AiDraftContext): AiGenerationResult
    
    fun isAiEnabled(): Flow<Boolean>
    suspend fun setAiEnabled(enabled: Boolean)
    
    fun hasAcceptedDisclosure(): Flow<Boolean>
    suspend fun setAcceptedDisclosure(accepted: Boolean)
}
