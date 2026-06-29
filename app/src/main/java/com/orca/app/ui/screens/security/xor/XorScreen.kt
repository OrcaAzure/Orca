package com.orca.app.ui.screens.security.xor

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orca.app.ui.components.*

@Composable
fun XorScreen(onBack: () -> Unit, viewModel: XorViewModel = hiltViewModel()) {
    val input by viewModel.input.collectAsStateWithLifecycle()
    val key by viewModel.key.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "XOR Cipher", onBack = onBack) {
        Text("Encode outputs hex; decode expects hex input", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
        OrcaChipRow(listOf("Encode", "Decode"), mode, viewModel::onModeChange)
        Spacer(Modifier.height(16.dp))
        OrcaTextField(
            value = input,
            onValueChange = viewModel::onInputChange,
            label = if (mode == "Encode") "Plaintext" else "Hex",
            singleLine = false,
        )
        Spacer(Modifier.height(12.dp))
        OrcaTextField(
            value = key,
            onValueChange = viewModel::onKeyChange,
            label = "Key",
            placeholder = "XOR key",
            onImeAction = viewModel::run,
        )
        Spacer(Modifier.height(16.dp))
        OrcaButton(text = mode, onClick = viewModel::run)
        Spacer(Modifier.height(24.dp))
        AsyncToolContent(state = uiState, idleMessage = "Enter input and tap $mode") { r ->
            ResultCard { Text(r, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
