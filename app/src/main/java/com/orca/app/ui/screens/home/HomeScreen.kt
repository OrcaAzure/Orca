package com.orca.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orca.app.navigation.Routes
import com.orca.app.ui.components.CategoryCard
import com.orca.app.ui.components.OrcaSearchBar
import com.orca.app.ui.theme.DeveloperAccent
import com.orca.app.ui.theme.DeviceAccent
import com.orca.app.ui.theme.NetworkAccent
import com.orca.app.ui.theme.SecurityAccent

private data class Category(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val route: String,
    val accentColor: androidx.compose.ui.graphics.Color,
)

private val categories = listOf(
    Category("🌐", "Network", "Recon, DNS, ports & more", Routes.NETWORK, NetworkAccent),
    Category("💻", "Developer", "Base64, Hash, JSON & more", Routes.DEVELOPER, DeveloperAccent),
    Category("🔐", "Security", "Password, ROT, XOR & HMAC", Routes.SECURITY, SecurityAccent),
    Category("📱", "Device", "System info & diagnostics", Routes.DEVICE, DeviceAccent),
)

@Composable
fun HomeScreen(
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 16.dp),
    ) {
        Text(
            text = "Welcome to Orca",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your network & developer toolkit",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(28.dp))

        OrcaSearchBar(onClick = onSearchClick)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Categories",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            categories.forEach { category ->
                CategoryCard(
                    emoji = category.emoji,
                    title = category.title,
                    subtitle = category.subtitle,
                    accentColor = category.accentColor,
                    onClick = { onCategoryClick(category.route) },
                )
            }
        }
    }
}
