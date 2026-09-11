# Kontact++ AI Server

This is a Kotlin Ktor server acting as a privacy-focused proxy between the Kontact++ Android application and AI providers (like OpenAI or Groq).

## Development Status
This server is intended for **local development and testing only**. 
Production deployment requires a robust client authentication strategy (e.g., Play Integrity / App Attestation) to prevent unauthorized access.

## Features
- Proxies generation requests to OpenAI-compatible endpoints.
- Injects API keys safely from server-side environment variables.
- Hides system prompts from the client.
- Performs basic request validation.
- No logging of private request/response bodies.

## Prerequisites
- JDK 11 or higher.
- A valid AI provider API key (e.g., OpenAI).

## Configuration
Create a `.env` file in the root of the server directory with the following variables:

```env
AI_PROVIDER_BASE_URL=https://api.openai.com/v1
AI_PROVIDER_API_KEY=your_api_key_here
AI_PROVIDER_MODEL=gpt-3.5-turbo
```

## Running the Server
Use the Gradle wrapper to start the server:

```bash
./gradlew run
```

The server will start on `http://0.0.0.0:5000`.

## Android Emulator Access
The Android emulator can reach this local server at `http://10.0.2.2:5000`.

## Privacy
- This server does **not** persist any requests or generated text.
- Sensitive data categories (phone numbers, full contact lists) are redacted or excluded by the Android client before reaching this server.
