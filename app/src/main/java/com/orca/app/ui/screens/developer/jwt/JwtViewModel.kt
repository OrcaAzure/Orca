package com.orca.app.ui.screens.developer.jwt

import androidx.lifecycle.ViewModel
import com.orca.app.domain.developer.JwtDecodeResult
import com.orca.app.domain.developer.JwtTool
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class JwtViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ToolUiState<JwtDecodeResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<JwtDecodeResult>> = _uiState.asStateFlow()

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token.asStateFlow()

    fun onTokenChange(value: String) {
        _token.value = value
    }

    fun decode() {
        val text = _token.value.trim()
        if (text.isBlank()) {
            _uiState.value = ToolUiState.Error("Paste a JWT token")
            return
        }

        runCatching { JwtTool.decode(text) }
            .onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Invalid JWT") }
    }
}
