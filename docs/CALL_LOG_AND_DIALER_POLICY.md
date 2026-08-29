# Call Log and Dialer Policy

## READ_CALL_LOG Permission
The `READ_CALL_LOG` permission is used exclusively to display the user's call history within the "Recents" tab of Kontact++. This access is restricted and follows these strict guidelines:

1. **Role Requirement**: The application will only request or use the `READ_CALL_LOG` permission after the user has successfully set Kontact++ as their Default Phone App (ROLE_DIALER).
2. **Immediate Revocation Handling**: If Kontact++ loses its status as the Default Phone App, all access to the Call Log stops immediately. Any call log data stored in memory or displayed in the UI is cleared.
3. **Local-Only Processing**: Call history data is processed locally on the device. It is never uploaded to any server, shared with third parties, or persisted in any local database (e.g., Room).
4. **Purpose**: The data is used solely to provide a functional and integrated calling experience for the user.

## ROLE_DIALER (Default Phone App)
Kontact++ implements the `InCallService` API and provides a user interface for incoming and ongoing calls. By becoming the Default Phone App, Kontact++ ensures:
- A privacy-first calling experience.
- Seamless integration between contacts, favourites, and call history.
- Compliance with Google Play policies regarding the "Default Dialer" functionality.

## Data Privacy
- **No Persistence**: Call log data remains in volatile memory only for the duration of the application's active session.
- **No Logging**: Personal identifiable information (PII) such as phone numbers or names from the call log are never logged to Logcat or any crash reporting service.
