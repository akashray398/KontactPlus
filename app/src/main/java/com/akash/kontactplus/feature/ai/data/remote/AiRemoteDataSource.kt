package com.akash.kontactplus.feature.ai.data.remote

import com.akash.kontactplus.feature.ai.data.remote.model.AiRequestDto
import com.akash.kontactplus.feature.ai.domain.model.AiGenerationResult
import javax.inject.Inject

interface AiRemoteDataSource {
    suspend fun generate(request: AiRequestDto): AiGenerationResult
}

class RetrofitAiRemoteDataSource @Inject constructor(
    private val apiService: AiApiService
) : AiRemoteDataSource {

    override suspend fun generate(request: AiRequestDto): AiGenerationResult {
        return try {
            val response = apiService.generateText(request)
            if (response.isSuccessful) {
                response.body()?.let { 
                    AiGenerationResult.Success(it.text, it.modelLabel)
                } ?: AiGenerationResult.Failed(com.akash.kontactplus.R.string.recents_error_description)
            } else {
                when (response.code()) {
                    401 -> AiGenerationResult.Unauthorized
                    429 -> AiGenerationResult.RateLimited
                    503 -> AiGenerationResult.Unavailable
                    504 -> AiGenerationResult.Timeout
                    else -> AiGenerationResult.Failed(com.akash.kontactplus.R.string.recents_error_description)
                }
            }
        } catch (e: java.net.SocketTimeoutException) {
            AiGenerationResult.Timeout
        } catch (e: java.io.IOException) {
            AiGenerationResult.Offline
        } catch (e: Exception) {
            AiGenerationResult.Failed(com.akash.kontactplus.R.string.recents_error_description)
        }
    }
}
