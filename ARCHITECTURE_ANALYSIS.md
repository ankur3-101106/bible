# Sanctuary Bible Planner — Architecture Analysis

## Executive Summary

This document provides a comprehensive architectural analysis of the existing **Sanctuary Bible Planner** web application codebase located in the `ankur3-101106/bible` repository. It documents the core data files, data structures, planning algorithms, state persistence mechanisms, UI components, date handling logic, edge cases, and a step-by-step trace of the end-to-end user workflows.

This analysis serves as the blueprint for preserving and improving the planning behavior when implementing the Android native app.

---

## 1. Existing Bible Data Files

* **`en_kjv.json`** (~4.9 MB):
  * Primary Bible data file containing the full text of the King James Version (KJV).
  * Structure: JSON array of 66 book objects (Genesis to Revelation).
  * Fetched asynchronously during app initialization via `fetch('en_kjv.json')`.
  * Provides verse-level text used for rendering passages directly inside the application UI.

---

## 2. Existing Bible Data Structure

### A. Book Metadata (`bibleBooks`)
Hardcoded JavaScript array containing metadata for all 66 books of the Bible in canonical order:

```javascript
const bibleBooks = [
  { n: "Genesis", c: 50, abbrev: "gn" },
  { n: "Exodus", c: 40, abbrev: "ex" },
  ...
  { n: "Revelation", c: 22, abbrev: "re" }
];
```
* `n` (*string*): Full book name (e.g., `"Genesis"`).
* `c` (*number*): Total number of chapters in the book (e.g., `50`).
* `abbrev` (*string*): Canonical short identifier (e.g., `"gn"`).
* **Total Chapters**: 1,189 across all 66 books.

### B. Flat Chapter List (`allChaptersFlat`)
Constructed dynamically on app initialization by flattening `bibleBooks`:

```javascript
let allChaptersFlat = [];
bibleBooks.forEach(book => {
  for (let i = 1; i <= book.c; i++) allChaptersFlat.push(`${book.n} ${i}`);
});
```
* Result: A 1D array of 1,189 chapter reference strings:
  `["Genesis 1", "Genesis 2", ..., "Revelation 22"]`.

### C. Bible Text JSON Structure (`en_kjv.json`)
```json
[
  {
    "abbrev": "gn",
    "name": "Genesis",
    "chapters": [
      [
        "In the beginning God created the heaven and the earth.",
        "And the earth was without form, and void...",
        ...
      ],
      ...
    ]
  },
  ...
]
```
* `chapters`: 2D array (`chapters[chapterIndex][verseIndex]`). Note: 0-indexed chapter arrays (`chapters[0]` represents Chapter 1).
* Verse text strings contain optional inline translator notes formatted inside curly braces (e.g., `{And the evening...: Heb. ...}`).

---

## 3. Existing Planning Algorithm

The core chapter distribution algorithm is implemented in `distributeChapters(chaptersToAssign, startD, endD)`:

```javascript
function distributeChapters(chaptersToAssign, startD, endD) {
  startD.setHours(0, 0, 0, 0);
  endD.setHours(0, 0, 0, 0);

  const diffTime = Math.abs(endD - startD);
  const totalDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

  const plan = [];
  let chapterIndex = 0;

  for (let day = 0; day < totalDays; day++) {
    if (chapterIndex >= chaptersToAssign.length) break;

    const remainingDays = totalDays - day;
    const remainingChapters = chaptersToAssign.length - chapterIndex;
    const chaptersTodayCount = Math.ceil(remainingChapters / remainingDays);

    const chaptersToday = chaptersToAssign.slice(chapterIndex, chapterIndex + chaptersTodayCount);

    const currentDate = new Date(startD);
    currentDate.setDate(startD.getDate() + day);

    plan.push({
      id: `day-${currentDate.getTime()}`,
      dateString: currentDate.toISOString().split('T')[0],
      displayDate: currentDate.toLocaleDateString(undefined, { weekday: 'long', month: 'short', day: 'numeric' }),
      chapters: chaptersToday,
      readingString: formatReadingString(chaptersToday),
      completed: false
    });

    chapterIndex += chaptersTodayCount;
  }
  return plan;
}
```

### Key Mathematical Characteristics
1. **Inclusive Day Calculation**: `totalDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1`.
2. **Dynamic Daily Pace**: `chaptersTodayCount = Math.ceil(remainingChapters / remainingDays)`.
   * Using `Math.ceil()` on the ratio of remaining chapters to remaining days balances chapter counts evenly across all remaining days.
   * If chapters do not divide evenly into days, earlier days receive 1 additional chapter until the remainder is exhausted.
   * Prevents rounding drift and guarantees that all chapters are assigned by the final day.

---

## 4. Existing Plan-Generation Logic

* Function: `generatePlan()`
* Input: `#start-date` and `#end-date` HTML date picker values.
* Validation: Ensures both dates are selected and `endDate > startDate`.
* Data Generation: Calls `distributeChapters(allChaptersFlat, startDate, endDate)`.
* Formatting Helper (`formatReadingString`):
  * Single chapter: `"Genesis 1"`
  * Multiple chapters in same book: `"Genesis 1-3"`
  * Multi-book range: `"Genesis 50 to Exodus 2"`
