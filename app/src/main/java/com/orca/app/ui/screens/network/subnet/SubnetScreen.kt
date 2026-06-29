package com.orca.app.ui.screens.network.subnet

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
import com.orca.app.domain.network.SubnetResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold

@Composable
fun SubnetScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubnetViewModel = hiltViewModel(),
) {
    val ipAddress by viewModel.ipAddress.collectAsStateWithLifecycle()
    val subnetMask by viewModel.subnetMask.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "Subnet Calculator", onBack = onBack, modifier = modifier) {
        Text(
            text = "Works offline — no network required",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        OrcaTextField(
            value = ipAddress,
            onValueChange = viewModel::onIpChange,
            label = "IP Address",
            placeholder = "e.g. 192.168.1.45",
        )

        Spacer(modifier = Modifier.height(12.dp))

        OrcaTextField(
            value = subnetMask,
            onValueChange = viewModel::onMaskChange,
            label = "Subnet Mask",
            placeholder = "e.g. 255.255.255.0",
            onImeAction = viewModel::calculate,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = "Calculate", onClick = viewModel::calculate)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter IP and mask, then tap Calculate",
            successContent = { result -> SubnetResultContent(result) },
        )
    }
}

@Composable
private fun SubnetResultContent(result: SubnetResult) {
    ResultCard {
        ResultRow("IP address", result.ipAddress)
        ResultRow("Subnet mask", result.subnetMask)
        ResultRow("Prefix", "/${result.prefixLength}")
        ResultRow("Network", result.networkAddress)
        ResultRow("Broadcast", result.broadcastAddress)
        result.firstHost?.let { ResultRow("First host", it) }
        result.lastHost?.let { ResultRow("Last host", it) }
        ResultRow("Wildcard mask", result.wildcardMask)
        ResultRow("Total addresses", result.totalAddresses.toString())
        ResultRow("Usable hosts", result.usableHosts.toString())

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Binary",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        ResultRow("IP", result.ipBinary)
        ResultRow("Mask", result.maskBinary)
        ResultRow("Network", result.networkBinary)
    }
}
