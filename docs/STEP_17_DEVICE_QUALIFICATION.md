# Step 17: Device Qualification Record

This document records the manual qualification of Telecom features on a physical device.

## Device Information
- **Manufacturer**: [To be filled by user]
- **Model**: [To be filled by user]
- **Android Version**: [To be filled by user]
- **API Level**: [To be filled by user]
- **SIM Count**: [To be filled by user]

## Summary of Results
| Scenario | Result | Observations |
| :--- | :--- | :--- |
| A. Installation and Role | | |
| B. Outgoing Call | | |
| C. Incoming Call | | |
| D. Controls (Mute/Audio) | | |
| E. Lifecycle (Rotation/BG) | | |
| F. Role Loss | | |
| G. Privacy (Logcat) | | |

## Detailed Scenarios

### A. Installation and Role
1. **Initial Launch**: No role dialog appears automatically. [EXPECTED: PASS]
2. **Onboarding**: Explicit explanation of why the Phone role is needed. [EXPECTED: PASS]
3. **Role Request**: System dialog appears after tapping the action. [EXPECTED: PASS]
4. **Accept Role**: App detects the role is held and unlocks features. [EXPECTED: PASS]
5. **Decline Role**: No repeated dialog loop. [EXPECTED: PASS]

### B. Outgoing Call
1. **Normal Call**: Entering a number and tapping Call starts exactly one Telecom session. [EXPECTED: PASS]
2. **State Transition**: UI shows Dialing/Connecting then Active. [EXPECTED: PASS]
3. **End Call**: Tapping the end button closes the UI and clears the notification. [EXPECTED: PASS]
4. **Airplane Mode**: System handles unavailability gracefully. [EXPECTED: PASS]

### C. Incoming Call
1. **Identity**: Shows contact name for Known Contact A, "Unknown Caller" for others. [EXPECTED: PASS]
2. **Actions**: Answer and Decline buttons are clearly labeled and functional. [EXPECTED: PASS]
3. **Lock Screen**: Call UI appears over the lock screen correctly. [EXPECTED: PASS]
4. **Heads-up**: Heads-up notification appears when the phone is unlocked. [EXPECTED: PASS]

### D. Controls
1. **Mute**: Mute toggle updates UI and correctly silences the local microphone. [EXPECTED: PASS]
2. **Speaker**: Switching to speaker route works and reflects in UI. [EXPECTED: PASS]
3. **DTMF**: Keypad sends tones (verified by remote party or IVR). [EXPECTED: PASS]
4. **Hold**: If supported by the carrier/device, Hold/Resume works. [EXPECTED: NOT_APPLICABLE if unsupported]

### E. Lifecycle
1. **Rotation**: UI reconnects to the active call state without interrupting the call. [EXPECTED: PASS]
2. **Background**: Call remains active; notification allows returning to UI. [EXPECTED: PASS]
3. **Process Restart**: App recovers active call state if killed while Telecom service is alive. [EXPECTED: PASS]

### F. Role Loss
1. **Revoke Role**: Changing default Phone app away from Kontact++ is detected. [EXPECTED: PASS]
2. **Restricted Access**: Call history reading and dialer features are disabled safely. [EXPECTED: PASS]

### G. Privacy
1. **Logcat Audit**: No phone numbers, caller names, or private notes are logged. [EXPECTED: PASS]
2. **Backend**: No call metadata or log data is sent to the Ktor server. [EXPECTED: PASS]

## Emergency Calling
- **Status**: EXCLUDED from manual testing.
- **Handling**: Kontact++ defers all emergency number routing to the Android platform and system dialer.

---
*Confirmation: No emergency calls were placed during verification. Automated tests were not created or run.*
