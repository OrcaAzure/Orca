package com.orca.app.ui.screens.network.dns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orca.app.data.network.DnsRecordType
import com.orca.app.data.network.DnsRepository
import com.orca.app.data.network.DnsResult
import com.orca.app.ui.common.CancellableJob
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DnsViewModel @Inject constructor(
    private val repository: DnsRepository,
) : ViewModel() {

    private val requestJob = CancellableJob()

    val recordTypes = DnsRecordType.entries.map { it.label }

    private val _uiState = MutableStateFlow<ToolUiState<DnsResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<DnsResult>> = _uiState.asStateFlow()

    private val _host = MutableStateFlow("")
    val host: StateFlow<String> = _host.asStateFlow()

    private val _recordType = MutableStateFlow(DnsRecordType.A.label)
    val recordType: StateFlow<String> = _recordType.asStateFlow()

    fun onHostChange(value: String) {
        _host.value = value
    }

    fun onRecordTypeChange(value: String) {
        _recordType.value = value
    }

    fun lookup() {
        val target = _host.value.trim()
        if (target.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter a hostname or IP")
            return
        }

        val type = DnsRecordType.entries.find { it.label == _recordType.value } ?: DnsRecordType.A

        requestJob.launch(viewModelScope, onCancel = { _uiState.value = ToolUiState.Idle }) {
            _uiState.value = ToolUiState.Loading
            repository.lookup(target, type)
                .onSuccess { _uiState.value = ToolUiState.Success(it) }
                .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Lookup failed") }
        }
    }
}
