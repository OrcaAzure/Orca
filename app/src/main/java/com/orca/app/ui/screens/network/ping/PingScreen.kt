package com.orca.app.ui.screens.network.ping

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
import com.orca.app.data.network.PingResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.common.ToolUiState
import com.orca.app.ui.components.ToolScaffold
import com.orca.app.ui.theme.OrcaSuccess

@Composable
fun PingScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PingViewModel = hiltViewModel(),
) {
    val host by viewModel.host.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "Ping", onBack = onBack, modifier = modifier) {
        Text(
            text = "Send ICMP echo requests to test reachability",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        OrcaTextField(
            value = host,
            onValueChange = viewModel::onHostChange,
            label = "Host",
            placeholder = "e.g. google.com or 8.8.8.8",
            onImeAction = viewModel::ping,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(
            text = "Ping",
            onClick = viewModel::ping,
            enabled = uiState !is ToolUiState.Loading,
        )

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter a host and tap Ping",
            successContent = { result -> PingResultContent(result) },
        )
    }
}

@Composable
private fun PingResultContent(result: PingResult) {
    ResultCard {
        ResultRow("Host", result.host)
        result.resolvedIp?.let { ResultRow("Resolved IP", it) }
        ResultRow("Status", if (result.reachable) "Reachable" else "Unreachable")
        ResultRow("Packets", "${result.packetsReceived}/${result.packetsSent} received")
        ResultRow("Packet loss", "${result.packetLossPercent}%")
        result.avgRttMs?.let { ResultRow("Avg RTT", "${it} ms") }
        result.minRttMs?.let { ResultRow("Min RTT", "${it} ms") }
        result.maxRttMs?.let { ResultRow("Max RTT", "${it} ms") }

        if (result.output.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Output",
                style = MaterialTheme.typography.labelMedium,
                color = OrcaSuccess,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = result.output,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
