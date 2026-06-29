package com.orca.app.ui.screens.network.subnet

import androidx.lifecycle.ViewModel
import com.orca.app.domain.network.SubnetCalculator
import com.orca.app.domain.network.SubnetResult
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SubnetViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ToolUiState<SubnetResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<SubnetResult>> = _uiState.asStateFlow()

    private val _ipAddress = MutableStateFlow("")
    val ipAddress: StateFlow<String> = _ipAddress.asStateFlow()

    private val _subnetMask = MutableStateFlow("")
    val subnetMask: StateFlow<String> = _subnetMask.asStateFlow()

    fun onIpChange(value: String) {
        _ipAddress.value = value
    }

    fun onMaskChange(value: String) {
        _subnetMask.value = value
    }

    fun calculate() {
        val ip = _ipAddress.value.trim()
        val mask = _subnetMask.value.trim()

        if (ip.isBlank() || mask.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter both IP address and subnet mask")
            return
        }

        runCatching { SubnetCalculator.calculate(ip, mask) }
            .onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Invalid input") }
    }
}
