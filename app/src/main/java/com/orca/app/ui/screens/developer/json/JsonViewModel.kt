package com.orca.app.ui.screens.developer.json

import androidx.lifecycle.ViewModel
import com.orca.app.domain.developer.JsonTool
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class JsonViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<ToolUiState<String>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<String>> = _uiState.asStateFlow()
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()
    private val _mode = MutableStateFlow("Format")
    val mode: StateFlow<String> = _mode.asStateFlow()

    fun onInputChange(v: String) { _input.value = v }
    fun onModeChange(v: String) { _mode.value = v }

    fun run() {
        runCatching {
            if (_mode.value == "Format") JsonTool.format(_input.value) else JsonTool.minify(_input.value)
        }.onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Invalid JSON") }
    }
}
