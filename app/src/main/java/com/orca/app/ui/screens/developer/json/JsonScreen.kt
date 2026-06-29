package com.orca.app.ui.screens.developer.json

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
fun JsonScreen(onBack: () -> Unit, viewModel: JsonViewModel = hiltViewModel()) {
    val input by viewModel.input.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "JSON Formatter", onBack = onBack) {
        Text("Pretty-print or minify JSON — offline", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
        OrcaChipRow(listOf("Format", "Minify"), mode, viewModel::onModeChange)
        Spacer(Modifier.height(16.dp))
        OrcaTextField(
            value = input,
            onValueChange = viewModel::onInputChange,
            label = "JSON",
            placeholder = "{\"key\":\"value\"}",
            singleLine = false,
        )
        Spacer(Modifier.height(16.dp))
        OrcaButton(text = mode, onClick = viewModel::run)
        Spacer(Modifier.height(24.dp))
        AsyncToolContent(state = uiState, idleMessage = "Paste JSON and tap $mode") { r ->
            ResultCard { Text(r, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
