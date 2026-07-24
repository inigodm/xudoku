package com.inigo.xudoku.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inigo.xudoku.R
import com.inigo.xudoku.ui.theme.Background
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.Primary
import com.inigo.xudoku.ui.theme.SurfaceContainer
import kotlinx.coroutines.delay

/**
 * Pantalla de splash: muestra el logo animado ~2 segundos y llama a [onSplashComplete].
 */
@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.82f) }

    LaunchedEffect(Unit) {
        // Animar entrada
        alpha.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
        scale.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
        // Esperar y navegar
        delay(1_600L)
        onSplashComplete()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(SurfaceContainer, Background)
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            // Logo
            androidx.compose.foundation.Image(
                painter            = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Sudoku logo",
                modifier           = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(28.dp))
            )
            Spacer(Modifier.height(24.dp))
            // Nombre de la app
            Text(
                text       = "SUDOKU",
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color      = Primary,
                letterSpacing = 6.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "PON A PRUEBA TU MENTE",
                style = MaterialTheme.typography.labelLarge,
                color = OnSurfaceVariant
            )
        }
    }
}
