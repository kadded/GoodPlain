package com.goodplan.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1F7A8C),
    secondary = Color(0xFFF2B93B),
    tertiary = Color(0xFF4CAF50),
    background = Color(0xFFF7F8FA),
    surface = Color.White
)

@Composable
fun GoodPlanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}