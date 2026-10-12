package com.example.wifisecuritylabbd.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wifisecuritylabbd.R
import com.example.wifisecuritylabbd.data.ScanOutcome
import com.example.wifisecuritylabbd.data.SecurityType
import com.example.wifisecuritylabbd.data.WifiNetwork
import com.example.wifisecuritylabbd.data.WifiScanner
import com.example.wifisecuritylabbd.data.hasFineLocation
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scanner = remember(context) { WifiScanner(context) }

    var hasLocation by remember { mutableStateOf(context.hasFineLocation()) }
    var isScanning by remember { mutableStateOf(false) }
    var outcome by remember { mutableStateOf<ScanOutcome?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasLocation = granted }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            InfoCard(title = null) {
                Text(stringResource(R.string.scan_intro), style = MaterialTheme.typography.bodyMedium)
                Text(
                    stringResource(R.string.scan_limits_note),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            if (!hasLocation) {
                InfoCard(title = null) {
                    Text(stringResource(R.string.location_needed), style = MaterialTheme.typography.bodyMedium)
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.grant_location))
                    }
                }
            } else {
                Button(
                    onClick = {
                        isScanning = true
                        scope.launch {
                            outcome = scanner.scanOnce()
                            isScanning = false
                        }
                    },
                    enabled = !isScanning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(if (isScanning) R.string.scanning else R.string.scan_button))
                }
            }
        }

        outcome?.let { o ->
            when {
                o.permissionDenied -> item {
                    Text(
                        stringResource(R.string.permission_denied),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                !o.wifiEnabled -> item { Text(stringResource(R.string.wifi_off)) }
                else -> {
                    if (!o.freshScan) {
                        item {
                            Text(
                                stringResource(R.string.cached_results_note),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    if (o.networks.isEmpty()) {
                        item { Text(stringResource(R.string.no_results)) }
                    }
                    items(o.networks, key = { "${it.bssid}-${it.ssid}-${it.frequencyMhz}" }) { network ->
                        NetworkCard(network)
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworkCard(n: WifiNetwork) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = n.ssid.ifBlank { stringResource(R.string.hidden_network) },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(stringResource(R.string.rssi_format, n.rssi, qualityLabel(n.signalBars)))
            Text(stringResource(R.string.freq_format, n.band.label, n.channel))
            Text(stringResource(R.string.security_format, securityLabel(n.security)))
            n.brandHint?.let { hint ->
                Text(
                    stringResource(R.string.brand_hint_format, hint.brand),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    stringResource(R.string.brand_unverified),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                stringResource(R.string.bssid_format, n.bssid),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun qualityLabel(bars: Int): String = stringResource(
    when (bars) {
        4 -> R.string.quality_excellent
        3 -> R.string.quality_good
        2 -> R.string.quality_fair
        1 -> R.string.quality_weak
        else -> R.string.quality_poor
    }
)

@Composable
private fun securityLabel(type: SecurityType): String = stringResource(
    when (type) {
        SecurityType.OPEN -> R.string.sec_open
        SecurityType.WEP -> R.string.sec_wep
        SecurityType.WPA -> R.string.sec_wpa
        SecurityType.WPA2 -> R.string.sec_wpa2
        SecurityType.WPA3 -> R.string.sec_wpa3
        SecurityType.WPA2_WPA3 -> R.string.sec_wpa23
        SecurityType.ENTERPRISE -> R.string.sec_enterprise
        SecurityType.OWE -> R.string.sec_owe
        SecurityType.UNKNOWN -> R.string.sec_unknown
    }
)
