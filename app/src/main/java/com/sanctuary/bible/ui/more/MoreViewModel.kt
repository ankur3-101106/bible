package com.sanctuary.bible.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanctuary.bible.data.model.Bookmark
import com.sanctuary.bible.data.model.JsonBook
import com.sanctuary.bible.data.model.Note
import com.sanctuary.bible.data.repository.BibleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchResultItem(
    val bookName: String,
    val chapter: Int,
    val verseNumber: Int,
    val snippetText: String
)

data class MoreUiState(
    val searchQuery: String = "",
    val searchResults: List<SearchResultItem> = emptyList(),
    val isSearching: Boolean = false,
    val bookmarks: List<Bookmark> = emptyList(),
    val notes: List<Note> = emptyList()
)

class MoreViewModel(
    private val bibleRepository: BibleRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(MoreUiState())
    val uiState: StateFlow<MoreUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bibleRepository.bookmarks.collect { list ->
                _uiState.update { it.copy(bookmarks = list) }
            }
        }

        observeSearchQuery()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .mapLatest { query ->
                    if (query.length < 3) {
                        emptyList()
                    } else {
                        _uiState.update { it.copy(isSearching = true) }
                        val results = searchInternal(query)
                        _uiState.update { it.copy(isSearching = false) }
                        results
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { results ->
                    _uiState.update { it.copy(searchResults = results) }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    private suspend fun searchInternal(query: String): List<SearchResultItem> {
        val data: List<JsonBook> = bibleRepository.loadBibleData()
        val results = mutableListOf<SearchResultItem>()

        for (book in data) {
            val bookName = book.name ?: continue
            book.chapters.forEachIndexed { chapIdx, verses ->
                verses.forEachIndexed { verseIdx, verseText ->
                    if (results.size >= 50) return results
                    if (verseText.contains(query, ignoreCase = true)) {
                        val cleanedText = verseText.replace(Regex("\\{(.*?)\\}"), "$1")
                        results.add(
                            SearchResultItem(
                                bookName = bookName,
                                chapter = chapIdx + 1,
                                verseNumber = verseIdx + 1,
                                snippetText = cleanedText
                            )
                        )
                    }
                }
            }
        }
        return results
    }

    class Factory(
        private val bibleRepository: BibleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MoreViewModel(bibleRepository) as T
        }
    }
}
