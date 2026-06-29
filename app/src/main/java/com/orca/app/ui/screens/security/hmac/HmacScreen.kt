package com.orca.app.ui.screens.security.hmac

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
fun HmacScreen(onBack: () -> Unit, viewModel: HmacViewModel = hiltViewModel()) {
    val input by viewModel.input.collectAsStateWithLifecycle()
    val key by viewModel.key.collectAsStateWithLifecycle()
    val algorithm by viewModel.algorithm.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "HMAC", onBack = onBack) {
        Text("Compute HMAC signatures — offline", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
        OrcaChipRow(viewModel.algorithms, algorithm, viewModel::onAlgorithmChange)
        Spacer(Modifier.height(16.dp))
        OrcaTextField(value = input, onValueChange = viewModel::onInputChange, label = "Message", singleLine = false)
        Spacer(Modifier.height(12.dp))
        OrcaTextField(
            value = key,
            onValueChange = viewModel::onKeyChange,
            label = "Secret key",
            onImeAction = viewModel::compute,
        )
        Spacer(Modifier.height(16.dp))
        OrcaButton(text = "Compute HMAC", onClick = viewModel::compute)
        Spacer(Modifier.height(24.dp))
        AsyncToolContent(state = uiState, idleMessage = "Enter message and key") { r ->
            ResultCard {
                ResultRow("Algorithm", r.algorithm)
                Spacer(Modifier.height(8.dp))
                Text(r.value, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
