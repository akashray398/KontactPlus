# Kontact++ Data Inventory

This document details the data categories used by the Kontact++ Android application and its Kotlin/Ktor backend proxy.

## Device-Local Data (Not Transmitted by Default)

| Data Category | Source | Stored on Device? | Sent Off-Device? | Retention | User Control |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Contact Names** | System Contacts | No (Read-only) | No | Transient | System Settings (Permission) |
| **Phone Numbers** | System Contacts | No (Read-only) | No | Transient | System Settings (Permission) |
| **Contact Photos** | System Contacts | No (Read-only) | No | Transient | System Settings (Permission) |
| **Call Log Entries** | System Call Log | No (Read-only) | No | Transient | System Settings (Permission) |
| **Call Timestamps** | System Call Log | No (Read-only) | No | Transient | System Settings (Permission) |
| **Call Duration** | System Call Log | No (Read-only) | No | Transient | System Settings (Permission) |
| **Private Notes** | User Entry | Yes (Room DB) | **Only if Opted-in for AI** | Indefinite | Relationship Editor / Privacy Center |
| **Relationship Tags** | User Entry | Yes (Room DB) | **Only if Opted-in for AI** | Indefinite | Relationship Editor / Privacy Center |
| **Important Dates** | User Entry | Yes (Room DB) | **Only if Opted-in for AI** | Indefinite | Relationship Editor / Privacy Center |
| **Follow-up Prefs** | User Entry | Yes (Room DB) | No | Indefinite | Relationship Editor / Privacy Center |
| **Connection Insights**| On-device Engine | No | No | Transient | Privacy Center (Settings) |
| **Snooze/Dismiss** | User Action | Yes (Room DB) | No | Indefinite | Privacy Center (Reset) |

## Transmitted Data (User-Initiated AI Writing Assistance)

The following data is transmitted **only** to the developer-controlled Kotlin/Ktor backend proxy when the user explicitly choose an AI action and confirms the payload preview.

| Data Field | Purpose | Shared With Provider? | Retention (Backend) |
| :--- | :--- | :--- | :--- |
| **AI Instruction** | To guide generation | Yes (AI Provider) | Transient (In-memory) |
| **AI Tone** | To style the output | Yes (AI Provider) | Transient (In-memory) |
| **Contact Alias** | To personalize (e.g. "Friend")| Yes (AI Provider) | Transient (In-memory) |
| **Reference Text** | Text selected by user | Yes (AI Provider) | Transient (In-memory) |
| **AI Context** | Relationship snippet | Yes (AI Provider) | Transient (In-memory) |

## Infrastructure & Configuration Data

| Data Field | Storage | Shared? | Purpose |
| :--- | :--- | :--- | :--- |
| **AI Model Label** | App/Backend Config | No | Identifies the AI model version used. |
| **Permission State** | DataStore / System | No | Tracks user consent for feature visibility. |
| **Disclosure Status**| DataStore | No | Tracks first-time privacy warning acceptance. |
| **API Base URL** | App Config | No | Directs network traffic to the correct backend. |

## Privacy Summary
- **No Automatic Background Uploads**: No data is sent to the cloud unless the user taps "Generate" in the AI flow.
- **Redaction**: Phone numbers and email addresses are automatically redacted by the app before leaving the device.
- **On-Device Insights**: Connection suggestions are calculated entirely on-device using local call history and user-defined schedules.
- **Strict Backup**: App-owned data is excluded from cloud backups to maximize privacy.
