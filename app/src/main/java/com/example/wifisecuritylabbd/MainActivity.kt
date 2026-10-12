package com.example.wifisecuritylabbd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import com.example.wifisecuritylabbd.ui.LearnScreen
import com.example.wifisecuritylabbd.ui.RecoveryScreen
import com.example.wifisecuritylabbd.ui.ScanScreen
import com.example.wifisecuritylabbd.ui.theme.WifiLabTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WifiLabTheme {
                AppRoot(onSetLanguage = ::setLanguage)
            }
        }
    }

    /** Uses AppCompat per-app locales, so the choice persists without extra storage code. */
    private fun setLanguage(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }
}

private enum class Tab(val labelRes: Int, val icon: ImageVector) {
    SCAN(R.string.tab_scan, Icons.Filled.Search),
    RECOVERY(R.string.tab_recovery, Icons.Filled.Settings),
    LEARN(R.string.tab_learn, Icons.Filled.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot(onSetLanguage: (String) -> Unit) {
    var tab by rememberSaveable { mutableStateOf(Tab.SCAN) }
    val isBangla = LocalConfiguration.current.locales[0].language == "bn"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    TextButton(onClick = { onSetLanguage(if (isBangla) "en" else "bn") }) {
                        Text(stringResource(R.string.lang_switch_to))
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { tab = item },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.labelRes)) }
                    )
                }
            }
        }
    ) { padding ->
        val contentModifier = Modifier.fillMaxSize().padding(padding)
        when (tab) {
            Tab.SCAN -> ScanScreen(contentModifier)
            Tab.RECOVERY -> RecoveryScreen(contentModifier)
            Tab.LEARN -> LearnScreen(contentModifier)
        }
    }
}
