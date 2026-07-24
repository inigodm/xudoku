package com.inigo.xudoku.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inigo.xudoku.ui.theme.OnSurface
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.Secondary
import com.inigo.xudoku.ui.theme.SecondaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainerHigh
import com.inigo.xudoku.ui.theme.Tertiary

/**
 * Teclado numérico 1–9 + botón de borrar.
 * El número activo (mismo que la celda seleccionada) se resalta en cian.
 * Los botones tienen un efecto 3D táctil via offset animado.
 *
 * @param onNumberClick   Llamado con el dígito pulsado (1-9).
 * @param onDeleteClick   Llamado al pulsar el botón de borrar.
 * @param selectedNumber  Número actualmente en la celda seleccionada (para resaltar).
 */
@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    selectedNumber: Int? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Fila 1: dígitos 1–5
        Row(Modifier.fillMaxWidth()) {
            for (n in 1..5) {
                NumberKey(
                    digit           = n,
                    isActive        = n == selectedNumber,
                    onClick         = { onNumberClick(n) },
                    modifier        = Modifier.weight(1f).padding(3.dp)
                )
            }
        }
        // Fila 2: dígitos 6–9 + borrar
        Row(Modifier.fillMaxWidth()) {
            for (n in 6..9) {
                NumberKey(
                    digit           = n,
                    isActive        = n == selectedNumber,
                    onClick         = { onNumberClick(n) },
                    modifier        = Modifier.weight(1f).padding(3.dp)
                )
            }
            DeleteKey(
                onClick  = onDeleteClick,
                modifier = Modifier.weight(1f).padding(3.dp)
            )
        }
    }
}

@Composable
private fun NumberKey(
    digit: Int,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = tween(80),
        label = "key-press-$digit"
    )

    val faceColor   = if (isActive) Tertiary else SurfaceContainerHigh
    val shadowColor = if (isActive) Tertiary.copy(alpha = 0.4f) else SurfaceContainerHigh.copy(alpha = 0.6f)
    val textColor   = if (isActive) SurfaceContainerHigh else OnSurface

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.aspectRatio(0.9f)
    ) {
        // Sombra 3D (capa inferior)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(shadowColor)
        )
        // Cara del botón (capa superior, se desplaza al pulsar)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .matchParentSize()
                .offset(y = offsetY)
                .clip(RoundedCornerShape(10.dp))
                .background(faceColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    onClick           = onClick
                )
        ) {
            Text(
                text      = digit.toString(),
                style     = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color     = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DeleteKey(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = tween(80),
        label = "delete-key-press"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.aspectRatio(0.9f)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SecondaryContainer.copy(alpha = 0.5f))
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .matchParentSize()
                .offset(y = offsetY)
                .clip(RoundedCornerShape(10.dp))
                .background(SecondaryContainer)
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null,
                    onClick           = onClick
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Borrar",
                tint = Secondary
            )
        }
    }
}
