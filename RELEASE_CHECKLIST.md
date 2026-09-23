# Release Verification Checklist — Sanctuary RC1

## 1. Build & Compilation
- [x] `./gradlew clean`
- [x] `./gradlew test` (All unit tests passing)
- [x] `./gradlew assembleDebug` (Debug APK generated successfully)
- [x] ProGuard/R8 rules verified in `app/proguard-rules.pro`

## 2. Functionality & Offline Readiness
- [x] Dynamic Plan Generation (1,189 chapters, 365 days / custom schedules)
- [x] Schedule Redistribution on overdue portions
- [x] Offline KJV Bible text rendering from `en_kjv.json`
- [x] Bookmark, Note, and Highlight persistence in Room DB
- [x] Version 1 JSON Backup Export and Import
- [x] WorkManager Daily Reminders & Boot Receiver

## 3. Security & Privacy
- [x] Zero `INTERNET` permission in `AndroidManifest.xml` (100% offline)
- [x] No sensitive credentials or keystore files committed
- [x] Privacy documentation created (`docs/PRIVACY.md`, `docs/PLAY_DATA_SAFETY.md`)
- [x] Public Domain KJV licensing documented (`docs/BIBLE_DATA_LICENSE.md`)
