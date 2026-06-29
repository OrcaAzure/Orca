package com.orca.app.ui.screens.network.dns

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
import com.orca.app.data.network.DnsResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaChipRow
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.common.ToolUiState
import com.orca.app.ui.components.ToolScaffold

@Composable
fun DnsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DnsViewModel = hiltViewModel(),
) {
    val host by viewModel.host.collectAsStateWithLifecycle()
    val recordType by viewModel.recordType.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "DNS Lookup", onBack = onBack, modifier = modifier) {
        Text(
            text = "Query A, AAAA, MX, TXT, CNAME, NS, SOA, PTR records",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OrcaTextField(
            value = host,
            onValueChange = viewModel::onHostChange,
            label = "Hostname / IP",
            placeholder = "e.g. example.com or 8.8.8.8 for PTR",
            onImeAction = viewModel::lookup,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Record type",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OrcaChipRow(
            options = viewModel.recordTypes,
            selected = recordType,
            onSelected = viewModel::onRecordTypeChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(
            text = "Lookup",
            onClick = viewModel::lookup,
            enabled = uiState !is ToolUiState.Loading,
        )

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Select record type and tap Lookup",
            successContent = { result -> DnsResultContent(result) },
        )
    }
}

@Composable
private fun DnsResultContent(result: DnsResult) {
    ResultCard {
        ResultRow("Query", result.host)
        ResultRow("Type", result.recordType)
        ResultRow("Records", "${result.records.size} found")

        Spacer(modifier = Modifier.height(8.dp))

        result.records.forEach { record ->
            val ttl = record.ttl?.let { " (TTL $it)" } ?: ""
            ResultRow(record.type, record.value + ttl)
        }
    }
}
