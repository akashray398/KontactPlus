# AI Privacy Disclosure

## Overview
Kontact++ provides optional AI writing assistance for follow-ups, greetings, and summarization.

## Data Minimization
- **No Automatic Uploads**: No data is sent to the cloud in the background.
- **Explicit Consent**: Every AI request requires the user to choice an action, review the payload, and tap "Generate".
- **Redaction**: Phone numbers and email addresses are automatically redacted from the payload before transmission.
- **Redaction Default**: Contact names, private notes, and tags are excluded from the AI payload unless the user explicitly toggles them ON for a specific request.

## Data Categories
- **Instruction**: The specific task for the AI (e.g., "Draft a follow-up").
- **Tone**: The selected style (e.g., "Professional").
- **Optional Context**: If enabled by the user, the contact's first name or an excerpt of a private note.

## Excluded Data
- Phone numbers
- Call logs / History
- Complete contact list
- Exact location
- Device identifiers

## Accuracy
AI output can be inaccurate. Users are required to review and edit any generated draft before sending it to others.
