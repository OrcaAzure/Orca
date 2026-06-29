package com.orca.app.ui.screens.device.info

import androidx.compose.foundation.layout.padding
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
fun DeviceInfoScreen(onBack: () -> Unit, viewModel: DeviceInfoViewModel = hiltViewModel()) {
    val info by viewModel.info.collectAsStateWithLifecycle()
    ToolScaffold(title = "Device Info", onBack = onBack) {
        ResultCard {
            ResultRow("Manufacturer", info.manufacturer)
            ResultRow("Model", info.model)
            ResultRow("Device", info.device)
            ResultRow("Android", "${info.androidVersion} (API ${info.apiLevel})")
            ResultRow("Build ID", info.buildId)
            ResultRow("ABIs", info.abis)
            ResultRow("Android ID", info.androidId)
        }
    }
}
