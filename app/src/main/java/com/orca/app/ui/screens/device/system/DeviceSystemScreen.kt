package com.orca.app.ui.screens.device.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orca.app.ui.components.ResultCard
import com.orca.app.ui.components.ResultRow
import com.orca.app.ui.components.ToolScaffold

@Composable
fun DeviceSystemScreen(onBack: () -> Unit, viewModel: DeviceSystemViewModel = hiltViewModel()) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    ToolScaffold(title = "System Status", onBack = onBack) {
        ResultCard {
            ResultRow("Battery", "${status.batteryLevel}%")
            ResultRow("Status", status.batteryStatus)
            ResultRow("Charging", if (status.isCharging) "Yes" else "No")
            ResultRow("Storage total", status.storageTotalGb)
            ResultRow("Storage free", status.storageFreeGb)
            ResultRow("Storage used", "${status.storageUsedPercent}%")
            ResultRow("RAM total", status.ramTotalMb)
            ResultRow("RAM available", status.ramAvailableMb)
        }
    }
}
