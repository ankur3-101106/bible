# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-06-01

### Added
* **Dynamic Planning Engine**: Smart chapter allocation using `Math.ceil(remainingChapters / remainingDays)` to balance daily reading workloads cleanly.
* **Dynamic Schedule Redistribution**: Recalculates unread chapters when reading portions are missed without modifying completed history.
* **Offline KJV Reader**: Native Scripture reading surface rendering text from bundled `en_kjv.json` with theme customization (*Sanctuary Canvas*, *Sepia Contemplation*, *Nocturne Black*), font scaling (`A-`/`A+`), verse bookmarks, notes, and highlights.
* **Stitch Design System**: Full Material 3 dark/light theme implementation with responsive Bottom Navigation Bar (phones) and Navigation Rail (tablets/foldables).
* **WorkManager Daily Reminders**: Non-redundant background reminders with deep-link navigation.
* **Version 1 JSON Backup System**: Transactional database export and restore.
