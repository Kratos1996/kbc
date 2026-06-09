package com.ishan.kbc.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.ui.theme.ErrorRed
import com.ishan.kbc.ui.theme.SurfaceVariant

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.signOutRequested) {
        if (state.signOutRequested) {
            viewModel.consumeSignOut()
            onSignOut()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
            Spacer(Modifier.height(0.dp))
            Text(
                text = "  ${stringResource(R.string.settings_title)}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(16.dp))
        SectionTitle(stringResource(R.string.settings_about))
        SwitchRow(stringResource(R.string.settings_sound), state.soundEnabled, viewModel::setSound)
        SwitchRow(stringResource(R.string.settings_music), state.musicEnabled, viewModel::setMusic)
        SwitchRow(stringResource(R.string.settings_haptics), state.hapticsEnabled, viewModel::setHaptics)
        SwitchRow(stringResource(R.string.settings_notifications), state.notificationsEnabled, viewModel::setNotifications)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SurfaceVariant)
        SectionTitle(stringResource(R.string.settings_account))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .padding(vertical = 4.dp),
        ) {
            OutlinedButton(
                onClick = viewModel::requestSignOut,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.cta_sign_out), color = ErrorRed)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.settings_version),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(vertical = 6.dp),
    )
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(0.85f),
        )
        Spacer(Modifier.fillMaxWidth(0.05f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
