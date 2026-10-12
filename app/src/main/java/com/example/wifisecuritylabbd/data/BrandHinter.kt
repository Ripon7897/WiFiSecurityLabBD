package com.example.wifisecuritylabbd.data

/**
 * Best-effort brand hints from default SSID naming patterns only.
 *
 * Limitations:
 *  - Users often rename networks, so a missing match proves nothing.
 *  - A matching name does not prove the router is that brand.
 *  - OUI (BSSID vendor prefix) lookup is NOT implemented here. A correct OUI table
 *    needs the official IEEE registry, which should be bundled from a verified source
 *    rather than typed from memory.
 */
object BrandHinter {

    private val rules: List<Pair<String, Regex>> = listOf(
        "TP-Link" to Regex("tp[-_ ]?link", RegexOption.IGNORE_CASE),
        "Tenda" to Regex("tenda", RegexOption.IGNORE_CASE),
        "D-Link" to Regex("d[-_ ]?link|\\bdir[-_]\\d+", RegexOption.IGNORE_CASE),
        "Huawei" to Regex("huawei", RegexOption.IGNORE_CASE),
        "ZTE" to Regex("\\bzte|zxhn", RegexOption.IGNORE_CASE),
        "Xiaomi" to Regex("xiaomi|miwifi|redmi|^mi[-_ ]", RegexOption.IGNORE_CASE),
        "Netis" to Regex("netis", RegexOption.IGNORE_CASE),
        "Mercusys" to Regex("mercusys", RegexOption.IGNORE_CASE)
    )

    fun hint(ssid: String): BrandHint? {
        if (ssid.isBlank()) return null
        return rules.firstOrNull { (_, pattern) -> pattern.containsMatchIn(ssid) }
            ?.let { BrandHint(it.first) }
    }
}
