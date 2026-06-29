package com.orca.app.ui.screens.network.httpheaders

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
import com.orca.app.data.network.HttpHeadersResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold

@Composable
fun HttpHeadersScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HttpHeadersViewModel = hiltViewModel(),
) {
    val url by viewModel.url.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "HTTP Headers", onBack = onBack, modifier = modifier) {
        Text(
            text = "Fetch and inspect HTTP response headers",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        OrcaTextField(
            value = url,
            onValueChange = viewModel::onUrlChange,
            label = "URL",
            placeholder = "e.g. google.com or https://example.com",
            onImeAction = viewModel::fetch,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = "Fetch Headers", onClick = viewModel::fetch)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter a URL and tap Fetch Headers",
            successContent = { result -> HttpHeadersResultContent(result) },
        )
    }
}

@Composable
private fun HttpHeadersResultContent(result: HttpHeadersResult) {
    ResultCard {
        ResultRow("URL", result.url)
        ResultRow("Status", "${result.statusCode} ${result.statusMessage}")
        ResultRow("Response time", "${result.responseTimeMs} ms")
        ResultRow("Headers", "${result.headers.size} found")

        if (result.headers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            result.headers.forEach { header ->
                ResultRow(header.name, header.value)
            }
        }
    }
}
