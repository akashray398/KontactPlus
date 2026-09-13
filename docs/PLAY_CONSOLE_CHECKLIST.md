# Google Play Console Checklist

Prepare these items before starting the release process in Play Console.

## 1. Compliance Declarations
- [ ] **Data Safety**: Complete based on `DATA_SAFETY_DRAFT.md`.
- [ ] **Privacy Policy**: Host `PRIVACY_POLICY_DRAFT.md` at a public URL and link it.
- [ ] **Permissions**:
    - [ ] Declare `READ_CALL_LOG` as a core feature for a "Default Phone App".
    - [ ] Declare `USE_FULL_SCREEN_INTENT` for receiving calls.
- [ ] **Ads**: Declare **"No"** to containing ads.
- [ ] **Content Rating**: Complete the questionnaire (expected: Low maturity).
- [ ] **Target Audience**: 13+ or 18+. Avoid the "Children" category.

## 2. Technical Setup
- [ ] **App Signing**: Enable Google Play App Signing.
- [ ] **Internal Testing**: Create a track and upload the first AAB.
- [ ] **Version**: confirm `versionCode` and `versionName` in `build.gradle.kts`.

## 3. Store Listing
- [ ] **Branding**: Use "Kontact++" consistently.
- [ ] **Graphics**:
    - [ ] Launcher Icon (Adaptive)
    - [ ] Feature Graphic (1024x500)
    - [ ] Phone Screenshots (At least 4)
    - [ ] Tablet Screenshots (Optional but recommended)
- [ ] **Text**: Use `PLAY_STORE_LISTING_DRAFT.md`.

## 4. AI Disclosure
- [ ] Ensure the store description mentions that AI features are optional and user-initiated.
- [ ] Be prepared to explain the "redaction" logic if audited by Google.
