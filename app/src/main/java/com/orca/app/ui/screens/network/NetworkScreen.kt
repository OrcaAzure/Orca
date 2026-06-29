package com.orca.app.ui.screens.network

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orca.app.navigation.Routes
import com.orca.app.ui.components.CategoryCard
import com.orca.app.ui.components.ToolScaffold
import com.orca.app.ui.theme.NetworkAccent

private data class NetworkTool(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val route: String,
)

private val networkTools = listOf(
    NetworkTool("📡", "Ping", "Test host reachability", Routes.NETWORK_PING),
    NetworkTool("🔍", "DNS Lookup", "A, AAAA, MX, TXT, CNAME, NS, PTR", Routes.NETWORK_DNS),
    NetworkTool("📋", "HTTP Headers", "Inspect response headers", Routes.NETWORK_HTTP_HEADERS),
    NetworkTool("🔌", "Port Scanner", "TCP scan — common CTF ports", Routes.NETWORK_PORT_SCANNER),
    NetworkTool("🔒", "SSL Inspector", "Certificate & SAN inspection", Routes.NETWORK_SSL),
    NetworkTool("📇", "WHOIS", "Domain registration lookup", Routes.NETWORK_WHOIS),
    NetworkTool("🧮", "CIDR Calculator", "Parse CIDR — offline", Routes.NETWORK_CIDR),
    NetworkTool("🔢", "Subnet Calculator", "IP + mask — offline", Routes.NETWORK_SUBNET),
)

@Composable
fun NetworkScreen(
    onBack: () -> Unit,
    onToolClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ToolScaffold(
        title = "Network",
        onBack = onBack,
        modifier = modifier,
    ) {
        Text(
            text = "Recon & networking tools for CTF",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            networkTools.forEach { tool ->
                CategoryCard(
                    emoji = tool.emoji,
                    title = tool.title,
                    subtitle = tool.subtitle,
                    accentColor = NetworkAccent,
                    onClick = { onToolClick(tool.route) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
