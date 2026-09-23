# Sanctuary Architecture Specification

## Overview

Sanctuary follows modern Android architecture recommendations using a layered, local-first design pattern:

```
[ UI Layer ] (Jetpack Compose, ViewModels, StateFlow, Material 3)
      │
[ Domain Layer ] (PlanningEngine, Schedule Algorithms)
      │
[ Data Layer ] (Room Database, BibleRepository, PlanRepository)
```

## Layers

### 1. UI / Presentation Layer (`ui/`)
* **Framework**: Built entirely using Jetpack Compose and Material 3 design tokens derived from the Google Stitch design system.
* **State Management**: Reactive ViewModels expose `StateFlow` state objects consumed by Composables.
* **Navigation**: Type-safe navigation via `SanctuaryNavGraph` supporting Bottom Navigation Bar (phones) and Navigation Rail (tablets/foldables).

### 2. Domain Layer (`domain/`)
* **`PlanningEngine`**: Pure Kotlin domain object responsible for dynamic chapter schedule allocation, daily chapter range formatting, and schedule redistribution.

### 3. Data & Persistence Layer (`data/`)
* **`SanctuaryDatabase`**: Room database handling plans, daily schedules, bookmarks, notes, and highlights.
* **`BibleRepository`**: Asynchronously parses `en_kjv.json` asset on background threads, caching parsed chapter text in memory.
* **`PlanRepository`**: Interfaces between `SanctuaryDao` and `PlanningEngine`.
