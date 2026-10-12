# WiFi Security Lab BD

A defensive Wi-Fi education and scanning app for Android (Kotlin, Jetpack Compose, Bangla/English).

## What it does
- **Scan:** lists nearby networks with SSID, signal (dBm), band, channel, and the security type each router advertises.
- **Brand hints:** guesses TP-Link, Tenda, D-Link, Huawei, ZTE, Xiaomi, Netis, or Mercusys from the network name only. Unverified.
- **Recovery:** steps to view a password saved on *your own* phone, and to reach your router's admin page or factory-reset it.
- **Learn:** WPA2 vs WPA3, password strength, router hardening, common weaknesses.

## What it deliberately does not do
- It does not recover, crack, capture, or test Wi-Fi passwords or handshakes, for any network.
- Android does not allow apps to read other apps' saved Wi-Fi passwords.
- Scan results are limited by Android: throttled scans, Location must be on, some networks hidden.

## Build (Android Studio)
1. Install **Android Studio Ladybug (2024.2.1) or newer**. It bundles JDK 17.
2. In the SDK Manager, install **Android SDK Platform 35** and build tools.
3. Open Android Studio, choose **File > Open**, and select the `WiFiSecurityLabBD` folder.
4. If the Gradle wrapper is missing (`gradlew` / `gradle/wrapper/gradle-wrapper.jar`), copy `gradlew`, `gradlew.bat`, and the `gradle/wrapper` folder from any new Android project, or run `gradle wrapper --gradle-version 8.9` if Gradle is installed.
5. Wait for **Gradle sync** to finish.
6. Debug APK: **Build > Build App Bundle(s) / APK(s) > Build APK(s)**. Output: `app/build/outputs/apk/debug/app-debug.apk`.
   Or from a terminal: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).
7. Install on a phone: enable **Developer options > USB debugging**, connect the phone, and run the app from Android Studio, or copy the APK to the phone and install it (allow "install unknown apps" for your file manager).

## Release APK (signed)
1. **Build > Generate Signed App Bundle / APK > APK > Create new...** and create a keystore. Keep the keystore and passwords safe; losing them means you cannot update the app.
2. Choose `release`, then finish. Output: `app/release/app-release.apk`.
3. Or run `./gradlew assembleRelease` after configuring `signingConfigs` in `app/build.gradle.kts`.

## Permissions
| Permission | Why |
|---|---|
| `ACCESS_WIFI_STATE` | Read Wi-Fi state and scan results |
| `CHANGE_WIFI_STATE` | Request a new scan |
| `ACCESS_NETWORK_STATE` | Standard network state |
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | Required by Android to receive Wi-Fi scan results. The app does not read GPS location. |

## Limitations
- Security labels reflect what a router advertises in its beacon, not how strong the password is.
- Brand hints are name-based guesses only. OUI (BSSID vendor) lookup is not included, because it needs an official, verified registry.
- Hidden networks show no name.
- The app was written without being compiled or run in this environment; run Gradle sync and fix any SDK-specific issues Android Studio reports.
