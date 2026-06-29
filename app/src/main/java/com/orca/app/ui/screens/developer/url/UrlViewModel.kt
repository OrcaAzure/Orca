package com.orca.app.ui.screens.developer.url

import androidx.lifecycle.ViewModel
import com.orca.app.domain.developer.UrlTool
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UrlViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<ToolUiState<String>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<String>> = _uiState.asStateFlow()
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()
    private val _mode = MutableStateFlow("Encode")
    val mode: StateFlow<String> = _mode.asStateFlow()

    fun onInputChange(v: String) { _input.value = v }
    fun onModeChange(v: String) { _mode.value = v }

    fun run() {
        runCatching {
            if (_mode.value == "Encode") UrlTool.encode(_input.value) else UrlTool.decode(_input.value)
        }.onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Failed") }
    }
}
