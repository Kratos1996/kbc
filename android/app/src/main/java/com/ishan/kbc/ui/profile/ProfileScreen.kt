package com.ishan.kbc.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.UserProfile
import com.ishan.kbc.domain.model.UserStats
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SecondaryContainer
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow
import kotlinx.coroutines.delay

private val HexShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w, h * 0.25f)
            lineTo(w, h * 0.75f)
            lineTo(w * 0.5f, h)
            lineTo(0f, h * 0.75f)
            lineTo(0f, h * 0.25f)
            close()
        }
        return Outline.Generic(path)
    }
}

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.profile == null -> Text(
                state.error ?: stringResource(R.string.error_generic),
                color = MaterialTheme.colorScheme.error,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileHeader(state.profile!!)
                if (state.stats != null) StatsGrid(state.profile!!, state.stats!!)
                Button(
                    onClick = viewModel::beginEdit,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(stringResource(R.string.profile_edit), fontWeight = FontWeight.Bold)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.6f))
            .border(1.dp, SurfaceContainerHighest.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val avatarModifier = Modifier
                .size(80.dp)
                .clip(HexShape)
                .background(PrimaryContainer.copy(alpha = 0.4f))
                .border(2.dp, Primary.copy(alpha = 0.6f), HexShape)
            if (!profile.avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = null,
                    modifier = avatarModifier,
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(avatarModifier, contentAlignment = Alignment.Center) {
                    Text(
                        profile.displayName?.firstOrNull()?.uppercaseChar()?.toString()
                            ?: profile.username.firstOrNull()?.uppercaseChar().toString(),
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    profile.displayName?.takeIf { it.isNotBlank() } ?: profile.username,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Text(
                    profile.email,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    stringResource(R.string.profile_member_since, profile.createdAt.take(10)),
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(profile: UserProfile, stats: UserStats) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            GlassStatCard(
                label = stringResource(R.string.profile_coins),
                value = profile.coins.toString(),
                modifier = Modifier.weight(1f),
            )
            GlassStatCard(
                label = stringResource(R.string.profile_premium),
                value = stringResource(if (profile.isPremium) R.string.profile_yes else R.string.profile_no),
                modifier = Modifier.weight(1f),
            )
            GlassStatCard(
                label = stringResource(R.string.profile_best_score),
                value = stats.bestScore.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            GlassStatCard(
                label = stringResource(R.string.profile_total_score),
                value = stats.totalScore.toString(),
                modifier = Modifier.weight(1f),
            )
            GlassStatCard(
                label = stringResource(R.string.profile_games_played),
                value = stats.gamesPlayed.toString(),
                modifier = Modifier.weight(1f),
            )
            GlassStatCard(
                label = stringResource(R.string.profile_games_won),
                value = stats.gamesWon.toString(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun GlassStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.5f))
            .border(1.dp, SurfaceContainerHighest.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(value, color = Gold, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
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
        containerColor = SurfaceContainerHigh,
        title = { Text(stringResource(R.string.profile_edit), color = OnSurface) },
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
