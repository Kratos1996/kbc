package com.ishan.kbc.ui.multiplayer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.MpMatch
import com.ishan.kbc.ui.theme.Gold

@Composable
fun MultiplayerLobbyScreen(
    onBack: () -> Unit,
    viewModel: MultiplayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
            Spacer(Modifier.height(0.dp))
            Text(
                text = "  ${stringResource(R.string.mp_title)}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(12.dp))
        TabRow(selectedTabIndex = state.tab.ordinal) {
            MpTab.entries.forEach { tab ->
                Tab(
                    selected = state.tab == tab,
                    onClick = { viewModel.setTab(tab) },
                    text = {
                        Text(
                            when (tab) {
                                MpTab.Create -> stringResource(R.string.mp_tab_create)
                                MpTab.Join -> stringResource(R.string.mp_tab_join)
                            }
                        )
                    },
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        when (state.tab) {
            MpTab.Create -> CreateTab(
                loading = state.loading,
                match = state.createdMatch,
                onCreate = viewModel::create,
                onCopy = { code -> copyToClipboard(context, code) },
                onShare = { code -> shareText(context, "Join my KBC match with code: $code") },
            )
            MpTab.Join -> JoinTab(
                loading = state.loading,
                code = state.joinCode,
                error = state.error,
                match = state.joinedMatch,
                onCodeChanged = viewModel::onJoinCodeChanged,
                onJoin = viewModel::join,
            )
        }
    }
}

@Composable
private fun CreateTab(
    loading: Boolean,
    match: MpMatch?,
    onCreate: () -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (match == null) {
            Text(
                stringResource(R.string.mp_create_cta),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Button(onClick = onCreate, enabled = !loading, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cta_create))
            }
            if (loading) CircularProgressIndicator()
        } else {
            Text(
                stringResource(R.string.mp_invite_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    match.inviteCode.orEmpty(),
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onCopy(match.inviteCode.orEmpty()) }) {
                    Text(stringResource(R.string.cta_copy))
                }
                OutlinedButton(onClick = { onShare(match.inviteCode.orEmpty()) }) {
                    Text(stringResource(R.string.cta_share))
                }
            }
            Text(
                stringResource(R.string.mp_match_id, match.id),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                stringResource(R.string.mp_status, match.status),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.mp_players_count, match.players.size),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun JoinTab(
    loading: Boolean,
    code: String,
    error: String?,
    match: MpMatch?,
    onCodeChanged: (String) -> Unit,
    onJoin: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChanged,
            label = { Text(stringResource(R.string.hint_invite_code)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Button(onClick = onJoin, enabled = !loading && code.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.cta_join))
        }
        if (loading) CircularProgressIndicator()
        if (error != null) {
            Text(
                text = stringResource(R.string.mp_join_error) + " ($error)",
                color = MaterialTheme.colorScheme.error,
            )
        }
        match?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.mp_match_id, it.id),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                stringResource(R.string.mp_status, it.status),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                stringResource(R.string.mp_players_count, it.players.size),
                color = MaterialTheme.colorScheme.onSurface,
            )
            it.players.forEach { p ->
                Text(
                    "• ${p.displayName?.takeIf { n -> n.isNotBlank() } ?: p.username} (${p.score})",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, value: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("KBC invite", value))
}

private fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share invite"))
}
