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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishan.kbc.R
import com.ishan.kbc.ui.components.ArenaBackground
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.JetBrainsMonoFont
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.SoraFont
import com.ishan.kbc.ui.theme.SurfaceBright
import com.ishan.kbc.ui.theme.SurfaceContainerHigh
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow

@Composable
fun TournamentScreen(
    onBack: () -> Unit,
    viewModel: TournamentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.startCountdown() }

    Box(modifier = Modifier.fillMaxSize()) {
        ArenaBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow.copy(alpha = 0.85f))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RADIANCE ARENA",
                        color = Primary,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 4.sp,
                        fontFamily = SoraFont,
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceContainerHigh.copy(alpha = 0.4f))
                        .border(1.dp, OnSurfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "\u20B9",
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "12,500 GC",
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }
            }

            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainerLow.copy(alpha = 0.6f))
                    .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Box(
                        modifier = Modifier
                            .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "PREMIUM EVENT",
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "SUNDAY",
                        color = OnSurface,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        fontFamily = SoraFont,
                    )
                    Text(
                        text = "GRAND CHAMPIONSHIP",
                        color = Gold,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        fontFamily = SoraFont,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.tournament_desc),
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.tournament_prize_pool),
                                color = OnSurfaceVariant,
                                fontSize = 10.sp,
                                letterSpacing = 2.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "$1,000,000",
                                color = Gold,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
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
                                text = stringResource(R.string.tournament_starts),
                                color = OnSurfaceVariant,
                                fontSize = 10.sp,
                                letterSpacing = 2.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                // Hex decoration
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(120.dp)
                        .padding(bottom = 8.dp, end = 8.dp),
                ) {
                    HexDecoration()
                }
            }

            Spacer(Modifier.height(20.dp))

            // Requirements + Prize Breakdown in flex row
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Requirements
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceContainerLow.copy(alpha = 0.6f))
                        .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(20.dp),
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.tournament_requirements),
                                color = OnSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = SoraFont,
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        RequirementCard(
                            icon = "\u25B2",
                            title = "Minimum Level 10",
                            desc = "Standard player eligibility",
                        )
                        Spacer(Modifier.height(8.dp))
                        RequirementCard(
                            icon = "\u26A1",
                            title = "Valid Lifeline Bundle",
                            desc = "Active for championship duration",
                        )
                        Spacer(Modifier.height(8.dp))
                        RequirementCard(
                            icon = "\u270D",
                            title = "Clean Play Record",
                            desc = "No active bans or warnings",
                        )
                        Spacer(Modifier.height(8.dp))
                        RequirementCard(
                            icon = "\uD83C\uDF10",
                            title = "Region Verified",
                            desc = "Available for Global Arena",
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Prize Breakdown
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceContainerLow.copy(alpha = 0.6f))
                        .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(20.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.tournament_breakdown),
                            color = OnSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = SoraFont,
                        )
                        Spacer(Modifier.height(16.dp))
                        PrizeBreakdownRow(
                            icon = "\uD83C\uDFC6",
                            label = "1st Place Grand Winner",
                            amount = "$500,000",
                            gold = true,
                        )
                        Spacer(Modifier.height(8.dp))
                        PrizeBreakdownRow(
                            icon = "\uD83C\uDF96",
                            label = "2nd - 10th Finalists",
                            amount = "$25,000 each",
                        )
                        Spacer(Modifier.height(8.dp))
                        PrizeBreakdownRow(
                            icon = "\uD83D\uDC65",
                            label = "Top 100 Consolation",
                            amount = "$2,500 each",
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Entry Fee + Register
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceContainerLow.copy(alpha = 0.6f))
                        .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(20.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.tournament_entry_method),
                            color = OnSurfaceVariant,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                        EntryCard(
                            title = "5,000 RP",
                            subtitle = "Radiance Points Entry",
                            icon = "\uD83D\uDCB0",
                            selected = state.selectedEntry == EntryMethod.RadiancePoints,
                            gold = true,
                            onClick = { viewModel.selectEntry(EntryMethod.RadiancePoints) },
                        )
                        Spacer(Modifier.height(8.dp))
                        EntryCard(
                            title = "Gold Pass",
                            subtitle = "Season Pass Admission",
                            icon = "\uD83D\uDCC5",
                            selected = state.selectedEntry == EntryMethod.GoldPass,
                            gold = false,
                            onClick = { viewModel.selectEntry(EntryMethod.GoldPass) },
                        )
                        Spacer(Modifier.height(20.dp))

                        // Register Button with 3D Ledge + Shimmer
                        val transition = rememberInfiniteTransition(label = "pulse")
                        val pulseScale by transition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.03f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse,
                            ),
                            label = "pulseScale",
                        )
                        Button(
                            onClick = { viewModel.register() },
                            enabled = !state.isRegistering && !state.isRegistered,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .drawBehind {
                                    if (!state.isRegistered) {
                                        drawRoundRect(
                                            color = Color.Black.copy(alpha = 0.3f),
                                            cornerRadius = CornerRadius(16f, 16f),
                                            topLeft = Offset(0f, 4f),
                                            size = Size(size.width, size.height),
                                        )
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = OnPrimary,
                                disabledContainerColor = Color.Transparent,
                            ),
                        ) {
                            if (state.isRegistering) {
                                CircularProgressIndicator(color = OnPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else if (state.isRegistered) {
                                Text(
                                    text = stringResource(R.string.tournament_registered),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 3.sp,
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(listOf(Gold, GoldDark)),
                                            RoundedCornerShape(16.dp),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = stringResource(R.string.tournament_register),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        letterSpacing = 3.sp,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.tournament_ends),
                            color = OnSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Event Logistics
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceContainerLow.copy(alpha = 0.6f))
                        .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(20.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.tournament_logistics),
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                        )
                        Spacer(Modifier.height(16.dp))
                        LogisticsItem(
                            icon = "\uD83D\uDCC5",
                            text = "Sunday, Oct 24, 2023",
                        )
                        Spacer(Modifier.height(12.dp))
                        LogisticsItem(
                            icon = "\u23F0",
                            text = "20:00 UTC (Main Event)",
                        )
                        Spacer(Modifier.height(12.dp))
                        LogisticsItem(
                            icon = "\uD83C\uDFAE",
                            text = "Format: Triple Knockout",
                        )
                    }
                }
            }

            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun CountdownUnit(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = JetBrainsMonoFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
        )
        Text(
            text = label,
            fontSize = 8.sp,
            color = OnSurfaceVariant,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun HexDecoration() {
    val w = 120f; val h = 120f
    val path = Path().apply {
        moveTo(w * 0.25f, 0f)
        lineTo(w * 0.75f, 0f)
        lineTo(w, h * 0.5f)
        lineTo(w * 0.75f, h)
        lineTo(w * 0.25f, h)
        lineTo(0f, h * 0.5f)
        close()
    }
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(HtmlHexShape)
            .background(Primary.copy(alpha = 0.08f)),
    )
}

