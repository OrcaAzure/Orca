package com.orca.app.ui.screens.developer.base64

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
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaChipRow
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ToolScaffold

@Composable
fun Base64Screen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: Base64ViewModel = hiltViewModel(),
) {
    val input by viewModel.input.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "Base64", onBack = onBack, modifier = modifier) {
        Text(
            text = "Offline encode/decode",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OrcaChipRow(
            options = listOf("Encode", "Decode"),
            selected = mode,
            onSelected = viewModel::onModeChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaTextField(
            value = input,
            onValueChange = viewModel::onInputChange,
            label = "Input",
            placeholder = "Text or Base64 string",
            singleLine = false,
            onImeAction = viewModel::run,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = mode, onClick = viewModel::run)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter input and tap $mode",
            successContent = { output ->
                ResultCard {
                    Text(
                        text = output,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
        )
    }
}
