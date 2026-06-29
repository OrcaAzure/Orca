package com.orca.app.ui.screens.network.ssl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orca.app.data.network.SslCertificateResult
import com.orca.app.ui.components.AsyncToolContent
import com.orca.app.ui.components.OrcaButton
import com.orca.app.ui.components.OrcaTextField
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold
import com.orca.app.ui.theme.OrcaError
import com.orca.app.ui.theme.OrcaSuccess
import com.orca.app.ui.theme.OrcaWarning

@Composable
fun SslScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SslViewModel = hiltViewModel(),
) {
    val host by viewModel.host.collectAsStateWithLifecycle()
    val port by viewModel.port.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ToolScaffold(title = "SSL Inspector", onBack = onBack, modifier = modifier) {
        Text(
            text = "Inspect TLS certificates — accepts self-signed certs",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        // Trust-all warning banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(OrcaWarning.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = "⚠ Inspection mode — certificate chain is NOT validated. Do not trust results for security decisions.",
                style = MaterialTheme.typography.bodySmall,
                color = OrcaWarning,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        OrcaTextField(
            value = host,
            onValueChange = viewModel::onHostChange,
            label = "Hostname",
            placeholder = "e.g. example.com",
            onImeAction = viewModel::inspect,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OrcaTextField(
            value = port,
            onValueChange = viewModel::onPortChange,
            label = "Port",
            placeholder = "443",
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrcaButton(text = "Inspect Certificate", onClick = viewModel::inspect)

        Spacer(modifier = Modifier.height(24.dp))

        AsyncToolContent(
            state = uiState,
            idleMessage = "Enter a host and tap Inspect",
            successContent = { result -> SslResultContent(result) },
        )
    }
}

@Composable
private fun SslResultContent(result: SslCertificateResult) {
    ResultCard {
        ResultRow("Host", "${result.host}:${result.port}")
        ResultRow("Status", if (result.isExpired) "EXPIRED" else "Valid",)
        ResultRow("Expires in", "${result.daysUntilExpiry} days")
        ResultRow("Subject", result.subject)
        ResultRow("Issuer", result.issuer)
        ResultRow("Valid from", result.validFrom)
        ResultRow("Valid to", result.validTo)
        ResultRow("Serial", result.serialNumber)
        ResultRow("Algorithm", result.signatureAlgorithm)
        ResultRow("Version", "v${result.version}")

        if (result.subjectAltNames.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Subject Alt Names",
                style = MaterialTheme.typography.labelMedium,
                color = if (result.isExpired) OrcaError else OrcaSuccess,
            )
            result.subjectAltNames.forEach { san ->
                Text(
                    text = "• $san",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
