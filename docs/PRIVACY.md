# Sanctuary Privacy Policy & Local-First Commitment

## Summary
Sanctuary is built as a **100% offline-first, local-first** application. We do not track you, collect your personal reflections, or transmit your reading habits anywhere.

## Data Storage
* **Local Database**: All plans, reading progress, bookmarks, notes, and highlights are stored exclusively on your device in a local Room database (`sanctuary_database`).
* **No Cloud Transmission**: Sanctuary does not operate external servers, analytics engines, or cloud tracking services.
* **No Network Permission**: Sanctuary does not request or require internet access permissions (`android.permission.INTERNET` is omitted from `AndroidManifest.xml`).

## Backups
* Backups are created only when explicitly exported by the user using Android's system document picker (`CreateDocument`).
* Backup files are saved locally to the user's chosen directory.
