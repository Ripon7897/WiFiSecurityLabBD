package com.example.wifisecuritylabbd.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

fun Context.hasFineLocation(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

class WifiScanner(context: Context) {

    private val app = context.applicationContext
    private val wifi = app.getSystemService(Context.WIFI_SERVICE) as WifiManager

    /**
     * Requests one scan and returns whatever Android reports.
     * Android may throttle the request; in that case the cached list is returned
     * and [ScanOutcome.freshScan] is false.
     */
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    suspend fun scanOnce(timeoutMs: Long = 10_000L): ScanOutcome {
        if (!app.hasFineLocation()) {
            return ScanOutcome(wifi.isWifiEnabled, emptyList(), freshScan = false, permissionDenied = true)
        }
        if (!wifi.isWifiEnabled) {
            return ScanOutcome(wifiEnabled = false, networks = emptyList(), freshScan = false)
        }

        val fresh = withTimeoutOrNull(timeoutMs) { awaitScanBroadcast() } ?: false

        return try {
            val networks = wifi.scanResults
                .map { it.toWifiNetwork() }
                .sortedByDescending { it.rssi }
            ScanOutcome(wifiEnabled = true, networks = networks, freshScan = fresh)
        } catch (e: SecurityException) {
            ScanOutcome(wifiEnabled = true, networks = emptyList(), freshScan = false, permissionDenied = true)
        }
    }

    /** Starts a scan and waits for the results broadcast. Resolves false if the scan could not start. */
    @Suppress("DEPRECATION")
    private suspend fun awaitScanBroadcast(): Boolean = suspendCancellableCoroutine { cont ->
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                runCatching { app.unregisterReceiver(this) }
                if (cont.isActive) {
                    cont.resume(intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false))
                }
            }
        }

        ContextCompat.registerReceiver(
            app,
            receiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        cont.invokeOnCancellation { runCatching { app.unregisterReceiver(receiver) } }

        val started = try {
            wifi.startScan()
        } catch (e: SecurityException) {
            false
        }
        if (!started) {
            // startScan() returns false when throttled: no broadcast will follow.
            runCatching { app.unregisterReceiver(receiver) }
            if (cont.isActive) cont.resume(false)
        }
    }

    @Suppress("DEPRECATION")
    private fun ScanResult.toWifiNetwork(): WifiNetwork {
        val ssid = this.SSID.orEmpty()
        return WifiNetwork(
            ssid = ssid,
            bssid = BSSID.orEmpty(),
            rssi = level,
            frequencyMhz = frequency,
            channel = BandParser.channel(frequency),
            band = BandParser.band(frequency),
            security = SecurityParser.parse(capabilities.orEmpty()),
            brandHint = BrandHinter.hint(ssid)
        )
    }
}
