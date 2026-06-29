package com.orca.app.ui.screens.network.httpheaders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orca.app.data.network.HttpHeadersRepository
import com.orca.app.data.network.HttpHeadersResult
import com.orca.app.ui.common.CancellableJob
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HttpHeadersViewModel @Inject constructor(
    private val repository: HttpHeadersRepository,
) : ViewModel() {

    private val requestJob = CancellableJob()

    private val _uiState = MutableStateFlow<ToolUiState<HttpHeadersResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<HttpHeadersResult>> = _uiState.asStateFlow()

    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url.asStateFlow()

    fun onUrlChange(value: String) {
        _url.value = value
    }

    fun fetch() {
        val target = _url.value.trim()
        if (target.isBlank()) {
            _uiState.value = ToolUiState.Error("Enter a URL")
            return
        }

        requestJob.launch(viewModelScope, onCancel = { _uiState.value = ToolUiState.Idle }) {
            _uiState.value = ToolUiState.Loading
            repository.fetchHeaders(target)
                .onSuccess { _uiState.value = ToolUiState.Success(it) }
                .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Request failed") }
        }
    }
}
