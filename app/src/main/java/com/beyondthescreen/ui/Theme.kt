package com.beyondthescreen.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.beyondthescreen.R

val Ink = Color(0xFF07080C)
val Panel = Color(0xFF171B24)
val PanelBright = Color(0xFF242A38)
val Line = Color(0xFF3A4254)
val Ivory = Color(0xFFF3EEE4)
val IvoryDim = Color(0xFFB7B1A6)
val Amber = Color(0xFFF0B429)
val Sea = Color(0xFF8FDED0)

private val DisplayFont = FontFamily(
    Font(R.font.fraunces_medium, FontWeight.Medium),
    Font(R.font.fraunces_semibold, FontWeight.SemiBold),
    Font(R.font.fraunces_italic, FontWeight.Medium, FontStyle.Italic),
)

private val SansFont = FontFamily(
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
)

object BType {
    val display = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        color = Ivory,
    )
    val title = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        color = Ivory,
    )
    val cardTitle = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        color = Ivory,
    )
    val body = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        color = Ivory,
    )
    val meta = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = IvoryDim,
    )
    val label = TextStyle(
        fontFamily = SansFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.4.sp,
        color = Amber,
    )
    val italic = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        color = Ivory,
    )
}

@Composable
fun BeyondTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Amber,
            background = Ink,
            surface = Panel,
            onPrimary = Ink,
            onBackground = Ivory,
            onSurface = Ivory,
        ),
        typography = Typography(
            displayLarge = BType.display,
            headlineLarge = BType.title,
            titleLarge = BType.cardTitle,
            bodyLarge = BType.body,
            bodyMedium = BType.meta,
            labelLarge = BType.label,
        ),
        content = content,
    )
}
