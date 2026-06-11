package com.ishan.kbc.ui.daily

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.SafeZone

@Composable
fun DailyChallengeScreen(
    onBack: () -> Unit,
    viewModel: DailyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        when {
            state.loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 80.dp))
            state.error != null -> ErrorView(state.error!!, onBack)
            state.alreadySubmitted -> AlreadyDoneView(state.previousScore ?: 0, onBack)
            state.result != null -> ResultView(
                correct = state.result!!.correct,
                total = state.result!!.total,
                score = state.result!!.score,
                bonusAwarded = state.result!!.bonusAwarded,
                bonusCoins = state.bonusCoins,
                onBack = onBack,
            )
            else -> PlayingView(state = state, viewModel = viewModel, onBack = onBack)
        }
    }
}

@Composable
private fun PlayingView(
    state: DailyUiState,
    viewModel: DailyViewModel,
    onBack: () -> Unit,
) {
    val q = state.currentQuestion ?: return
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            stringResource(R.string.daily_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            stringResource(R.string.daily_progress, state.currentIndex + 1, state.questions.size),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        LinearProgressIndicator(
            progress = { (state.currentIndex + 1).toFloat() / state.questions.size.coerceAtLeast(1) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = Gold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            q.text,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        q.options.forEachIndexed { i, opt ->
            val chosen = state.answers[q.id] == i
            OptionTile(
                label = ('A' + i).toString(),
                text = opt,
                selected = chosen,
            ) { viewModel.selectOption(i) }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val isLast = (state.currentIndex == state.questions.size - 1)
            val canAdvance = state.answers.containsKey(q.id) && !state.submitting
            Button(
                onClick = { viewModel.next() },
                enabled = canAdvance,
                modifier = Modifier.fillMaxWidth(0.6f),
            ) {
                Text(stringResource(if (isLast) R.string.daily_finish else R.string.daily_next))
            }
            OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
        }
        if (state.submitting) CircularProgressIndicator()
    }
}

@Composable
private fun OptionTile(label: String, text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) SafeZone else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface,
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
            ) {
                Text(label, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Text(text, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ResultView(
    correct: Int,
    total: Int,
    score: Int,
    bonusAwarded: Boolean,
    bonusCoins: Int,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.daily_score_summary, correct, total, score),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(16.dp))
        if (bonusAwarded) {
            Text(
                stringResource(R.string.daily_bonus_awarded, bonusCoins),
                style = MaterialTheme.typography.titleLarge,
                color = Gold,
            )
        }
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
    }
}

@Composable
private fun AlreadyDoneView(score: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.daily_already_done),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Score: $score",
            style = MaterialTheme.typography.titleLarge,
            color = Gold,
        )
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
    }
}

@Composable
private fun ErrorView(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.daily_error_load),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
        )
        if (message.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cta_back)) }
    }
}
