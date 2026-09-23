# Sanctuary Performance & Reliability Specification

## Performance Optimizations

1. **Reactive Flow Search Debouncing**:
   * Bible search queries are debounced at 300ms using Coroutine `debounce(300L)` and `mapLatest`.
   * Search execution runs on `Dispatchers.IO` and yields early upon reaching the 50-result limit to preserve UI responsiveness.

2. **Compose Recomposition & Layouts**:
   * Used `remember` and `derivedStateOf` to prevent redundant recompositions during scroll events.
   * Constrained maximum layout width to `680dp` on tablet and wide-screen displays to ensure optimal reading measure.

3. **Background Asynchronous AssetState**:
   * The 4.9MB `en_kjv.json` dataset is loaded and parsed asynchronously on background threads using `Dispatchers.IO` with memory caching in `BibleRepository`.
