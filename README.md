# Sanctuary

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![License](https://img.shields.io/badge/License-GPL--3.0-blue.svg?style=flat-square)
![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square)

**A thoughtful, offline-first Bible reading companion built around dynamic adaptive reading plans.**

</div>

---

## Overview

**Sanctuary** is a contemplative, distraction-free Android application designed for daily Scripture engagement and spiritual formation. Built natively with **Jetpack Compose**, **Material 3**, and **Room Database**, Sanctuary features a dynamic planning engine that calculates daily reading portions with mathematical precision—ensuring your reading workload remains balanced without ever missing a chapter or dropping progress history.

---

## Key Features

* **Dynamic Planning Engine**: Smart chapter allocation using `Math.ceil(remainingChapters / remainingDays)` to balance daily reading workloads cleanly.
* **Dynamic Schedule Redistribution**: Fell behind on your reading? Recalculate unread chapters across your remaining schedule starting today without modifying completed history.
* **Offline KJV Scripture Reader**: Read all 1,189 chapters of the King James Version offline with theme customization (*Sanctuary Canvas*, *Sepia Contemplation*, *Nocturne Black*), font scaling (`A-`/`A+`), verse bookmarks, notes, and highlights.
* **Stitch Design System**: Full Material 3 dark/light theme implementation with responsive Bottom Navigation Bar (phones) and Navigation Rail (tablets/foldables).
* **WorkManager Daily Reminders**: Non-redundant background reminders with deep-link navigation directly to today's reading.
* **Version 1 JSON Backup System**: Transactional database export and restore that guarantees zero data loss or partial database corruption.
* **100% Local-First & Private**: Zero ads, zero tracking, zero cloud telemetry, and completely offline-first.

---

## Architecture & Tech Stack

Sanctuary follows modern Android architecture recommendations using a layered, local-first design pattern:

```
[ UI Layer ] (Jetpack Compose, ViewModels, StateFlow, Material 3)
      │
[ Domain Layer ] (PlanningEngine, Schedule Algorithms)
      │
[ Data Layer ] (Room Database, BibleRepository, PlanRepository)
```

* **Language**: Kotlin 2.0
* **UI**: Jetpack Compose, Material 3, Navigation Compose
* **Database**: Room Database (`SanctuaryDatabase`)
* **Background Scheduling**: WorkManager (`androidx.work`)
* **Concurrency**: Kotlin Coroutines & StateFlow
* **JSON Processing**: Gson

---

## Getting Started & Building

### Prerequisites
* Android Studio Ladybug or newer
* JDK 17+
* Android SDK 35

### Building the Project
```bash
# Clone the repository
git clone https://github.com/ankur3-101106/bible.git
cd bible

# Run Unit Tests
./gradlew test

# Build Debug APK
./gradlew assembleDebug
```

---

## Documentation

* [Architecture Specification](docs/ARCHITECTURE.md)
* [Planning Engine Mathematics & Invariants](docs/PLANNING_ENGINE.md)
* [Notifications & Background Worker Specification](docs/NOTIFICATIONS.md)
* [Backup & Restore JSON Format (Version 1)](docs/BACKUP_FORMAT.md)
* [Data Storage & Room Database Schema](docs/DATA_STORAGE.md)
* [Privacy Policy & Local-First Commitment](docs/PRIVACY.md)
* [Play Store Data Safety Declarations](docs/PLAY_DATA_SAFETY.md)
* [Bible Data Licensing & KJV Attribution](docs/BIBLE_DATA_LICENSE.md)

---

## License

Sanctuary is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.
