package com.orca.app.ui.screens.device.network

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold

@Composable
fun DeviceNetworkScreen(onBack: () -> Unit, viewModel: DeviceNetworkViewModel = hiltViewModel()) {
    val info by viewModel.info.collectAsStateWithLifecycle()
    ToolScaffold(title = "Network Info", onBack = onBack) {
        ResultCard {
            ResultRow("Connection", info.connectionType)
            ResultRow("Internet", if (info.isConnected) "Connected" else "Disconnected")
            ResultRow("VPN", if (info.isVpn) "Active" else "Inactive")
            Spacer(Modifier.height(8.dp))
            if (info.localAddresses.isEmpty()) {
                ResultRow("Local IPs", "None found")
            } else {
                info.localAddresses.forEach { ResultRow("Local IP", it) }
            }
        }
    }
}
