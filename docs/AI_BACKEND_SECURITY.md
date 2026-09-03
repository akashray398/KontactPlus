# AI Backend Security Design

## Secret Handling
- AI Provider API keys (Gemini, OpenAI, etc.) are **never** stored in the Android application.
- The Android app calls a backend proxy (Kotlin Ktor Server).
- The backend reads the API key from environment variables (configured via `.env` in development or secure environment settings in production).

## Development Configuration
- Local development uses HTTP via `10.0.2.2` (Android emulator loopback).
- Cleartext traffic is enabled **only** in debug builds for the `10.0.2.2` domain.

## Production Configuration
- Production AI traffic **must** use HTTPS.
- The `AiApiService` uses a production base URL that is disabled/placeholder by default until a real backend is deployed.

## Authentication & Attestation
- Current implementation uses a direct proxy.
- Future hardening: Integrate **Play Integrity API** or **App Attestation** to ensure only genuine Kontact++ clients can access the AI proxy.
- Do not use static shared secrets between the app and backend.

## Rate Limiting
- The backend should implement rate limiting per client IP to prevent abuse.

## Data Retention
- The backend does **not** persist prompts or generated text.
- Body logging is disabled in both the client (OkHttp) and the server.
- Retention by the upstream AI provider depends on the specific provider's terms of service.
