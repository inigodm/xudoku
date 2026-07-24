package com.inigo.xudoku.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.ui.theme.Background
import com.inigo.xudoku.ui.theme.ErrorColor
import com.inigo.xudoku.ui.theme.OnSurface
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.Secondary
import com.inigo.xudoku.ui.theme.SecondaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainer
import com.inigo.xudoku.ui.theme.SurfaceContainerHigh
import com.inigo.xudoku.ui.theme.Tertiary
import kotlin.math.sin
import kotlin.random.Random

/**
 * Pantalla de victoria mostrada al completar un puzzle.
 *
 * @param elapsedSeconds  Tiempo total de la partida en segundos.
 * @param mistakes        Número de errores cometidos.
 * @param difficulty      Dificultad jugada.
 * @param score           Puntuación calculada.
 * @param onNextLevel     Callback para iniciar una nueva partida con la misma dificultad.
 * @param onMainMenu      Callback para volver a Selección de Dificultad.
 */
@Composable
fun VictoryScreen(
    elapsedSeconds: Int,
    mistakes: Int,
    difficulty: Difficulty,
    score: Int,
    onNextLevel: () -> Unit,
    onMainMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Confeti animado
        ConfettiLayer()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(56.dp))

            // Badge "NUEVA MARCA"
            // TODO("mostrar solo cuando sea realmente una nueva marca")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(SecondaryContainer)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Outlined.EmojiEvents, null, tint = Secondary, modifier = Modifier.size(18.dp))
                Text(
                    "  NUEVA MARCA",
                    style      = MaterialTheme.typography.labelLarge,
                    color      = Secondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "¡VICTORIA!",
                style      = MaterialTheme.typography.displayLarge,
                color      = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Nivel Completado con éxito",
                style = MaterialTheme.typography.bodyLarge,
                color = Tertiary
            )

            Spacer(Modifier.height(24.dp))

            // Card de puntuación final
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryContainer.copy(alpha = 0.4f), SecondaryContainer.copy(alpha = 0.4f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Icon(Icons.Outlined.Star, null, tint = Tertiary, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    "PUNTUACIÓN FINAL",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant
                )
                Text(
                    "%,d".format(score),
                    style      = MaterialTheme.typography.displayLarge,
                    color      = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            // Stats: tiempo y errores
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "TIEMPO",
                    value = elapsedSeconds.toTimeString(),
                    color = Tertiary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "ERRORES",
                    value = "$mistakes/3",
                    color = if (mistakes == 0) Tertiary else ErrorColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Card de dificultad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerHigh)
                    .padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SecondaryContainer)
                ) {
                    Icon(Icons.Outlined.Psychology, null, tint = Secondary)
                }
                Column(Modifier.padding(start = 12.dp)) {
                    Text("DIFICULTAD", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text(difficulty.labelEs(), style = MaterialTheme.typography.headlineMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Barra de nivel/XP — TODO("conectar a persistencia")
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("NIVEL 1", style = MaterialTheme.typography.labelLarge, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text("0 / 1000 XP", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress   = { 0f }, // TODO
                    modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color      = Tertiary,
                    trackColor = SurfaceContainer,
                    strokeCap  = StrokeCap.Round,
                    gapSize    = 0.dp
                )
            }

            Spacer(Modifier.height(28.dp))

            // Botones de acción
            Button(
                onClick  = onNextLevel,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
            ) {
                Text(
                    "Siguiente Nivel →",
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color      = OnSurface
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick  = onMainMenu,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                border   = androidx.compose.foundation.BorderStroke(1.dp, OnSurfaceVariant.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Outlined.Home, null, modifier = Modifier.size(18.dp))
                Text("  MENÚ PRINCIPAL", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .padding(16.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

/** Capa de confeti animado con Canvas. Partículas de colores cian y violeta. */
@Composable
private fun ConfettiLayer() {
    val particles = remember {
        List(40) {
            ConfettiParticle(
                x      = Random.nextFloat(),
                y      = Random.nextFloat() * -0.5f,
                color  = if (Random.nextBoolean()) Tertiary else Secondary,
                size   = Random.nextFloat() * 10f + 5f,
                speed  = Random.nextFloat() * 0.002f + 0.001f,
                angle  = Random.nextFloat() * 360f
            )
        }
    }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        anim.animateTo(1f, tween(8_000, easing = LinearEasing))
    }

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val yPos = ((p.y + anim.value * p.speed * 800f) % 1.2f) * size.height
            val xPos = (p.x + sin((anim.value * 3f + p.angle) * 0.05f) * 0.05f) * size.width
            drawRect(
                color = p.color,
                topLeft = androidx.compose.ui.geometry.Offset(xPos, yPos),
                size = androidx.compose.ui.geometry.Size(p.size, p.size * 0.6f),
                alpha = 0.8f
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float, val y: Float, val color: Color,
    val size: Float, val speed: Float, val angle: Float
)

private fun Difficulty.labelEs() = when (this) {
    Difficulty.VERY_EASY -> "Fácil"
    Difficulty.EASY      -> "Medio"
    Difficulty.MEDIUM    -> "Difícil"
    Difficulty.HARD      -> "Extremo"
    Difficulty.HARDEST   -> "Imposible"
}
