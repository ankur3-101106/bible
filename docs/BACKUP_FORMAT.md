# Sanctuary Backup Format Specification (Version 1)

## Overview

Sanctuary provides a versioned, deterministic, human-readable JSON backup system allowing users to export and restore their active plans, reading history, bookmarks, notes, and highlights.

## JSON Schema (Version 1)

```json
{
  "backupVersion": 1,
  "createdAt": "2026-06-01T20:00:00Z",
  "appVersion": "1.0",
  "plans": [
    {
      "id": "plan_1711929600000",
      "title": "Complete the Bible",
      "startDateIso": "2026-01-01",
      "endDateIso": "2026-12-31",
      "isActive": true
    }
  ],
  "planDays": [
    {
      "id": "day-20454",
      "planId": "plan_1711929600000",
      "dateIso": "2026-01-01",
      "displayDate": "Thursday, Jan 1",
      "chaptersJson": "[\"Genesis 1\",\"Genesis 2\",\"Genesis 3\"]",
      "readingString": "Genesis 1–3",
      "completed": true
    }
  ],
  "bookmarks": [],
  "notes": [],
  "highlights": []
}
```

## Validation & Restore Strategy
* **Schema Validation**: Checks `backupVersion <= 1` and verifies ISO date formats.
* **Transactional Import**: Restoration uses Room's `database.withTransaction` block. If any error occurs during import, the transaction rolls back, leaving the existing local database completely untouched.
