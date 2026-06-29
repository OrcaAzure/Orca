package com.orca.app.data.device

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.NetworkInterface
import javax.inject.Inject
import javax.inject.Singleton

data class DeviceInfoResult(
    val manufacturer: String,
    val model: String,
    val device: String,
    val androidVersion: String,
    val apiLevel: Int,
    val buildId: String,
    val abis: String,
    val androidId: String,
)

data class NetworkInfoResult(
    val connectionType: String,
    val isConnected: Boolean,
    val isVpn: Boolean,
    val localAddresses: List<String>,
)

data class SystemStatusResult(
    val batteryLevel: Int,
    val batteryStatus: String,
    val isCharging: Boolean,
    val storageTotalGb: String,
    val storageFreeGb: String,
    val storageUsedPercent: Int,
    val ramTotalMb: String,
    val ramAvailableMb: String,
)

@Singleton
class DeviceRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun getDeviceInfo(): DeviceInfoResult {
        val abis = Build.SUPPORTED_ABIS.joinToString(", ")
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "Unknown"

        return DeviceInfoResult(
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            device = Build.DEVICE,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            buildId = Build.ID,
            abis = abis,
            androidId = androidId,
        )
    }

    fun getNetworkInfo(): NetworkInfoResult {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }

        val connectionType = when {
            caps == null -> "None"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
            else -> "Other"
        }

        val isVpn = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
        val isConnected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        val addresses = NetworkInterface.getNetworkInterfaces().toList()
            .flatMap { it.inetAddresses.toList() }
            .mapNotNull { addr ->
                val host = addr.hostAddress
                if (host != null && !addr.isLoopbackAddress && !host.contains(':')) host else null
            }
            .distinct()

        return NetworkInfoResult(
            connectionType = connectionType,
            isConnected = isConnected,
            isVpn = isVpn,
            localAddresses = addresses,
        )
    }

    fun getSystemStatus(): SystemStatusResult {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 0

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
        val statusText = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
            else -> "Unknown"
        }

        val stat = StatFs(Environment.getDataDirectory().path)
        val totalBytes = stat.totalBytes
        val freeBytes = stat.availableBytes
        val usedPercent = if (totalBytes > 0) ((totalBytes - freeBytes) * 100 / totalBytes).toInt() else 0

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        return SystemStatusResult(
            batteryLevel = batteryPct,
            batteryStatus = statusText,
            isCharging = isCharging,
            storageTotalGb = "%.1f GB".format(totalBytes / 1_073_741_824.0),
            storageFreeGb = "%.1f GB".format(freeBytes / 1_073_741_824.0),
            storageUsedPercent = usedPercent,
            ramTotalMb = "%.0f MB".format(memInfo.totalMem / 1_048_576.0),
            ramAvailableMb = "%.0f MB".format(memInfo.availMem / 1_048_576.0),
        )
    }
}
