package com.example.wifisecuritylabbd.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wifisecuritylabbd.R

@Composable
fun RecoveryScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            InfoCard(title = null) {
                Text(
                    stringResource(R.string.recovery_ownership_note),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        item {
            InfoCard(title = stringResource(R.string.saved_title)) {
                Text(stringResource(R.string.saved_intro), style = MaterialTheme.typography.bodyMedium)
                NumberedList(stringArrayResource(R.array.saved_steps))
            }
        }
        item {
            InfoCard(title = stringResource(R.string.router_title)) {
                Text(stringResource(R.string.router_intro), style = MaterialTheme.typography.bodyMedium)
                NumberedList(stringArrayResource(R.array.router_steps))
            }
        }
    }
}

@Composable
fun LearnScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            InfoCard(title = stringResource(R.string.learn_wpa_title)) {
                BulletList(stringArrayResource(R.array.learn_wpa_items))
            }
        }
        item {
            InfoCard(title = stringResource(R.string.learn_strength_title)) {
                BulletList(stringArrayResource(R.array.learn_strength_items))
            }
        }
        item {
            InfoCard(title = stringResource(R.string.learn_hardening_title)) {
                BulletList(stringArrayResource(R.array.learn_hardening_items))
            }
        }
        item {
            InfoCard(title = stringResource(R.string.learn_weak_title)) {
                BulletList(stringArrayResource(R.array.learn_weak_items))
            }
        }
        item {
            InfoCard(title = stringResource(R.string.learn_scope_title)) {
                Text(stringResource(R.string.learn_scope_body), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