private val HtmlHexShape = object : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density,
    ): androidx.compose.ui.graphics.Outline {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.25f, 0f)
            lineTo(w * 0.75f, 0f)
            lineTo(w, h * 0.5f)
            lineTo(w * 0.75f, h)
            lineTo(w * 0.25f, h)
            lineTo(0f, h * 0.5f)
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

@Composable
private fun RequirementCard(icon: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceBright.copy(alpha = 0.05f))
            .border(1.dp, OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = icon, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Text(
                text = desc,
                color = OnSurfaceVariant,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun PrizeBreakdownRow(icon: String, label: String, amount: String, gold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .let {
                if (gold) {
                    it.background(
                        Brush.horizontalGradient(
                            listOf(
                                Gold.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
                } else {
                    it.background(SurfaceBright.copy(alpha = 0.05f))
                }
            }
            .border(1.dp, if (gold) Gold.copy(alpha = 0.2f) else OnSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                color = if (gold) Gold else OnSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }
        Text(
            text = amount,
            color = if (gold) Gold else OnSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            fontFamily = SoraFont,
        )
    }
}

@Composable
private fun EntryCard(
    title: String,
    subtitle: String,
    icon: String,
    selected: Boolean,
    gold: Boolean,
    onClick: () -> Unit,
) {
    val borderC = if (selected) (if (gold) Gold.copy(alpha = 0.6f) else Primary.copy(alpha = 0.6f))
        else OnSurfaceVariant.copy(alpha = 0.2f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerHigh)
            .border(1.dp, borderC, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected && gold) Gold.copy(alpha = 0.2f) else Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = icon, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = OnSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Text(
                text = subtitle,
                color = OnSurfaceVariant,
                fontSize = 12.sp,
            )
        }
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    if (selected) (if (gold) Gold else Primary)
                    else SurfaceContainerHighest
                )
                .border(
                    2.dp,
                    if (selected) (if (gold) Gold else Primary) else OnSurfaceVariant.copy(alpha = 0.3f),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(OnPrimary),
                )
            }
        }
    }
}

@Composable
private fun LogisticsItem(icon: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 14.sp)
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
