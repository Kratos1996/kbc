package com.ishan.kbc.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.UserProfile
import com.ishan.kbc.domain.model.UserStats
import com.ishan.kbc.ui.theme.Gold
import kotlinx.coroutines.delay


@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(1500)
            viewModel.consumeMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
            Spacer(Modifier.height(0.dp))
            Text(
                text = "  ${stringResource(R.string.profile_title)}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(16.dp))

        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.profile == null -> Text(
                state.error ?: stringResource(R.string.error_generic),
                color = MaterialTheme.colorScheme.error,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileHeader(state.profile!!)
                if (state.stats != null) StatsRow(state.profile!!, state.stats!!)
                Button(onClick = viewModel::beginEdit, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.profile_edit))
                }
                if (state.message != null) {
                    Text(
                        stringResource(R.string.profile_updated),
                        color = Gold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    if (state.editing) {
        EditDialog(
            displayName = state.draftDisplayName,
            avatarUrl = state.draftAvatarUrl,
            saving = state.saving,
            onDisplayNameChange = viewModel::onDisplayNameChange,
            onAvatarChange = viewModel::onAvatarChange,
            onSave = viewModel::save,
            onCancel = viewModel::cancelEdit,
        )
    }
}

@Composable
private fun ProfileHeader(profile: UserProfile) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val avatarModifier = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
        if (!profile.avatarUrl.isNullOrBlank()) {
            AsyncImage(model = profile.avatarUrl, contentDescription = null, modifier = avatarModifier)
        } else {
            Box(avatarModifier, contentAlignment = Alignment.Center) {
                Text(
                    profile.displayName?.firstOrNull()?.uppercaseChar()?.toString() ?: profile.username.firstOrNull()?.uppercaseChar().toString(),
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }
        Spacer(Modifier.size(16.dp))
        Column {
            Text(
                profile.displayName?.takeIf { it.isNotBlank() } ?: profile.username,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                stringResource(R.string.profile_member_since, profile.createdAt.take(10)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatsRow(profile: UserProfile, stats: UserStats) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        StatCard(label = stringResource(R.string.profile_coins), value = profile.coins.toString(), modifier = Modifier.fillMaxWidth(0.5f))
        StatCard(
            label = stringResource(R.string.profile_premium),
            value = stringResource(if (profile.isPremium) R.string.profile_yes else R.string.profile_no),
            modifier = Modifier.fillMaxWidth(0.5f),
        )
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        StatCard(label = stringResource(R.string.profile_best_score), value = stats.bestScore.toString(), modifier = Modifier.fillMaxWidth(0.5f))
        StatCard(label = stringResource(R.string.profile_total_score), value = stats.totalScore.toString(), modifier = Modifier.fillMaxWidth(0.5f))
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        StatCard(label = stringResource(R.string.profile_games_played), value = stats.gamesPlayed.toString(), modifier = Modifier.fillMaxWidth(0.5f))
        StatCard(label = stringResource(R.string.profile_games_won), value = stats.gamesWon.toString(), modifier = Modifier.fillMaxWidth(0.5f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
    ) {
        Column {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(value, color = Gold, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun EditDialog(
    displayName: String,
    avatarUrl: String,
    saving: Boolean,
    onDisplayNameChange: (String) -> Unit,
    onAvatarChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.profile_edit)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text(stringResource(R.string.hint_display_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = onAvatarChange,
                    label = { Text(stringResource(R.string.hint_avatar_url)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !saving) { Text(stringResource(R.string.cta_save)) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.cta_cancel)) }
        },
    )
}
