package com.orca.app.ui.common

sealed interface ToolUiState<out T> {
    data object Idle : ToolUiState<Nothing>
    data object Loading : ToolUiState<Nothing>
    data class Success<T>(val data: T) : ToolUiState<T>
    data class Error(val message: String) : ToolUiState<Nothing>
}
