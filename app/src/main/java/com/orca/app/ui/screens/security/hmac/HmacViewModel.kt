package com.orca.app.ui.screens.security.hmac

import androidx.lifecycle.ViewModel
import com.orca.app.domain.security.HmacAlgorithm
import com.orca.app.domain.security.HmacTool
import com.orca.app.ui.common.ToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HmacResult(val algorithm: String, val value: String)

@HiltViewModel
class HmacViewModel @Inject constructor() : ViewModel() {
    val algorithms = HmacAlgorithm.entries.map { it.label }
    private val _uiState = MutableStateFlow<ToolUiState<HmacResult>>(ToolUiState.Idle)
    val uiState: StateFlow<ToolUiState<HmacResult>> = _uiState.asStateFlow()
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()
    private val _key = MutableStateFlow("")
    val key: StateFlow<String> = _key.asStateFlow()
    private val _algorithm = MutableStateFlow(HmacAlgorithm.SHA256.label)
    val algorithm: StateFlow<String> = _algorithm.asStateFlow()

    fun onInputChange(v: String) { _input.value = v }
    fun onKeyChange(v: String) { _key.value = v }
    fun onAlgorithmChange(v: String) { _algorithm.value = v }

    fun compute() {
        val algo = HmacAlgorithm.entries.find { it.label == _algorithm.value } ?: HmacAlgorithm.SHA256
        runCatching {
            HmacResult(algo.label, HmacTool.compute(_input.value, _key.value, algo))
        }.onSuccess { _uiState.value = ToolUiState.Success(it) }
            .onFailure { _uiState.value = ToolUiState.Error(it.message ?: "Failed") }
    }
}
