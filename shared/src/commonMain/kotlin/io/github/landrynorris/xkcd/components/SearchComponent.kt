package io.github.landrynorris.xkcd.components

import io.github.landrynorris.xkcd.model.XkcdModel
import io.github.landrynorris.xkcd.repositories.ComicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface SearchLogic {
    val state: StateFlow<SearchState>

    fun searchTextUpdated(text: String)
    fun onExpandedChanged(value: Boolean)
}

class SearchComponent(private val repository: ComicRepository): SearchLogic {
    override val state = MutableStateFlow(SearchState())
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun searchTextUpdated(text: String) {
        state.update { it.copy(searchText = text, isExpanded = true) }
        coroutineScope.launch {

            val searchedNumber = text.toIntOrNull()
            if(searchedNumber != null) {
                val comic = repository.getComicOrNull(searchedNumber)
                state.update { it.copy(results = listOfNotNull(comic)) }
            } else {
                val matching = repository.getComicsMatching(text, 5)
                state.update { it.copy(results = matching) }
            }
        }
    }

    override fun onExpandedChanged(value: Boolean) {
        state.update { it.copy(isExpanded = value) }
    }
}

data class SearchState(val searchText: String = "", val isExpanded: Boolean = false,
                       val results: List<XkcdModel> = listOf())
