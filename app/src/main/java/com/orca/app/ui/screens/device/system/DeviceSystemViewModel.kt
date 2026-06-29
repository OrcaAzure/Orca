package com.orca.app.ui.screens.device.system

import androidx.lifecycle.ViewModel
import com.orca.app.data.device.DeviceRepository
import com.orca.app.data.device.SystemStatusResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DeviceSystemViewModel @Inject constructor(repository: DeviceRepository) : ViewModel() {
    private val _status = MutableStateFlow(repository.getSystemStatus())
    val status: StateFlow<SystemStatusResult> = _status.asStateFlow()
}
