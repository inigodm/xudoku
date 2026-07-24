package com.inigo.xudoku.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.ui.theme.ErrorContainer
import com.inigo.xudoku.ui.theme.OnErrorContainer
import com.inigo.xudoku.ui.theme.OnPrimaryContainer
import com.inigo.xudoku.ui.theme.OnSecondaryContainer
import com.inigo.xudoku.ui.theme.OnTertiaryContainer
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.SecondaryContainer
import com.inigo.xudoku.ui.theme.TertiaryContainer

/** Metadatos visuales de una dificultad para la tarjeta de selección. */
data class DifficultyVisuals(
    val labelEs: String,
    val subtitle: String,
    val faceColor: Color,
    val shadowColor: Color,
    val textColor: Color,
    val iconColor: Color,
    val icon: ImageVector
)

/**
 * Tarjeta de selección de dificultad con efecto 3D táctil.
 *
 * @param difficulty  Nivel de dificultad representado.
 * @param visuals     Metadatos visuales (colores, label, icono).
 * @param onClick     Callback al seleccionar.
 */
@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    visuals: DifficultyVisuals,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = tween(80),
        label = "difficulty-card-${difficulty.name}"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // Sombra 3D
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(visuals.shadowColor)
        )
        // Cara del botón
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .clip(RoundedCornerShape(14.dp))
                .background(visuals.faceColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    onClick           = onClick
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Icono circular
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector        = visuals.icon,
                    contentDescription = visuals.labelEs,
                    tint               = visuals.iconColor,
                    modifier           = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            // Textos
            androidx.compose.foundation.layout.Column {
                Text(
                    text       = visuals.labelEs,
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = visuals.textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text  = visuals.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = visuals.textColor.copy(alpha = 0.75f)
                )
            }
        }
    }
}

// ── Helpers de visuals ───────────────────────────────────────────────────────

fun difficultyVisuals(difficulty: Difficulty, icons: DifficultyIcons): DifficultyVisuals =
    when (difficulty) {
        Difficulty.VERY_EASY -> DifficultyVisuals(
            labelEs     = "Fácil",
            subtitle    = "35+ Números dados",
            faceColor   = TertiaryContainer,
            shadowColor = TertiaryContainer.copy(alpha = 0.45f),
            textColor   = OnTertiaryContainer,
            iconColor   = OnTertiaryContainer,
            icon        = icons.easy
        )
        Difficulty.EASY -> DifficultyVisuals(
            labelEs     = "Medio",
            subtitle    = "28–34 Números dados",
            faceColor   = SecondaryContainer,
            shadowColor = SecondaryContainer.copy(alpha = 0.45f),
            textColor   = OnSecondaryContainer,
            iconColor   = OnSecondaryContainer,
            icon        = icons.medium
        )
        Difficulty.MEDIUM -> DifficultyVisuals(
            labelEs     = "Difícil",
            subtitle    = "22–27 Números dados",
            faceColor   = PrimaryContainer,
            shadowColor = PrimaryContainer.copy(alpha = 0.45f),
            textColor   = OnPrimaryContainer,
            iconColor   = OnPrimaryContainer,
            icon        = icons.hard
        )
        Difficulty.HARD -> DifficultyVisuals(
            labelEs     = "Extremo",
            subtitle    = "< 22 Números dados",
            faceColor   = ErrorContainer,
            shadowColor = ErrorContainer.copy(alpha = 0.45f),
            textColor   = OnErrorContainer,
            iconColor   = OnErrorContainer,
            icon        = icons.extreme
        )
        Difficulty.HARDEST -> DifficultyVisuals( // oculto en la UI por ahora
            labelEs     = "Imposible",
            subtitle    = "17 Números dados",
            faceColor   = ErrorContainer,
            shadowColor = ErrorContainer.copy(alpha = 0.45f),
            textColor   = OnErrorContainer,
            iconColor   = OnErrorContainer,
            icon        = icons.extreme
        )
    }

/** Contenedor de iconos pasado desde la pantalla para evitar dependencias de Material en este fichero. */
data class DifficultyIcons(
    val easy: ImageVector,
    val medium: ImageVector,
    val hard: ImageVector,
    val extreme: ImageVector
)
