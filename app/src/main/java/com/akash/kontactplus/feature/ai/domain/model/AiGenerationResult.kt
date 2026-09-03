package com.akash.kontactplus.feature.ai.domain.model

sealed interface AiGenerationResult {
    data class Success(
        val text: String,
        val modelLabel: String? = null
    ) : AiGenerationResult
    
    data object LocalFallback : AiGenerationResult
    data object Offline : AiGenerationResult
    data object RateLimited : AiGenerationResult
    data object Unavailable : AiGenerationResult
    data class Failed(val errorRes: Int) : AiGenerationResult
}
