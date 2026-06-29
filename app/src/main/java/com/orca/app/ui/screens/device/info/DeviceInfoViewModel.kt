package com.orca.app.ui.screens.device.info

import androidx.lifecycle.ViewModel
import com.orca.app.data.device.DeviceInfoResult
import com.orca.app.data.device.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DeviceInfoViewModel @Inject constructor(repository: DeviceRepository) : ViewModel() {
    private val _info = MutableStateFlow(repository.getDeviceInfo())
    val info: StateFlow<DeviceInfoResult> = _info.asStateFlow()
}
