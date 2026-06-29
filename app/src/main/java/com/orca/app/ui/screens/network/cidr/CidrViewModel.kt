package com.orca.app.ui.screens.network.cidr

import androidx.lifecycle.ViewModel
import com.orca.app.domain.network.CidrCalculator
import com.orca.app.domain.network.CidrResult
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CidrViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ToolUiState<CidrResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<CidrResult>> = _uiState.asStateFlow()

    private val _cidr = MutableStateFlow("")
    val cidr: StateFlow<String> = _cidr.asStateFlow()

    fun onCidrChange(value: String) {
        _cidr.value = value
    }

    fun calculate() {
        val input = _cidr.value.trim()
        if (input.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter a CIDR notation")
            return
        }

        runCatching { CidrCalculator.calculate(input) }
            .onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Invalid CIDR") }
    }
}
