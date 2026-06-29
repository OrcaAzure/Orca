package com.orca.app.ui.screens.network.whois

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
import com.orca.app.data.network.WhoisResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold

@Composable
fun WhoisScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WhoisViewModel = hiltViewModel(),
) {
    val domain by viewModel.domain.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "WHOIS", onBack = onBack, modifier = modifier) {
        Text(
            text = "Domain registration lookup via WHOIS",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        OrcaTextField(
            value = domain,
            onValueChange = viewModel::onDomainChange,
            label = "Domain",
            placeholder = "e.g. example.com",
            onImeAction = viewModel::lookup,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = "Lookup", onClick = viewModel::lookup)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter a domain and tap Lookup",
            successContent = { result -> WhoisResultContent(result) },
        )
    }
}

@Composable
private fun WhoisResultContent(result: WhoisResult) {
    ResultCard {
        ResultRow("Domain", result.query)
        ResultRow("Server", result.server)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = result.response,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
