package com.ishan.kbc.ui.tournament

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.ui.theme.Error
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.JetBrainsMonoFont
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SoraFont
import com.ishan.kbc.ui.theme.SurfaceBright
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow
import com.ishan.kbc.ui.theme.SurfaceContainerLowest

@Composable
fun TournamentScreen(
    onBack: () -> Unit,
    viewModel: TournamentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.startCountdown() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(20.dp))

            // Hero section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(SurfaceContainerLow, SurfaceContainerLow.copy(alpha = 0.3f)),
                        ),
                    )
                    .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Box(
                        modifier = Modifier
                            .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(
                            "PREMIUM EVENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "SUNDAY",
                        style = MaterialTheme.typography.headlineLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "GRAND CHAMPIONSHIP",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Gold,
                        fontWeight = FontWeight.Black,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.tournament_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.tournament_prize_pool),
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                letterSpacing = 2.sp,
                            )
                            Text(
                                "$1,000,000",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Gold,
                                fontWeight = FontWeight.Black,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(OnSurfaceVariant.copy(alpha = 0.3f)),
                        )
                        Column {
                            Text(
                                stringResource(R.string.tournament_starts),
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                letterSpacing = 2.sp,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                CountdownUnit(state.days.toString().padStart(2, '0'), stringResource(R.string.tournament_days))
                                Text(":", color = Primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                CountdownUnit(state.hours.toString().padStart(2, '0'), stringResource(R.string.tournament_hrs))
                                Text(":", color = Primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                CountdownUnit(state.minutes.toString().padStart(2, '0'), stringResource(R.string.tournament_min))
                                Text(":", color = Primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                CountdownUnit(state.seconds.toString().padStart(2, '0'), stringResource(R.string.tournament_sec))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.tournament_requirements),
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))
                val reqs = listOf(
                    "Minimum Level 10" to "Standard player eligibility",
                    "Valid Lifeline Bundle" to "Active for championship duration",
                    "Clean Play Record" to "No active bans or warnings",
                    "Region Verified" to "Available for Global Arena",
                )
                reqs.forEach { (title, desc) ->
                    RequirementRow(title, desc)
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.tournament_breakdown),
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))
                PrizeRow("1st Place Grand Winner", "$500,000", gold = true)
                Spacer(Modifier.height(8.dp))
                PrizeRow("2nd - 10th Finalists", "$25,000 each")
                Spacer(Modifier.height(8.dp))
                PrizeRow("Top 100 Consolation", "$2,500 each")
            }

            Spacer(Modifier.height(12.dp))

            // Entry selection + Register
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.tournament_entry_method),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(12.dp))
                EntryOption(
                    title = "5,000 RP",
                    subtitle = "Radiance Points Entry",
                    icon = "RP",
                    selected = state.selectedEntry == EntryMethod.RadiancePoints,
                    onClick = { viewModel.selectEntry(EntryMethod.RadiancePoints) },
                )
                Spacer(Modifier.height(8.dp))
                EntryOption(
                    title = "Gold Pass",
                    subtitle = "Season Pass Admission",
                    icon = "GP",
                    selected = state.selectedEntry == EntryMethod.GoldPass,
                    onClick = { viewModel.selectEntry(EntryMethod.GoldPass) },
                )
                Spacer(Modifier.height(20.dp))

                val btnColor = if (state.isRegistered) Primary else Gold
                Button(
                    onClick = { viewModel.register() },
                    enabled = !state.isRegistering,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = OnPrimary,
                        disabledContainerColor = Color.Transparent,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                if (state.isRegistered) Brush.horizontalGradient(listOf(PrimaryContainer, Primary))
                                else Brush.horizontalGradient(listOf(Gold, GoldDark)),
                                RoundedCornerShape(16.dp),
                            ),
                    )
                    if (state.isRegistering) {
                        CircularProgressIndicator(
                            color = OnPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            if (state.isRegistered) stringResource(R.string.tournament_registered)
                            else stringResource(R.string.tournament_register),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 3.sp,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.tournament_ends),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(12.dp))

            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.tournament_logistics),
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))
                LogisticsRow("Sunday, Oct 24, 2023")
                Spacer(Modifier.height(8.dp))
                LogisticsRow("20:00 UTC (Main Event)")
                Spacer(Modifier.height(8.dp))
                LogisticsRow("Format: Triple Knockout")
            }

            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun CountdownUnit(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontFamily = JetBrainsMonoFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
        )
        Text(
            label,
            fontSize = 8.sp,
            color = OnSurfaceVariant,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun GlassPanel(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.4f))
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) { content() }
}

@Composable
private fun RequirementRow(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceBright.copy(alpha = 0.05f))
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.SemiBold)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun PrizeRow(label: String, amount: String, gold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (gold) Gold.copy(alpha = 0.08f)
                else SurfaceBright.copy(alpha = 0.05f),
            )
            .border(
                1.dp,
                if (gold) Gold.copy(alpha = 0.2f) else OnSurfaceVariant.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = if (gold) Gold else OnSurface, fontWeight = FontWeight.SemiBold)
        Text(amount, style = MaterialTheme.typography.titleSmall, color = if (gold) Gold else OnSurface, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EntryOption(
    title: String,
    subtitle: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Gold.copy(alpha = 0.6f) else OnSurfaceVariant.copy(alpha = 0.2f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (selected) Gold.copy(alpha = 0.2f)
                    else SurfaceContainerHighest,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selected) Gold else OnSurfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Gold, unselectedColor = OnSurfaceVariant),
        )
    }
}

@Composable
private fun LogisticsRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.4f)),
        )
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
    }
}
