# Security Review for Kontact++

## 1. Manifest Security Audit

| Component | Exported | Protection | Risk | Decision |
| :--- | :--- | :--- | :--- | :--- |
| **MainActivity** | True | None (Launcher) | Public entry point | Required for launch and DIAL intents. |
| **KontactInCallService**| True | BIND_INCALL_SERVICE| System hijacking | Mandatory for system dialer role. Protected by framework-only permission. |
| **ActiveCallActivity** | False | N/A | Unauthorized call UI | Internal only. |
| **CallActionReceiver** | False | N/A | Spoofed call actions | Internal only. |
| **ReminderActionReceiver**| False | N/A | Spoofed reminders | Internal only. |

## 2. Network Security
- **Release Configuration**: Strict HTTPS enforcement. Cleartext is disabled for all production domains.
- **Debug Configuration**: Limited cleartext exception for `10.0.2.2` (localhost) to support emulator development.
- **Secrets**: No AI provider API keys are stored in the APK. All keys are managed on the server.

## 3. Data Storage
- **Privacy**: Sensitive relationship data (notes, tags) is stored in a private Room database.
- **Backup**: Database files are explicitly excluded from cloud backup and device transfer in `data_extraction_rules.xml`.
- **Encryption**: Currently stored as plain text in the app's private directory. Residual risk: Rooted devices or forensic physical access. Recommended: SQLCipher for future hardening.

## 4. Permission Safeguards
- **Dialer Role**: The app holds the `ROLE_DIALER` role before accessing call logs.
- **Minimal Access**: The app requests only the permissions necessary for its active feature set.

## 5. Privacy Safeguards
- **Redaction**: AI payloads are automatically redacted for PII (Phone numbers, Emails) before transmission.
- **Opt-in Only**: Remote AI features are disabled by default and require a signed disclosure acceptance.
- **Payload Preview**: Users must approve the exact text leaving the device for every AI request.
