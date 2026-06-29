package com.orca.app.ui.screens.security

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
import com.orca.app.ui.theme.SecurityAccent

private data class SecurityTool(val emoji: String, val title: String, val subtitle: String, val route: String)

private val tools = listOf(
    SecurityTool("🔑", "Password Generator", "Secure random passwords — offline", Routes.SECURITY_PASSWORD),
    SecurityTool("🔄", "ROT Cipher", "Caesar cipher encode/decode", Routes.SECURITY_ROT),
    SecurityTool("⊕", "XOR Cipher", "XOR with key — hex output", Routes.SECURITY_XOR),
    SecurityTool("✍️", "HMAC", "HMAC-SHA256 & SHA-512", Routes.SECURITY_HMAC),
)

@Composable
fun SecurityScreen(onBack: () -> Unit, onToolClick: (String) -> Unit, modifier: Modifier = Modifier) {
    ToolScaffold(title = "Security", onBack = onBack, modifier = modifier) {
        Text(
            text = "Crypto & encoding tools for CTF",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tools.forEach { tool ->
                CategoryCard(
                    emoji = tool.emoji,
                    title = tool.title,
                    subtitle = tool.subtitle,
                    accentColor = SecurityAccent,
                    onClick = { onToolClick(tool.route) },
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
