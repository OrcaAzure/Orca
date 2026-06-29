package com.orca.app.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orca.app.data.FavoritesRepository
import com.orca.app.domain.tools.OrcaTool
import com.orca.app.domain.tools.ToolRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    val favoriteTools: StateFlow<List<OrcaTool>> = favoritesRepository.favoritesOrdered
        .map { ids -> ids.mapNotNull { ToolRegistry.findById(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun remove(toolId: String) {
        viewModelScope.launch { favoritesRepository.remove(toolId) }
    }
}
