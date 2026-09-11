package com.akash.kontactplus.feature.ai.data.remote

import com.akash.kontactplus.R
import com.akash.kontactplus.feature.ai.data.remote.model.AiRequestDto
import com.akash.kontactplus.feature.ai.domain.model.AiGenerationResult
import java.io.IOException
import java.net.SocketTimeoutException
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
                } ?: AiGenerationResult.Failed(R.string.ai_failed)
            } else {
                when (response.code()) {
                    401 -> AiGenerationResult.Unauthorized
                    403 -> AiGenerationResult.Unavailable
                    429 -> AiGenerationResult.RateLimited
                    503 -> AiGenerationResult.Unavailable
                    else -> AiGenerationResult.Failed(R.string.ai_failed)
                }
            }
        } catch (e: SocketTimeoutException) {
            AiGenerationResult.Timeout
        } catch (e: IOException) {
            AiGenerationResult.Offline
        } catch (e: Exception) {
            AiGenerationResult.Failed(R.string.ai_failed)
        }
    }
}
