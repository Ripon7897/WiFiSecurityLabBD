package com.example.wifisecuritylabbd.data

/** Security type derived from the capability string Android reports for each beacon. */
enum class SecurityType { OPEN, WEP, WPA, WPA2, WPA3, WPA2_WPA3, ENTERPRISE, OWE, UNKNOWN }

enum class Band(val label: String) {
    GHZ_2_4("2.4 GHz"),
    GHZ_5("5 GHz"),
    GHZ_6("6 GHz"),
    UNKNOWN("?")
}

/** A best-effort guess from the network name. Never treated as verified identification. */
data class BrandHint(val brand: String)

data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val frequencyMhz: Int,
    val channel: Int,
    val band: Band,
    val security: SecurityType,
    val brandHint: BrandHint?
) {
    /** 0 (very weak) to 4 (excellent), using common RSSI thresholds. */
    val signalBars: Int
        get() = when {
            rssi >= -50 -> 4
            rssi >= -60 -> 3
            rssi >= -70 -> 2
            rssi >= -80 -> 1
            else -> 0
        }
}

/**
 * Result of one scan attempt.
 * freshScan = false means Android returned cached results (scan throttled or timed out).
 */
data class ScanOutcome(
    val wifiEnabled: Boolean,
    val networks: List<WifiNetwork>,
    val freshScan: Boolean,
    val permissionDenied: Boolean = false
)

object SecurityParser {
    /** Parses an Android ScanResult.capabilities string such as "[WPA2-PSK-CCMP][ESS]". */
    fun parse(capabilities: String): SecurityType {
        val c = capabilities.uppercase()
        return when {
            "EAP" in c -> SecurityType.ENTERPRISE
            "OWE" in c -> SecurityType.OWE
            ("SAE" in c || "WPA3" in c) && "PSK" in c -> SecurityType.WPA2_WPA3
            "SAE" in c || "WPA3" in c -> SecurityType.WPA3
            "RSN" in c || "WPA2" in c -> SecurityType.WPA2
            "WPA-" in c || "WPA+" in c -> SecurityType.WPA
            "WEP" in c -> SecurityType.WEP
            "PSK" in c -> SecurityType.UNKNOWN
            else -> SecurityType.OPEN   // e.g. "[ESS]" only: no security flags advertised
        }
    }
}

object BandParser {
    fun band(frequencyMhz: Int): Band = when (frequencyMhz) {
        in 2400..2500 -> Band.GHZ_2_4
        in 4900..5900 -> Band.GHZ_5
        in 5925..7125 -> Band.GHZ_6
        else -> Band.UNKNOWN
    }

    fun channel(frequencyMhz: Int): Int = when (band(frequencyMhz)) {
        Band.GHZ_2_4 -> if (frequencyMhz == 2484) 14 else (frequencyMhz - 2407) / 5
        Band.GHZ_5 -> (frequencyMhz - 5000) / 5
        Band.GHZ_6 -> (frequencyMhz - 5950) / 5
        else -> -1
    }
}