* Storage: Serializes plan array to JSON and stores in `localStorage.setItem('sanctuaryPlan', ...)`.

---

## 5. Existing Progress Calculation

* Total Bible chapters: **1,189** (fixed).
* Function: `updateStats(completedCount)`
* Calculation:
  * `completedCount = sum(day.chapters.length for day in plan where day.completed === true)`
  * `percentage = Math.round((completedCount / 1189) * 100)`
* UI Output:
  * Progress Bar CSS Width: `${percentage}%`
  * Stat Text: `${completedCount} / 1189 chapters`

---

## 6. Existing Streak Calculation

* Function: `updateStreak()`
* Traverses `plan` array backwards (from `index = plan.length - 1` down to `0`).
* Algorithm:
  * Tracks `streak` counter and target `checkDate` initialized to `today` (midnight).
  * Evaluates completed days:
    * If a completed day matches `checkDate` or `today` (when starting) or `checkDate - 1 day` (86,400,000 ms), increments `streak` and steps `checkDate` back by 1 day.
    * Breaks out of the loop upon hitting any uncompleted day or date gap.
* Display: Shows `🔥 X day streak` indicator badge when `streak > 0`.

---

## 7. Existing Reading Completion Logic

* User clicks card or checkbox $\rightarrow$ calls `toggleDay(index)`.
* Action: Flips boolean flag `plan[index].completed = !plan[index].completed`.
* Persistence: Writes updated plan array back to `localStorage`.
* UI Refresh: Calls `renderList(plan, false)` (with `autoScroll = false` to preserve scroll position):
  * Recalculates total completed chapters.
  * Updates progress bar & percentage.
  * Re-evaluates streak counter.
  * Re-checks for past overdue/missed days.

---

## 8. Existing LocalStorage / State Persistence

* `'sanctuaryPlan'`: JSON string storing array of Day Plan objects.
* `'theme'`: String storing current UI theme (`'light'` or `'midnight'`).
* Fully client-side, synchronous reading & writing.
* Reset/Clear: `clearPlan()` prompts user confirmation and calls `localStorage.removeItem('sanctuaryPlan')`.

---

## 9. Existing UI Structure

* **Responsive 2-Pane Layout**:
  * **Sidebar / Drawer** (Left on Desktop, Drawer on Mobile):
    * Branding header + Midnight/Light theme toggle switch.
    * Setup Form (`#setup-section`): Start Date, End Date, "Generate Plan" button.
    * Dashboard Stats (`#dashboard-section`): Progress bar, percentage, chapter count, streak badge.
    * Overdue Alert (`#missed-alert`): Displayed when missed days exist; contains "🔄 Dynamic Adjust" button (`redistributePlan()`).
    * Reading View Toggles: "📍 Today's Portion" vs "📚 Full Plan" view mode selector.
    * Reset Button ("⚠️ Start Over").
  * **Main Content Area**:
    * Desktop Header: Title and subtitle.
    * Reading List Container (`#reading-list`): Renders Day Cards for selected view mode.
    * Day Card: Custom circular checkbox, date label ("🎯 Today", "⚠️ Overdue", or date string), reading text, chapter count pill, and "Read" button.
    * Inline Passage Reader (`togglePassage()`): Expands passage container below card, fetching and displaying verse-by-verse text formatted from `en_kjv.json`.

---

## 10. Existing Date Handling

* Date Inputs: HTML5 `<input type="date">` (`YYYY-MM-DD`).
* Date Objects: JS `Date` initialized from date strings or timestamp arithmetic.
* Midnight Normalization: `dateObj.setHours(0, 0, 0, 0)`.
* ISO Date Output: `dateObj.toISOString().split('T')[0]`.
* Display Date Formatting: `dateObj.toLocaleDateString(undefined, { weekday: 'long', month: 'short', day: 'numeric' })`.

---

## 11. Existing Assumptions, Limitations, and Known Bugs

1. **Timezone / ISO String Shift Bug**:
   * `new Date("YYYY-MM-DD")` parses strings in UTC.
   * Normalizing with `setHours(0,0,0,0)` converts time to local midnight.
   * Calling `.toISOString()` converts back to UTC. In negative UTC timezones (e.g., UTC-5 / EST), local midnight `2025-01-01 00:00:00 EST` becomes `2024-12-31T05:00:00.000Z`, so `.split('T')[0]` produces `"2024-12-31"`. This causes date strings in generated plans to shift back by one day!
2. **Fixed 1189 Chapter Constant**:
   * Progress calculation always divides completed chapters by 1189, assuming a full Bible plan. Custom plan lengths or subsets are not supported.
3. **Streak DST Bug & Today Logic**:
   * Fixed millisecond subtraction (`86,400,000` ms) in streak calculation fails across Daylight Saving Time boundaries (23 or 25 hour days).
   * If today is uncompleted, the backward check stops prematurely if yesterday was completed.
