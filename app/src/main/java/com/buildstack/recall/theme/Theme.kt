package com.buildstack.recall.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PriorityMedium,
    secondary = PriorityLow,
    tertiary = PriorityHigh,
    background = BgGradientBottom,
    surface = BgGradientTop,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun RecallTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
      colorScheme = DarkColorScheme,
      typography = Typography,
      content = content
  )
}
