package com.orca.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orca.app.data.FavoritesRepository
import com.orca.app.domain.tools.OrcaTool
import com.orca.app.domain.tools.ToolRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val favorites: StateFlow<Set<String>> = favoritesRepository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val results: StateFlow<List<OrcaTool>> = _query
        .combine(favorites) { q, _ -> ToolRegistry.search(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ToolRegistry.allTools)

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun toggleFavorite(toolId: String) {
        viewModelScope.launch { favoritesRepository.toggle(toolId) }
    }
}
