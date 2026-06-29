package com.orca.app.ui.screens.developer.hash

import androidx.lifecycle.ViewModel
import com.orca.app.domain.developer.HashAlgorithm
import com.orca.app.domain.developer.HashTool
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HashResult(
    val algorithm: String,
    val hash: String,
)

@HiltViewModel
class HashViewModel @Inject constructor() : ViewModel() {

    val algorithms = HashAlgorithm.entries.map { it.label }

    private val _uiState = MutableStateFlow<ToolUiState<HashResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<HashResult>> = _uiState.asStateFlow()

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()

    private val _algorithm = MutableStateFlow(HashAlgorithm.SHA256.label)
    val algorithm: StateFlow<String> = _algorithm.asStateFlow()

    fun onInputChange(value: String) {
        _input.value = value
    }

    fun onAlgorithmChange(value: String) {
        _algorithm.value = value
    }

    fun hash() {
        val text = _input.value
        if (text.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter text to hash")
            return
        }

        val algo = HashAlgorithm.entries.find { it.label == _algorithm.value } ?: HashAlgorithm.SHA256

        runCatching {
            HashResult(algo.label, HashTool.hash(text, algo))
        }.onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Failed") }
    }
}
