# Google Play Data Safety Draft

This draft represents the expected Data Safety declaration for Kontact++ based on the Step 18 implementation.

## 1. Data Collection and Security
| Question | Answer |
| :--- | :--- |
| Does your app collect or share any of the required user data types? | **Yes** (Only for optional AI features) |
| Is all of the user data collected by your app encrypted in transit? | **Yes** (All remote traffic uses HTTPS) |
| Do you provide a way for users to request that their data be deleted? | **Yes** (In-app Privacy Center for local data) |

## 2. Data Types Collected
*Note: These only apply if the user enables and uses the AI Writing Assistance.*

### Personal Info
- **Name**: Collected (Optional). Only if the user explicitly includes the contact's first name in an AI request.
- **Other Info**: Collected (Optional). Relationship context provided by the user for AI.

### App Activity
- **App Interactions**: Not Collected (No analytics).

### App Info and Performance
- **Diagnostics**: Not Collected (No crash reporting SDKs).

### Other
- **User-provided content**: Collected. The specific instruction or reference text sent for AI generation.

## 3. Data Usage & Sharing

### AI Writing Assistance (Optional feature)
- **Data Type**: User-provided text, Contact Alias, Selection.
- **Usage**: App functionality (AI generation).
- **Sharing**: This data is shared with the configured AI service provider ([AI PROVIDER NAME]) via our backend proxy.
- **Processed Ephemerally**: Yes (The backend does not store requests).

## 4. Data Types NOT Collected
- **Location**: No.
- **Financial Info**: No.
- **Health/Fitness**: No.
- **Messages/SMS**: No.
- **Photos/Videos**: No.
- **Audio Files**: No.
- **Files/Docs**: No.
- **Calendar**: No.
- **Contacts**: **NO** (Google Play defines "Collected" as transmitted off-device. Contacts are accessed locally only).
- **Device Identifiers**: No.

## 5. Privacy Links
- **Privacy Policy**: [PRIVACY POLICY URL]

---
**REQUIRES OWNER/PROVIDER CONFIRMATION**:
- Confirm exact AI Provider retention policies.
- Confirm backend hosting environment logs.
- Confirm whether generated requestId is considered an identifier by the host.
