package com.orca.app.ui.screens.developer.hash

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
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold

@Composable
fun HashScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HashViewModel = hiltViewModel(),
) {
    val input by viewModel.input.collectAsStateWithLifecycle()
    val algorithm by viewModel.algorithm.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "Hash", onBack = onBack, modifier = modifier) {
        Text(
            text = "Offline hashing — crack/compare hashes in CTF",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OrcaChipRow(
            options = viewModel.algorithms,
            selected = algorithm,
            onSelected = viewModel::onAlgorithmChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaTextField(
            value = input,
            onValueChange = viewModel::onInputChange,
            label = "Input",
            placeholder = "Text to hash",
            singleLine = false,
            onImeAction = viewModel::hash,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = "Hash", onClick = viewModel::hash)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter text and tap Hash",
            successContent = { result ->
                ResultCard {
                    ResultRow("Algorithm", result.algorithm)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.hash,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
        )
    }
}
