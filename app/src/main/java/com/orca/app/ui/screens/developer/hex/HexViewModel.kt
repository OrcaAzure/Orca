package com.orca.app.ui.screens.developer.hex

import androidx.lifecycle.ViewModel
import com.orca.app.domain.developer.HexTool
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HexViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ToolUiState<String>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<String>> = _uiState.asStateFlow()

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()

    private val _mode = MutableStateFlow("Encode")
    val mode: StateFlow<String> = _mode.asStateFlow()

    fun onInputChange(value: String) {
        _input.value = value
    }

    fun onModeChange(value: String) {
        _mode.value = value
    }

    fun run() {
        val text = _input.value
        if (text.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter text to process")
            return
        }

        runCatching {
            if (_mode.value == "Encode") HexTool.encode(text) else HexTool.decode(text)
        }.onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Failed") }
    }
}
