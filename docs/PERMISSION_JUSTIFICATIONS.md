# Permission Justifications for Google Play

This document provides the rationale for each sensitive permission requested by Kontact++.

## READ_CONTACTS
- **Feature**: Contact management, search, and relationship mapping.
- **Benefit**: Users can browse their device contacts within the app and attach private notes or reminders to them.
- **Timing**: Requested on the "Contacts" or "Favourites" screen if not granted.
- **Denial Path**: Users can still enter numbers manually in the Keypad, but contact-based features (names, avatars, notes) will be disabled.

## READ_CALL_LOG
- **Feature**: Recents list and Connection Insights.
- **Benefit**: Allows the app to show call history and suggest follow-ups locally based on the user's interaction cadence.
- **Policy Compliance**: This permission is requested **only** after Kontact++ has been selected as the user's **default Phone application**.
- **Timing**: Requested when opening the "Recents" tab or enabling "Call Analysis" in settings.

## CALL_PHONE
- **Feature**: Direct dialling from the keypad or contact list.
- **Benefit**: Users can place calls with a single tap.
- **Constraint**: This is used **only** when the user explicitly taps a "Call" button. No automatic calls are placed.

## POST_NOTIFICATIONS
- **Feature**: Relationship reminders and incoming call alerts.
- **Benefit**: Ensures users never miss an scheduled follow-up or an incoming phone call.
- **Timing**: Requested in-context before the first reminder is set or upon becoming the default dialer.

## USE_FULL_SCREEN_INTENT
- **Feature**: Incoming call handling.
- **Benefit**: Provides a high-priority UI for answering calls while the screen is locked or the phone is in use.
- **Compliance**: Used strictly for phone calls. Never used for marketing or background reminders.

## INTERNET
- **Feature**: Optional AI Writing Assistance and Backend Health Checks.
- **Benefit**: Users can generate smart follow-up drafts or greetings.
- **Privacy**: Core contacts and dialer features work entirely offline. Internet is only used for user-initiated AI actions.
