# Dependency Review for Kontact++

## Android Application Dependencies

| Dependency | Purpose | handles Sensitive Data? | Necessary? |
| :--- | :--- | :--- | :--- |
| **Room** | Local data storage | Yes (Notes, Reminders) | Yes |
| **DataStore** | Preferences | Yes (Privacy states) | Yes |
| **Retrofit / OkHttp** | AI Backend Proxy | Yes (AI Payloads) | Yes |
| **Hilt** | Dependency Injection | No | Yes |
| **WorkManager** | Reminder scheduling | No | Yes |
| **Navigation Compose** | UI Routing | No | Yes |
| **Kotlin Serialization**| JSON processing | Yes | Yes |

## Kotlin Ktor Backend Dependencies

| Dependency | Purpose | Handles Sensitive Data? | Necessary? |
| :--- | :--- | :--- | :--- |
| **Ktor Server** | Web framework | Yes (Requests) | Yes |
| **Ktor Client** | AI Provider communication| Yes (Payloads) | Yes |
| **Dotenv-Kotlin** | Secret management | Yes (API Keys) | Yes |
| **Logback** | Logging | No (Bodies disabled) | Yes |

## Supply Chain Notes
- **No Tracking SDKs**: No analytics (Firebase, AppsFlyer, etc.) are present.
- **No Advertising**: No ad SDKs are included.
- **Minimal Transitives**: Preferred official AndroidX and squareup libraries to minimize risk.
- **ProGuard/R8**: Ready for release minification (no custom code downloading).
