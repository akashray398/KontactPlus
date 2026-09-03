package com.akash.kontactplus.feature.ai.data.remote

import com.akash.kontactplus.feature.ai.data.remote.model.AiRequestDto
import com.akash.kontactplus.feature.ai.data.remote.model.AiResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApiService {
    @POST("api/v1/ai/generate")
    suspend fun generateText(@Body request: AiRequestDto): Response<AiResponseDto>
}
