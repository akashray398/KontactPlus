# Kontact++ Release Checklist

## A. Mandatory Security Verification
- [x] **Zero Secrets**: Confirm no API keys in source code, `BuildConfig`, or `local.properties`.
- [x] **No BODY Logs**: Confirm `HttpLoggingInterceptor` is set to `BASIC` in release.
- [x] **HTTPS Only**: Confirm `AI_BASE_URL` in release build type starts with `https://`.
- [x] **Backup Safe**: Confirm `data_extraction_rules.xml` excludes the database.
- [x] **Version Confirmed**: `versionCode = 1`, `versionName = "1.0.0-rc01"`.
- [x] **Release-Candidate Artifacts**: `app-release.apk` and `app-release.aab` generated successfully.

## B. Production Infrastructure
- [ ] **AI Backend**: Deploy the Kotlin/Ktor server to a production host.
- [ ] **Authentication**: Implement a strategy (e.g. Play Integrity) to secure the Ktor endpoints.
- [ ] **Rate Limiting**: Enable per-IP rate limiting on the production host.
- [ ] **Monitoring**: Set up coarse operational logging (success/fail count).

## C. Functional Smoke Test (Release APK)
- [ ] **Tabs**: All 5 tabs function.
- [ ] **Dialer**: Can place an outgoing call.
- [ ] **Notes**: Can save and read a private note.
- [ ] **AI**: Attempting AI in release shows "Backend not configured" (if unconfigured) or succeeds.
- [ ] **Privacy Center**: Can delete all local relationship data successfully.

## D. External Requirements
- [ ] **Privacy URL**: Active and reachable URL for the policy.
- [ ] **Support Email**: Dedicated email address for user inquiries.
- [ ] **Legal**: Review the draft policy with an owner/legal rep.
