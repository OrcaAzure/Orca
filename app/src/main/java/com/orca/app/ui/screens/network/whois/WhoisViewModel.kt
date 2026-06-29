package com.orca.app.ui.screens.network.whois

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orca.app.data.network.WhoisRepository
import com.orca.app.data.network.WhoisResult
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhoisViewModel @Inject constructor(
    private val repository: WhoisRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ToolUiState<WhoisResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<WhoisResult>> = _uiState.asStateFlow()

    private val _domain = MutableStateFlow("")
    val domain: StateFlow<String> = _domain.asStateFlow()

    fun onDomainChange(value: String) {
        _domain.value = value
    }

    fun lookup() {
        val target = _domain.value.trim()
        if (target.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter a domain name")
            return
        }

        viewModelScope.launch {
            _uiState.value = ToolUiState.Loading
            repository.lookup(target)
                .onSuccess { _uiState.value = ToolUiState.Success(it) }
                .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Lookup failed") }
        }
    }
}
