package com.akash.kontactplus.feature.ai.domain.usecase

import com.akash.kontactplus.feature.ai.domain.model.AiDraftContext
import com.akash.kontactplus.feature.ai.domain.model.AiGenerationResult
import com.akash.kontactplus.feature.ai.domain.repository.AiRepository
import javax.inject.Inject

class GenerateAiTextUseCase @Inject constructor(
    private val repository: AiRepository,
    private val prepareRequest: PrepareAiRequestUseCase
) {
    suspend operator fun invoke(context: AiDraftContext): AiGenerationResult {
        val preparedContext = prepareRequest(context)
        return repository.generateText(preparedContext)
    }
}
