package com.sanctuary.bible.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlanEntity::class,
        PlanDayEntity::class,
        BookmarkEntity::class,
        NoteEntity::class,
        HighlightEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SanctuaryDatabase : RoomDatabase() {

    abstract fun sanctuaryDao(): SanctuaryDao

    companion object {
        @Volatile
        private var INSTANCE: SanctuaryDatabase? = null

        fun getDatabase(context: Context): SanctuaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SanctuaryDatabase::class.java,
                    "sanctuary_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
