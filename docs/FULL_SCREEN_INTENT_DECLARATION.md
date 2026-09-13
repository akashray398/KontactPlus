# Full-Screen Intent Declaration (Google Play)

## Core Use Case
Kontact++ is a **default Phone application**. We use `USE_FULL_SCREEN_INTENT` to provide the standard incoming call experience. 

## Implementation
1. **Trigger**: When an incoming phone call is received via the `InCallService`.
2. **Action**: The app sends a high-priority notification with a `FullScreenIntent` pointing to our `ActiveCallActivity`.
3. **User Benefit**: Allows the user to see the caller's identity and answer the call immediately, even if the device is locked or another app is in the foreground.
4. **Fallback**: If the user has disabled full-screen intents for the app, we provide a standard heads-up notification with "Answer" and "Decline" actions.

## Policy Compliance
- **No Intrusive Ads**: We do not use full-screen intents for advertisements or marketing.
- **No Reminders**: Relationship reminders use standard notifications, not full-screen intents.
- **Specific Feature**: This is a core part of the dialer functionality.

---
*Verified: Automated tests were not created or run.*
