package com.akash.kontactplus.ai

import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.util.*

fun main() {
    embeddedServer(Netty, port = 5000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Basic CORS for development. Production should be restricted to known origins.
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // DEVELOPMENT ONLY - restrict for production
    }

    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val httpClient = HttpClient(CIO) {
        install(ClientContentNegotiation) {
            json()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 10000
        }
    }

    environment.monitor.subscribe(ApplicationStopped) {
        httpClient.close()
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "Healthy", "framework" to "Ktor"))
        }

        post("/api/v1/ai/generate") {
            val requestId = UUID.randomUUID().toString()
            
            val request = try {
                call.receive<AiRequestDto>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body.")
                return@post
            }
            
            // Validation
            if (request.action.isBlank() || request.instruction.isBlank() || request.tone.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Action, tone, and instruction are required.")
                return@post
            }

            if (request.instruction.length > 1000) {
                call.respond(HttpStatusCode.BadRequest, "Instruction too long.")
                return@post
            }

            val apiKey = System.getenv("AI_PROVIDER_API_KEY") ?: dotenv["AI_PROVIDER_API_KEY"]
            val baseUrl = System.getenv("AI_PROVIDER_BASE_URL") ?: dotenv["AI_PROVIDER_BASE_URL"]
            val model = System.getenv("AI_PROVIDER_MODEL") ?: dotenv["AI_PROVIDER_MODEL"] ?: "gpt-3.5-turbo"

            if (apiKey.isNullOrEmpty() || baseUrl.isNullOrEmpty()) {
                call.respond(HttpStatusCode.ServiceUnavailable, "AI backend not fully configured.")
                return@post
            }

            try {
                val systemPrompt = constructSystemPrompt(request)
                
                val aiResponse = httpClient.post("${baseUrl.trimEnd('/')}/chat/completions") {
                    header(HttpHeaders.Authorization, "Bearer $apiKey")
                    header("X-Request-ID", requestId)
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("model", model)
                        put("messages", buildJsonArray {
                            addJsonObject {
                                put("role", "system")
                                put("content", systemPrompt)
                            }
                            addJsonObject {
                                put("role", "user")
                                put("content", request.instruction)
                            }
                        })
                        put("max_tokens", 1000)
                    })
                }

                if (aiResponse.status.isSuccess()) {
                    val body = Json.parseToJsonElement(aiResponse.bodyAsText()).jsonObject
                    val text = body["choices"]?.jsonArray?.get(0)?.jsonObject?.get("message")?.jsonObject?.get("content")?.jsonPrimitive?.content ?: ""
                    
                    call.respond(AiResponseDto(
                        requestId = requestId,
                        text = text,
                        modelLabel = model
                    ))
                } else if (aiResponse.status == HttpStatusCode.TooManyRequests) {
                    call.respond(HttpStatusCode.TooManyRequests, "Provider rate limit reached.")
                } else {
                    call.respond(HttpStatusCode.BadGateway, "AI Provider failure.")
                }
            } catch (e: Exception) {
                // Do not leak exception details to client
                call.respond(HttpStatusCode.InternalServerError, "Generation failed internally.")
            }
        }
    }
}

private fun constructSystemPrompt(request: AiRequestDto): String {
    return """
        You are a helpful and privacy-conscious relationship assistant called Kontact++.
        Action: ${request.action}
        Tone: ${request.tone}
        Contact Alias: ${request.contactAlias}
        Relationship Context: ${request.context ?: "None"}
        Reference Text: ${request.selectedText}
        
        Draft a short communication based on the instructions. Never invent personal facts. Be concise.
    """.trimIndent()
}

@Serializable
data class AiRequestDto(
    val action: String,
    val tone: String,
    val instruction: String,
    val selectedText: String = "",
    val contactAlias: String = "Contact",
    val context: String? = null,
    val locale: String = "en-IN"
)

@Serializable
data class AiResponseDto(
    val requestId: String,
    val text: String,
    val modelLabel: String? = null,
    val finishReason: String? = null
)
