package com.ishan.kbc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ishan.kbc.R

// Variable TTF fonts — one file per family with wght/opsz axes
val SoraFont = FontFamily(
    Font(R.font.sora, FontWeight.Normal),
    Font(R.font.sora, FontWeight.Medium),
    Font(R.font.sora, FontWeight.SemiBold),
    Font(R.font.sora, FontWeight.Bold),
    Font(R.font.sora, FontWeight.ExtraBold),
    Font(R.font.sora, FontWeight.Black),
)

val InterFont = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
    Font(R.font.inter, FontWeight.Medium),
    Font(R.font.inter, FontWeight.SemiBold),
    Font(R.font.inter, FontWeight.Bold),
)

val JetBrainsMonoFont = FontFamily(
    Font(R.font.jetbrainsmono, FontWeight.Normal),
    Font(R.font.jetbrainsmono, FontWeight.Medium),
    Font(R.font.jetbrainsmono, FontWeight.SemiBold),
    Font(R.font.jetbrainsmono, FontWeight.Bold),
)

val KbcTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SoraFont,
        fontSize = 48.sp,
        fontWeight = FontWeight.Black,
        lineHeight = 56.sp,
        letterSpacing = (-0.02).em,
    ),
    displayMedium = TextStyle(
        fontFamily = SoraFont,
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 44.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = SoraFont,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 36.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = SoraFont,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = SoraFont,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = SoraFont,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = SoraFont,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SoraFont,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = SoraFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFont,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = InterFont,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = JetBrainsMonoFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.05.em,
    ),
    labelMedium = TextStyle(
        fontFamily = JetBrainsMonoFont,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.05.em,
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMonoFont,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 14.sp,
        letterSpacing = 0.05.em,
    ),
)
