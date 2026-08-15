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
import androidx.compose.material.icons.outlined.Lock
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

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.inigo.xudoku.R

/** Metadatos visuales de una dificultad para la tarjeta de selección. */
data class DifficultyVisuals(
    @StringRes val labelResId: Int,
    @StringRes val subtitleResId: Int,
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
    isLocked: Boolean = false,
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
                .background(if (isLocked) Color.Gray.copy(alpha = 0.3f) else visuals.shadowColor)
        )
        // Cara del botón
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isLocked) Color.Gray.copy(alpha = 0.15f) else visuals.faceColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    enabled           = !isLocked,
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
                    imageVector        = if (isLocked) androidx.compose.material.icons.Icons.Outlined.Lock else visuals.icon,
                    contentDescription = stringResource(id = visuals.labelResId),
                    tint               = if (isLocked) Color.Gray else visuals.iconColor,
                    modifier           = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            // Textos
            androidx.compose.foundation.layout.Column {
                Text(
                    text       = stringResource(id = visuals.labelResId),
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = if (isLocked) Color.Gray else visuals.textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text  = if (isLocked) stringResource(R.string.locked) else stringResource(id = visuals.subtitleResId),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isLocked) Color.Gray.copy(alpha = 0.75f) else visuals.textColor.copy(alpha = 0.75f)
                )
            }
        }
    }
}

// ── Helpers de visuals ───────────────────────────────────────────────────────

fun difficultyVisuals(difficulty: Difficulty, icons: DifficultyIcons): DifficultyVisuals =
    when (difficulty) {
        Difficulty.VERY_EASY -> DifficultyVisuals(
            labelResId    = R.string.diff_very_easy,
            subtitleResId = R.string.diff_subtitle_very_easy,
            faceColor   = Color(0xFF005244),
            shadowColor = Color(0xFF00382E),
            textColor   = Color(0xFF94F4DF),
            iconColor   = Color(0xFF94F4DF),
            icon        = icons.veryEasy
        )
        Difficulty.EASY -> DifficultyVisuals(
            labelResId    = R.string.diff_easy,
            subtitleResId = R.string.diff_subtitle_easy,
            faceColor   = Color(0xFF004578),
            shadowColor = Color(0xFF002F54),
            textColor   = Color(0xFFBBE6FF),
            iconColor   = Color(0xFFBBE6FF),
            icon        = icons.easy
        )
        Difficulty.MEDIUM -> DifficultyVisuals(
            labelResId    = R.string.diff_medium,
            subtitleResId = R.string.diff_subtitle_medium,
            faceColor   = Color(0xFF7C4A00),
            shadowColor = Color(0xFF543100),
            textColor   = Color(0xFFFFDDB8),
            iconColor   = Color(0xFFFFDDB8),
            icon        = icons.medium
        )
        Difficulty.HARD -> DifficultyVisuals(
            labelResId    = R.string.diff_hard,
            subtitleResId = R.string.diff_subtitle_hard,
            faceColor   = Color(0xFF9E1B1B),
            shadowColor = Color(0xFF6E0D0D),
            textColor   = Color(0xFFFFDAD6),
            iconColor   = Color(0xFFFFDAD6),
            icon        = icons.hard
        )
        Difficulty.HARDEST -> DifficultyVisuals(
            labelResId    = R.string.diff_hardest,
            subtitleResId = R.string.diff_subtitle_hardest,
            faceColor   = Color(0xFF5A002C),
            shadowColor = Color(0xFF3B001B),
            textColor   = Color(0xFFFFB0D0),
            iconColor   = Color(0xFFFFB0D0),
            icon        = icons.extreme
        )
    }

/** Contenedor de iconos pasado desde la pantalla para evitar dependencias de Material en este fichero. */
data class DifficultyIcons(
    val veryEasy: ImageVector,
    val easy: ImageVector,
    val medium: ImageVector,
    val hard: ImageVector,
    val extreme: ImageVector
)
