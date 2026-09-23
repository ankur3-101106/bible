# Planning Engine Specification & Invariants

## Core Distribution Invariants

The `PlanningEngine` calculates daily chapter workloads dynamically:

$$\text{chaptersTodayCount} = \left\lceil \frac{\text{remainingChapters}}{\text{remainingDays}} \right\rceil$$

### Key Mathematical Guarantees
1. **Zero Chapter Drift**: $\sum \text{assignedChapters} = \text{totalPlannedChapters}$.
2. **Strict Uniqueness**: Every chapter in the selected scope occurs **exactly once**.
3. **No Duplicate Allocations**: Re-distributing an active plan preserves all previously completed history and re-slices only the unread chapters cleanly.

## Schedule Redistribution
When a user falls behind, `redistributePlan()`:
1. Filters completed days into a historical log.
2. Collects all unread chapters from uncompleted days.
3. Re-runs schedule allocation from `today` through `endDate`.
4. Merges completed history with the newly allocated schedule.
