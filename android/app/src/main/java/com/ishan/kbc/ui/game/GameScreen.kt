package com.ishan.kbc.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.GameStatus
import com.ishan.kbc.domain.model.LifelineType
import com.ishan.kbc.ui.components.LifelineBar
import com.ishan.kbc.ui.components.PrizeLadder
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.SafeZone

@Composable
fun GameScreen(
    onExit: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { if (state.gameId == null) viewModel.startGame() }

    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Prize ladder
        PrizeLadder(
            currentLevel = state.level,
            safeZones = setOf(5, 10),
            modifier = Modifier.width(120.dp).fillMaxSize(),
        )
        Column(modifier = Modifier.fillMaxSize().padding(start = 12.dp)) {
            Text(
                "Level ${state.level} • ₹${state.prize}" + if (state.safeZone) "  (Safe Zone)" else "",
                color = if (state.safeZone) SafeZone else Gold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer()
            state.question?.let { q ->
                Text(q.text, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.fillMaxWidth())
                Spacer()
                q.options.forEachIndexed { i, opt ->
                    val isEliminated = i in state.eliminatedOptions
                    OptionTile(
                        label = ('A' + i).toString(),
                        text = opt,
                        isEliminated = isEliminated,
                        isHighlighted = state.revealedCorrectOption == i,
                        enabled = state.status == GameStatus.InProgress && state.lastAnswerCorrect == null,
                        onClick = { viewModel.selectOption(i) },
                    )
                }
            }
            Spacer()
            LifelineBar(
                available = state.lifelinesRemaining,
                onUse = viewModel::useLifeline,
            )
            Spacer()
            state.audiencePoll?.let { poll ->
                Text("Audience: " + poll.entries.joinToString { (i, p) -> "${('A' + i)}=$p%" }, color = MaterialTheme.colorScheme.onSurface)
            }
            state.expertAnswer?.let { Text("Expert suggests: ${'A' + it}", color = MaterialTheme.colorScheme.onSurface) }
            state.phoneAFriendAnswer?.let { Text("Friend suggests: ${'A' + it}", color = MaterialTheme.colorScheme.onSurface) }
            Spacer()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.lastAnswerCorrect == true) {
                    Button(onClick = { viewModel.nextQuestion() }) { Text(stringResource(R.string.cta_continue)) }
                }
                OutlinedButton(onClick = { viewModel.quit(); onExit() }) { Text(stringResource(R.string.cta_quit)) }
            }
            if (state.status == GameStatus.Won) Text(stringResource(R.string.msg_crorepati), color = Gold, style = MaterialTheme.typography.displayMedium)
            if (state.status == GameStatus.Lost) Text(stringResource(R.string.msg_wrong_answer), color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun OptionTile(label: String, text: String, isEliminated: Boolean, isHighlighted: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !isEliminated,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isHighlighted) SafeZone else MaterialTheme.colorScheme.surface,
            contentColor = if (isHighlighted) Color.Black else MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) { Text(label, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(12.dp))
            Text(text, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun Spacer(modifier: Modifier = Modifier) = androidx.compose.foundation.layout.Spacer(modifier.height(12.dp))
