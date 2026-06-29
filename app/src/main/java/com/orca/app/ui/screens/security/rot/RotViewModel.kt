package com.orca.app.ui.screens.security.rot

import androidx.lifecycle.ViewModel
import com.orca.app.domain.security.RotCipher
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RotViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<ToolUiState<String>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<String>> = _uiState.asStateFlow()
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()
    private val _shift = MutableStateFlow("13")
    val shift: StateFlow<String> = _shift.asStateFlow()
    private val _mode = MutableStateFlow("Encode")
    val mode: StateFlow<String> = _mode.asStateFlow()

    fun onInputChange(v: String) { _input.value = v }
    fun onShiftChange(v: String) { _shift.value = v.filter { it.isDigit() || it == '-' }.take(4) }
    fun onModeChange(v: String) { _mode.value = v }

    fun run() {
        val text = _input.value
        if (text.isBlank()) { _uiState.value = ToolUiState.Error("Enter text"); return }
        val shiftVal = _shift.value.toIntOrNull() ?: 13
        val actualShift = if (_mode.value == "Decode") -shiftVal else shiftVal
        _uiState.value = ToolUiState.Success(RotCipher.transform(text, actualShift))
    }
}