4. **Redistribution Logic Limitations**:
   * `redistributePlan()` gathers ALL unread chapters from past AND future uncompleted days and redistributes them from `today` to `endDate`.
   * If `today > endDate`, `redistributePlan()` shows an alert but passes `endDate` to `distributeChapters`, which can lead to invalid date ranges.
5. **No Verse-Level Progress**:
   * Completion tracking is strictly binary per day entry (all chapters in a day marked completed or uncompleted).
6. **Single Plan Limit**:
   * `localStorage` supports only a single plan stored under key `'sanctuaryPlan'`.

---

## Complete End-to-End Workflow Trace

```
Create Plan
  │
  ▼
Generate Schedule
  │
  ▼
Display Today's Reading
  │
  ▼
Mark Reading Complete
  │
  ▼
Update Progress
  │
  ▼
Update Streak
  │
  ▼
Handle Missed Reading
  │
  ▼
Redistribute Remaining Reading
```

### Detailed Trace Steps

1. **Create Plan**:
   * User inputs `Start Date` and `Goal Finish Date` in `#setup-section`.
   * Clicks "Generate Plan", invoking `generatePlan()`.

2. **Generate Schedule**:
   * `generatePlan()` validates dates (`startDate < endDate`).
   * Calls `distributeChapters(allChaptersFlat, startDate, endDate)`:
     * Calculates `totalDays = Math.ceil(diffTime / 86400000) + 1`.
     * Loops `day` from 0 to `totalDays - 1`.
     * Computes `chaptersTodayCount = Math.ceil(remainingChapters / remainingDays)`.
     * Slices chapters from `allChaptersFlat`.
     * Formats ref string via `formatReadingString()`.
     * Creates day plan object with `completed = false`.
   * Serializes array to `localStorage.setItem('sanctuaryPlan', ...)`.

3. **Display Today's Reading**:
   * Calls `renderList(plan)`.
   * Hides `#setup-section`, shows `#dashboard-section` and `#list-header`.
   * Filters plan items based on `currentViewMode`:
     * `'today'` mode: Displays cards where `isToday === true` or `isMissed === true`.
     * `'full'` mode: Displays all cards and scrolls to today's card via `scrollIntoView()`.
   * User can click "Read" on a card $\rightarrow$ `togglePassage()` extracts verses from loaded `en_kjv.json` and displays text inline.

4. **Mark Reading Complete**:
   * User taps day card or checkbox $\rightarrow$ calls `toggleDay(index)`.
   * Flips `plan[index].completed = !plan[index].completed`.
   * Saves updated plan to `localStorage`.
   * Re-renders list without auto-scrolling (`renderList(plan, false)`).

5. **Update Progress**:
   * `renderList()` calculates `completedChaptersCount = sum(day.chapters.length for completed days)`.
   * Calls `updateStats(completedChaptersCount)`.
   * Computes `percentage = Math.round((completedCount / 1189) * 100)`.
   * Updates progress bar width (`percentage%`) and text (`"X / 1189 chapters"`).

6. **Update Streak**:
   * `updateStats()` calls `updateStreak()`.
   * Iterates backwards through `plan` array checking consecutive completed dates ending today/yesterday.
   * Updates `#streak-days` text and toggle visibility of `#streak-indicator` badge.

7. **Handle Missed Reading**:
   * `renderList()` compares `dayDateObj < todayObj`.
   * If `isPast` and `!day.completed`, flags `isMissed = true` and `missedDays = true`.
   * Overdue cards render with red styling and `"⚠️ Overdue"` status label.
   * Displays `#missed-alert` banner in sidebar with "🔄 Dynamic Adjust" button.

8. **Redistribute Remaining Reading**:
   * User clicks "🔄 Dynamic Adjust" $\rightarrow$ calls `redistributePlan()`.
   * Extracts `completedHistory` (completed days) and `unreadChapters` (all chapters from uncompleted past/future days).
   * Invokes `distributeChapters(unreadChapters, today, endDate)` to evenly redistribute remaining chapters across remaining days.
   * Merges `completedHistory` and new future plan: `finalPlan = [...completedHistory, ...newFuturePlan].sort(...)`.
   * Saves `finalPlan` to `localStorage` and calls `renderList(finalPlan)`.

---

## Architectural Guidelines for Native Android Implementation

When building the native Android application:
1. **Preserve Planning Algorithm Intention**:
   * Keep the `Math.ceil(remainingChapters / remainingDays)` dynamic distribution algorithm as it ensures perfectly balanced daily chapter allocations without rounding drift.
2. **Fix Date & Timezone Issues**:
   * Use modern `java.time.LocalDate` in Kotlin to handle dates cleanly without time, timezone, or DST conversion bugs.
3. **Enhance Database & Storage**:
   * Replace `localStorage` JSON blobs with a robust Room Database (or SQLite) for storing plans, days, and chapter completion records.
4. **Improve Bible Data Management**:
   * Parse `en_kjv.json` into a structured SQLite/Room database or pre-indexed asset structure for fast querying and scrolling.
5. **Modern UI Architecture**:
   * Use Jetpack Compose with Material 3 for responsive UI, dark/light theme support, smooth scrolling, and expandable passage reading cards.
