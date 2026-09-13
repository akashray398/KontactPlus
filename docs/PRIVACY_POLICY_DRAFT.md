# Privacy Policy for Kontact++ (DRAFT)

**Effective Date:** [EFFECTIVE DATE]
**Status:** DRAFT - Requires Owner and Legal Review

Kontact++ is committed to providing a privacy-first experience for managing your contacts and relationships. This policy explains what data we access, how we use it, and your controls.

## 1. Local-First Design
Kontact++ operates primarily on your device. Most features work entirely offline and do not transmit your personal data to any server.

### 1.1 Device Contacts
We use the **READ_CONTACTS** permission to display and search your existing contacts. This information is processed locally and is not uploaded to our servers or any third-party provider.

### 1.2 Call History & Dialer
We use the **READ_CALL_LOG** and **CALL_PHONE** permissions to provide a recents list and direct calling functionality. This data is accessed only when Kontact++ is your active default Phone app. Your call history is processed on-device for connection insights and is never transmitted off-device.

### 1.3 Relationship Information
Any notes, tags, or reminders you create inside Kontact++ are stored in a local database on your device. This information is excluded from system cloud backups by default to ensure maximum privacy.

## 2. Optional AI Writing Assistance
Kontact++ provides optional AI tools to help you draft messages or summarize notes. This feature requires an internet connection and transmits limited data to our backend proxy.

### 2.1 Explicit Consent
No data is sent for AI processing without your explicit request. You must select an AI action, review the exact text in a "Payload Preview," and tap "Generate" for each request.

### 2.2 Data Transmitted
Only the following user-reviewed fields are sent to our Kotlin/Ktor backend proxy:
- Your specific instruction (e.g., "Draft a follow-up").
- A selected tone (e.g., "Professional").
- A contact alias you provide (e.g., "Colleague").
- Any specific text or relationship context you explicitly choose to include.

### 2.3 Third-Party Processing
Our backend proxy forwards your request to [AI PROVIDER NAME] (e.g., OpenAI or Groq). We do not send your phone number, complete contact list, or call history to the AI provider.

### 2.4 Redaction
We automatically attempt to redact phone numbers and email addresses from AI requests before they leave your device.

## 3. Data Retention & Deletion
- **On-Device**: You can delete all relationship data (notes, tags, reminders) at any time through the "Privacy Center" in Settings.
- **Backend**: Our backend proxy does not persist AI requests or generated responses. Data is held transiently in memory for the duration of the request.
- **Upstream**: Data retention by the AI provider is subject to their own privacy policies. [AI PROVIDER NAME] typically retains data for [DATA RETENTION PERIOD] for abuse monitoring.

## 4. Permissions
- **READ_CONTACTS**: Local display of contacts.
- **READ_CALL_LOG**: Local display of recents and on-device insights.
- **CALL_PHONE**: User-initiated outgoing calls.
- **POST_NOTIFICATIONS**: Reminders and incoming call alerts.
- **USE_FULL_SCREEN_INTENT**: Displaying incoming calls over the lock screen.
- **INTERNET**: Required for the optional AI tools and health checks.

## 5. Security
We use HTTPS for all communications between the app and our backend. Production AI access is restricted to genuine Kontact++ clients.

## 6. Children's Privacy
Kontact++ is not designed for or targeted at children under 13. We do not knowingly collect personal data from children.

## 7. Contact Us
If you have questions about this policy, please contact [CONTACT EMAIL].

---
*Note: This policy is a draft provided as part of technical documentation. It must be hosted at a public URL (e.g., [PRIVACY POLICY URL]) and reviewed by legal counsel before the app is published.*
