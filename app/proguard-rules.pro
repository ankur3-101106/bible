# Keep Room Entities and DAOs
-keep class com.sanctuary.bible.data.local.** { *; }

# Keep Gson models and TypeTokens
-keep class com.sanctuary.bible.data.model.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }

# Keep WorkManager workers
-keep class com.sanctuary.bible.worker.** { *; }
