package com.sanctuary.bible.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanctuary.bible.data.model.Verse
import com.sanctuary.bible.data.repository.BibleRepository
import com.sanctuary.bible.data.repository.PlanRepository
import com.sanctuary.bible.domain.PlanningEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ReaderTheme(val nameTitle: String, val bgHex: Long, val textHex: Long) {
    SANCTUARY("Sanctuary Canvas", 0xFF111318, 0xFFE2E2E9),
    SEPIA("Sepia Contemplation", 0xFF272118, 0xFFE8BE82),
    NOCTURNE("Nocturne Pure Black", 0xFF050608, 0xFF9CA3AF)
}

data class ReaderUiState(
    val bookName: String = "Genesis",
    val chapter: Int = 1,
    val verses: List<Verse> = emptyList(),
    val fontSizeSp: Float = 19f,
    val readerTheme: ReaderTheme = ReaderTheme.SANCTUARY,
    val selectedVerseNumber: Int? = null,
    val isLoading: Boolean = false,
    val isChapterCompleted: Boolean = false
)

class ReaderViewModel(
    private val bibleRepository: BibleRepository,
    private val planRepository: PlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        loadInitialReading()
    }

    private fun loadInitialReading() {
        viewModelScope.launch {
            val planDays = planRepository.activePlanDays.firstOrNull() ?: emptyList()
            val todayDay = planDays.find { !it.completed } ?: planDays.firstOrNull()
            val firstChapterRef = todayDay?.chapters?.firstOrNull()

            if (firstChapterRef != null) {
                val parts = firstChapterRef.split(" ")
                val chap = parts.last().toIntOrNull() ?: 1
                val book = parts.dropLast(1).joinToString(" ")
                loadChapter(book, chap)
            } else {
                loadChapter("Genesis", 1)
            }
        }
    }

    fun loadChapter(bookName: String, chapter: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, bookName = bookName, chapter = chapter, selectedVerseNumber = null) }
            val verses = bibleRepository.getChapterVerses(bookName, chapter)
            _uiState.update { it.copy(verses = verses, isLoading = false) }
        }
    }

    fun nextChapter() {
        val current = _uiState.value
        val bookMeta = PlanningEngine.BIBLE_BOOKS.find { it.name.equals(current.bookName, ignoreCase = true) }
            ?: return

        if (current.chapter < bookMeta.chapterCount) {
            loadChapter(current.bookName, current.chapter + 1)
        } else {
            // Book Boundary Transition (e.g. Genesis 50 -> Exodus 1)
            val currentBookIndex = PlanningEngine.BIBLE_BOOKS.indexOf(bookMeta)
            if (currentBookIndex >= 0 && currentBookIndex < PlanningEngine.BIBLE_BOOKS.size - 1) {
                val nextBook = PlanningEngine.BIBLE_BOOKS[currentBookIndex + 1]
                loadChapter(nextBook.name, 1)
            }
        }
    }

    fun previousChapter() {
        val current = _uiState.value
        val bookMeta = PlanningEngine.BIBLE_BOOKS.find { it.name.equals(current.bookName, ignoreCase = true) }
            ?: return

        if (current.chapter > 1) {
            loadChapter(current.bookName, current.chapter - 1)
        } else {
            // Book Boundary Transition Backwards (e.g. Exodus 1 -> Genesis 50)
            val currentBookIndex = PlanningEngine.BIBLE_BOOKS.indexOf(bookMeta)
            if (currentBookIndex > 0) {
                val prevBook = PlanningEngine.BIBLE_BOOKS[currentBookIndex - 1]
                loadChapter(prevBook.name, prevBook.chapterCount)
            }
        }
    }

    fun increaseFontSize() {
        _uiState.update { it.copy(fontSizeSp = (it.fontSizeSp + 2f).coerceAtMost(28f)) }
    }

    fun decreaseFontSize() {
        _uiState.update { it.copy(fontSizeSp = (it.fontSizeSp - 2f).coerceAtLeast(14f)) }
    }

    fun setReaderTheme(theme: ReaderTheme) {
        _uiState.update { it.copy(readerTheme = theme) }
    }

    fun selectVerse(verseNumber: Int) {
        _uiState.update {
            val newSelection = if (it.selectedVerseNumber == verseNumber) null else verseNumber
            it.copy(selectedVerseNumber = newSelection)
        }
    }

    fun bookmarkSelectedVerse() {
        val state = _uiState.value
        val verseNum = state.selectedVerseNumber ?: return
        viewModelScope.launch {
            bibleRepository.saveBookmark(state.bookName, state.chapter, verseNum)
        }
    }

    fun highlightSelectedVerse(colorHex: String = "#8F82F7") {
        val state = _uiState.value
        val verseNum = state.selectedVerseNumber ?: return
        viewModelScope.launch {
            bibleRepository.saveHighlight(state.bookName, state.chapter, verseNum, colorHex)
        }
    }

    fun addNoteToSelectedVerse(text: String) {
        val state = _uiState.value
        val verseNum = state.selectedVerseNumber ?: return
        viewModelScope.launch {
            bibleRepository.saveNote(state.bookName, state.chapter, verseNum, text)
        }
    }

    class Factory(
        private val bibleRepository: BibleRepository,
        private val planRepository: PlanRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReaderViewModel(bibleRepository, planRepository) as T
        }
    }
}
