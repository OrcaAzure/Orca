package com.orca.app.ui.screens.device.network

import androidx.lifecycle.ViewModel
import com.orca.app.data.device.DeviceRepository
import com.orca.app.data.device.NetworkInfoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DeviceNetworkViewModel @Inject constructor(repository: DeviceRepository) : ViewModel() {
    private val _info = MutableStateFlow(repository.getNetworkInfo())
    val info: StateFlow<NetworkInfoResult> = _info.asStateFlow()
}
