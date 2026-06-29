package com.orca.app.ui.screens.network.portscanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orca.app.data.network.PortScanResult
import com.orca.app.data.network.PortScannerRepository
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortScannerViewModel @Inject constructor(
    private val repository: PortScannerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ToolUiState<PortScanResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<PortScanResult>> = _uiState.asStateFlow()

    private val _host = MutableStateFlow("")
    val host: StateFlow<String> = _host.asStateFlow()

    private val _customPorts = MutableStateFlow("")
    val customPorts: StateFlow<String> = _customPorts.asStateFlow()

    private val _useCommonPorts = MutableStateFlow(true)
    val useCommonPorts: StateFlow<Boolean> = _useCommonPorts.asStateFlow()

    private val _timeoutMs = MutableStateFlow(2000)
    val timeoutMs: StateFlow<Int> = _timeoutMs.asStateFlow()

    fun onHostChange(value: String) {
        _host.value = value
    }

    fun onCustomPortsChange(value: String) {
        _customPorts.value = value
    }

    fun onUseCommonPortsChange(value: Boolean) {
        _useCommonPorts.value = value
    }

    fun onTimeoutChange(value: Int) {
        _timeoutMs.value = value
    }

    fun scan() {
        val target = _host.value.trim()
        if (target.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter a hostname or IP")
            return
        }

        val ports = if (_useCommonPorts.value) {
            repository.commonPorts
        } else {
            repository.parsePorts(_customPorts.value).ifEmpty {
                _uiState.value = ToolUiState.Error("Enter ports like: 80,443,8080")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = ToolUiState.Loading
            repository.scan(target, ports, timeoutMs = _timeoutMs.value)
                .onSuccess { _uiState.value = ToolUiState.Success(it) }
                .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Scan failed") }
        }
    }
}
