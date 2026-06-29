package com.orca.app.ui.screens.network.cidr

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
import com.orca.app.domain.network.CidrResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold

@Composable
fun CidrScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CidrViewModel = hiltViewModel(),
) {
    val cidr by viewModel.cidr.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "CIDR Calculator", onBack = onBack, modifier = modifier) {
        Text(
            text = "Works offline — no network required",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        OrcaTextField(
            value = cidr,
            onValueChange = viewModel::onCidrChange,
            label = "CIDR",
            placeholder = "e.g. 192.168.1.0/24",
            onImeAction = viewModel::calculate,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = "Calculate", onClick = viewModel::calculate)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter CIDR notation and tap Calculate",
            successContent = { result -> CidrResultContent(result) },
        )
    }
}

@Composable
private fun CidrResultContent(result: CidrResult) {
    ResultCard {
        ResultRow("CIDR", result.cidr)
        ResultRow("Network", result.networkAddress)
        ResultRow("Broadcast", result.broadcastAddress)
        ResultRow("First host", result.firstHost ?: when (result.prefixLength) {
            0    -> "N/A (entire Internet)"
            else -> "N/A"
        })
        ResultRow("Last host", result.lastHost ?: when (result.prefixLength) {
            0    -> "N/A (entire Internet)"
            else -> "N/A"
        })
        // Add a note for point-to-point and host routes
        if (result.prefixLength >= 31) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (result.prefixLength == 32) "Host route — single address"
                       else "Point-to-point link — no broadcast",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        ResultRow("Subnet mask", result.subnetMask)
        ResultRow("Wildcard mask", result.wildcardMask)
        ResultRow("Prefix", "/${result.prefixLength}")
        ResultRow("Total addresses", result.totalAddresses.toString())
        ResultRow("Usable hosts", result.usableHosts.toString())
        ResultRow("Class", result.ipClass)
    }
}
