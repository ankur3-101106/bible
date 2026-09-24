package com.sanctuary.bible.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PlanEntity::class,
        PlanDayEntity::class,
        BookmarkEntity::class,
        NoteEntity::class,
        HighlightEntity::class,
        ReadingPositionEntity::class,
        CompletedChapterEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SanctuaryDatabase : RoomDatabase() {

    abstract fun sanctuaryDao(): SanctuaryDao

    companion object {
        @Volatile
        private var INSTANCE: SanctuaryDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `reading_positions` (
                        `bookName` TEXT NOT NULL,
                        `chapter` INTEGER NOT NULL,
                        `verse` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`bookName`, `chapter`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `completed_chapters` (
                        `chapterRef` TEXT NOT NULL,
                        `completedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`chapterRef`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): SanctuaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SanctuaryDatabase::class.java,
                    "sanctuary_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
