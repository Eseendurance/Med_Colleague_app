package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = MedicalTealDarkPrimary,
    onPrimary = MedicalTealDarkOnPrimary,
    primaryContainer = MedicalTealDarkContainer,
    secondary = ClinicalNavyDarkSecondary,
    secondaryContainer = ClinicalNavyDarkContainer,
    tertiary = HighYieldAmberDarkTertiary,
    tertiaryContainer = HighYieldAmberDarkContainer,
    error = EmergencyRed,
    errorContainer = EmergencyRedContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = MedicalTealPrimary,
    onPrimary = MedicalTealOnPrimary,
    primaryContainer = MedicalTealContainer,
    onPrimaryContainer = MedicalTealOnContainer,
    secondary = ClinicalNavySecondary,
    onSecondary = ClinicalNavyOnSecondary,
    secondaryContainer = ClinicalNavyContainer,
    onSecondaryContainer = ClinicalNavyOnContainer,
    tertiary = HighYieldAmberTertiary,
    tertiaryContainer = HighYieldAmberContainer,
    onTertiaryContainer = HighYieldAmberOnContainer,
    error = EmergencyRed,
    errorContainer = EmergencyRedContainer
  )

@Composable
fun MedColleagueTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MedColleagueTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

