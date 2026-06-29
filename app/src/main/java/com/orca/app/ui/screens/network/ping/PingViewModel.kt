package com.orca.app.ui.screens.network.ping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orca.app.data.network.PingRepository
import com.orca.app.data.network.PingResult
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PingViewModel @Inject constructor(
    private val repository: PingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ToolUiState<PingResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<PingResult>> = _uiState.asStateFlow()

    private val _host = MutableStateFlow("")
    val host: StateFlow<String> = _host.asStateFlow()

    fun onHostChange(value: String) {
        _host.value = value
    }

    fun ping() {
        val target = _host.value.trim()
        if (target.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter a hostname or IP address")
            return
        }

        viewModelScope.launch {
            _uiState.value = ToolUiState.Loading
            repository.ping(target)
                .onSuccess { _uiState.value = ToolUiState.Success(it) }
                .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Ping failed") }
        }
    }
}
