package com.orca.app.ui.screens.network.ssl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orca.app.data.network.SslCertificateResult
import com.orca.app.data.network.SslRepository
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SslViewModel @Inject constructor(
    private val repository: SslRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ToolUiState<SslCertificateResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<SslCertificateResult>> = _uiState.asStateFlow()

    private val _host = MutableStateFlow("")
    val host: StateFlow<String> = _host.asStateFlow()

    private val _port = MutableStateFlow("443")
    val port: StateFlow<String> = _port.asStateFlow()

    fun onHostChange(value: String) {
        _host.value = value
    }

    fun onPortChange(value: String) {
        _port.value = value.filter { it.isDigit() }.take(5)
    }

    fun inspect() {
        val target = _host.value.trim()
        val portNum = _port.value.toIntOrNull() ?: 443

        if (target.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter a hostname")
            return
        }

        viewModelScope.launch {
            _uiState.value = ToolUiState.Loading
            repository.inspect(target, portNum)
                .onSuccess { _uiState.value = ToolUiState.Success(it) }
                .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Inspection failed") }
        }
    }
}
