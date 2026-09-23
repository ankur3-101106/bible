# Sanctuary Data Storage & Database Architecture

## Persistence Layer

Sanctuary uses **Room Database** (`SanctuaryDatabase`) backed by SQLite on Android.

### Database Tables
1. **`plans`**: Stores reading plan metadata (`id`, `title`, `startDateIso`, `endDateIso`, `isActive`).
2. **`plan_days`**: Stores daily chapter allocations (`id`, `planId`, `dateIso`, `displayDate`, `chaptersJson`, `readingString`, `completed`).
3. **`bookmarks`**: Stores bookmarked verses (`id`, `bookName`, `chapter`, `verse`, `createdAt`).
4. **`notes`**: Stores user journal reflections (`id`, `bookName`, `chapter`, `verse`, `noteText`, `createdAt`, `updatedAt`).
5. **`highlights`**: Stores verse highlights (`id`, `bookName`, `chapter`, `verse`, `colorHex`, `createdAt`).

### Local-First & Privacy Assurances
* All data is stored 100% locally on the device.
* No network transmission, third-party tracking, or external telemetry is used.
