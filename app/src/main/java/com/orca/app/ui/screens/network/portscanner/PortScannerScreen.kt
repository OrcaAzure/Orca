package com.orca.app.ui.screens.network.portscanner

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
import com.orca.app.data.network.PortScanResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold
import com.orca.app.ui.theme.OrcaSuccess

@Composable
fun PortScannerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortScannerViewModel = hiltViewModel(),
) {
    val host by viewModel.host.collectAsStateWithLifecycle()
    val customPorts by viewModel.customPorts.collectAsStateWithLifecycle()
    val useCommonPorts by viewModel.useCommonPorts.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "Port Scanner", onBack = onBack, modifier = modifier) {
        Text(
            text = "TCP connect scan — common CTF ports",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        OrcaTextField(
            value = host,
            onValueChange = viewModel::onHostChange,
            label = "Host",
            placeholder = "e.g. 10.10.10.5 or target.htb",
            onImeAction = viewModel::scan,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = useCommonPorts,
                onCheckedChange = viewModel::onUseCommonPortsChange,
            )
            Text(
                text = "Scan common ports (30 CTF ports)",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (!useCommonPorts) {
            Spacer(modifier = Modifier.height(8.dp))
            OrcaTextField(
                value = customPorts,
                onValueChange = viewModel::onCustomPortsChange,
                label = "Custom ports",
                placeholder = "80,443,8080,9000",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = "Scan", onClick = viewModel::scan)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter a host and tap Scan",
            successContent = { result -> PortScanResultContent(result) },
        )
    }
}

@Composable
private fun PortScanResultContent(result: PortScanResult) {
    ResultCard {
        ResultRow("Host", result.host)
        result.resolvedIp?.let { ResultRow("IP", it) }
        ResultRow("Scanned", "${result.scannedCount} ports")
        ResultRow("Open", "${result.openPorts.size} ports")
        ResultRow("Duration", "${result.durationMs} ms")

        if (result.openPorts.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No open ports found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Open ports",
                style = MaterialTheme.typography.labelMedium,
                color = OrcaSuccess,
            )
            Spacer(modifier = Modifier.height(4.dp))
            result.openPorts.forEach { port ->
                ResultRow("${port.port}", port.service)
            }
        }
    }
}
