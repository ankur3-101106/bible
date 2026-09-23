package com.sanctuary.bible

import android.app.Application
import com.sanctuary.bible.data.local.SanctuaryDatabase
import com.sanctuary.bible.data.repository.BibleRepository
import com.sanctuary.bible.data.repository.PlanRepository

class SanctuaryApplication : Application() {

    val database by lazy { SanctuaryDatabase.getDatabase(this) }
    val planRepository by lazy { PlanRepository(database.sanctuaryDao()) }
    val bibleRepository by lazy { BibleRepository(this, database.sanctuaryDao()) }
}
