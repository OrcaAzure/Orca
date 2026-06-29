package com.orca.app.ui.screens.developer

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
import com.orca.app.ui.theme.DeveloperAccent

private data class DeveloperTool(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val route: String,
)

private val developerTools = listOf(
    DeveloperTool("🔤", "Base64", "Encode & decode — offline", Routes.DEVELOPER_BASE64),
    DeveloperTool("🔢", "Hex", "Encode & decode hex — offline", Routes.DEVELOPER_HEX),
    DeveloperTool("#️⃣", "Hash", "MD5, SHA-1, SHA-256, SHA-512", Routes.DEVELOPER_HASH),
    DeveloperTool("🎫", "JWT Decoder", "Decode token header & payload", Routes.DEVELOPER_JWT),
    DeveloperTool("{ }", "JSON Formatter", "Pretty-print & minify JSON", Routes.DEVELOPER_JSON),
    DeveloperTool("🔗", "URL Encode", "URL encode & decode", Routes.DEVELOPER_URL),
)

@Composable
fun DeveloperScreen(
    onBack: () -> Unit,
    onToolClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ToolScaffold(title = "Developer", onBack = onBack, modifier = modifier) {
        Text(
            text = "CTF encoding & crypto helpers",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            developerTools.forEach { tool ->
                CategoryCard(
                    emoji = tool.emoji,
                    title = tool.title,
                    subtitle = tool.subtitle,
                    accentColor = DeveloperAccent,
                    onClick = { onToolClick(tool.route) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
