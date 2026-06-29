package com.orca.app.ui.screens.security.password

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ToolScaffold

@Composable
fun PasswordScreen(onBack: () -> Unit, viewModel: PasswordViewModel = hiltViewModel()) {
    val length by viewModel.length.collectAsStateWithLifecycle()
    val upper by viewModel.upper.collectAsStateWithLifecycle()
    val lower by viewModel.lower.collectAsStateWithLifecycle()
    val digits by viewModel.digits.collectAsStateWithLifecycle()
    val symbols by viewModel.symbols.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "Password Generator", onBack = onBack) {
        Text("Offline — generates cryptographically random passwords", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
        OrcaTextField(
            value = length,
            onValueChange = viewModel::onLengthChange,
            label = "Length",
            placeholder = "16",
            onImeAction = viewModel::generate,
        )
        Spacer(Modifier.height(8.dp))
        listOf(
            Triple("Uppercase (A-Z)", upper, viewModel::onUpperChange),
            Triple("Lowercase (a-z)", lower, viewModel::onLowerChange),
            Triple("Digits (0-9)", digits, viewModel::onDigitsChange),
            Triple("Symbols", symbols, viewModel::onSymbolsChange),
        ).forEach { (label, checked, onChange) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked, onChange); Text(label, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(16.dp))
        OrcaButton(text = "Generate", onClick = viewModel::generate)
        Spacer(Modifier.height(24.dp))
        AsyncToolContent(state = uiState, idleMessage = "Tap Generate") { pwd ->
            ResultCard { Text(pwd, style = MaterialTheme.typography.bodyLarge) }
        }
    }
}
