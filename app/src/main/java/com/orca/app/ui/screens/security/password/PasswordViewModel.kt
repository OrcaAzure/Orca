package com.orca.app.ui.screens.security.password

import androidx.lifecycle.ViewModel
import com.orca.app.domain.security.PasswordGenerator
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<ToolUiState<String>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<String>> = _uiState.asStateFlow()
    private val _length = MutableStateFlow("16")
    val length: StateFlow<String> = _length.asStateFlow()
    private val _upper = MutableStateFlow(true)
    val upper: StateFlow<Boolean> = _upper.asStateFlow()
    private val _lower = MutableStateFlow(true)
    val lower: StateFlow<Boolean> = _lower.asStateFlow()
    private val _digits = MutableStateFlow(true)
    val digits: StateFlow<Boolean> = _digits.asStateFlow()
    private val _symbols = MutableStateFlow(true)
    val symbols: StateFlow<Boolean> = _symbols.asStateFlow()

    fun onLengthChange(v: String) { _length.value = v.filter { it.isDigit() }.take(3) }
    fun onUpperChange(v: Boolean) { _upper.value = v }
    fun onLowerChange(v: Boolean) { _lower.value = v }
    fun onDigitsChange(v: Boolean) { _digits.value = v }
    fun onSymbolsChange(v: Boolean) { _symbols.value = v }

    fun generate() {
        runCatching {
            PasswordGenerator.generate(
                length = _length.value.toIntOrNull() ?: 16,
                includeUpper = _upper.value,
                includeLower = _lower.value,
                includeDigits = _digits.value,
                includeSymbols = _symbols.value,
            )
        }.onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Failed") }
    }
}
