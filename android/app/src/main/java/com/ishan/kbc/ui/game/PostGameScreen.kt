package com.ishan.kbc.ui.game

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ishan.kbc.R
import com.ishan.kbc.domain.model.GameStatus
import com.ishan.kbc.ui.theme.Error
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.SafeZone
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerLow

@Composable
fun PostGameScreen(
    status: GameStatus,
    prize: Int,
    score: Int,
    level: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val isWin = status == GameStatus.Won
    val accentColor = if (isWin) Gold else Error
    val title = when (status) {
        GameStatus.Won -> stringResource(R.string.msg_crorepati)
        GameStatus.Lost -> stringResource(R.string.msg_wrong_answer)
        GameStatus.Quit -> stringResource(R.string.post_quit)
        GameStatus.InProgress -> ""
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(24.dp),
            ) {
                // Outcome icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isWin) "🏆" else if (status == GameStatus.Lost) "✕" else "⏹",
                        fontSize = 36.sp,
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = title,
                    color = accentColor,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                // Stats glass panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainerLow.copy(alpha = 0.8f))
                        .padding(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatRow(
                            label = stringResource(R.string.post_prize),
                            value = stringResource(R.string.game_prize, prize),
                            color = Gold,
                        )
                        StatRow(
                            label = stringResource(R.string.winner_score, score),
                            value = stringResource(R.string.post_level_reached, level),
                            color = OnSurface,
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(0.7f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = OnPrimary,
                    ),
                    elevation = ButtonDefaults.buttonElevation(6.dp, 2.dp),
                ) {
                    Text(
                        text = stringResource(R.string.post_play_again),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Gold, GoldDark),
                                ),
                                RoundedCornerShape(14.dp),
                            )
                            .wrapContentSize(Alignment.Center)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceContainerHigh,
                        contentColor = OnSurface,
                    ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(text = stringResource(R.string.cta_back))
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = value,
            color = color,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
