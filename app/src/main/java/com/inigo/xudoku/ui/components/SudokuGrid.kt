package com.inigo.xudoku.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import com.inigo.xudoku.model.SudokuBoard
import com.inigo.xudoku.ui.CellState
import com.inigo.xudoku.ui.ScoreAnimationEvent
import com.inigo.xudoku.ui.theme.ErrorColor
import com.inigo.xudoku.ui.theme.OnSurface
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.Outline
import com.inigo.xudoku.ui.theme.OutlineVariant
import com.inigo.xudoku.ui.theme.Primary
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainerHigh
import com.inigo.xudoku.ui.theme.SurfaceContainerHighest
import com.inigo.xudoku.ui.theme.Tertiary

/**
 * Tablero 9×9 de Sudoku con los estados visuales del design system Vivid Logic.
 *
 * @param cells      Estado actual del grid (valor + isGiven + isError por celda).
 * @param notes      Notas en lápiz por celda: (row, col) → set de dígitos.
 * @param selectedCell  Coordenada de la celda actualmente seleccionada.
 * @param onCellClick   Callback al pulsar una celda.
 */
@Composable
fun SudokuGrid(
    cells: Array<Array<CellState>>,
    notes: Map<Pair<Int, Int>, Set<Int>>,
    selectedCell: Pair<Int, Int>?,
    lastScoreEvent: ScoreAnimationEvent?,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedValue = selectedCell
        ?.let { (r, c) -> cells[r][c].value.takeIf { it != SudokuBoard.EMPTY } }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(2.dp, OutlineVariant, RoundedCornerShape(8.dp))
    ) {
        Column(Modifier.fillMaxSize()) {
            for (row in 0 until SudokuBoard.SIZE) {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    for (col in 0 until SudokuBoard.SIZE) {
                        val cell = cells[row][col]
                        val isSelected = selectedCell == Pair(row, col)
                        val isHighlighted = !isSelected &&
                            selectedValue != null &&
                            selectedValue != SudokuBoard.EMPTY &&
                            cell.value == selectedValue
                        val isSameBox = selectedCell?.let { (sr, sc) ->
                            (row / 3 == sr / 3) && (col / 3 == sc / 3)
                        } ?: false
                        val isSameRowOrCol = selectedCell?.let { (sr, sc) ->
                            row == sr || col == sc
                        } ?: false

                        SudokuCell(
                            cell          = cell,
                            cellNotes     = notes[Pair(row, col)] ?: emptySet(),
                            isSelected    = isSelected,
                            isHighlighted = isHighlighted,
                            isSameArea    = !isSelected && (isSameBox || isSameRowOrCol),
                            row           = row,
                            col           = col,
                            lastScoreEvent = lastScoreEvent,
                            onClick       = { onCellClick(row, col) },
                            modifier      = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }

        // Separadores de cajas 3×3 superpuestos con Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val boxW = w / 3f
            val boxH = h / 3f
            val strokePx = 2.dp.toPx()

            listOf(1f, 2f).forEach { i ->
                drawLine(
                    color       = OutlineVariant,
                    start       = Offset(boxW * i, 0f),
                    end         = Offset(boxW * i, h),
                    strokeWidth = strokePx
                )
                drawLine(
                    color       = OutlineVariant,
                    start       = Offset(0f, boxH * i),
                    end         = Offset(w, boxH * i),
                    strokeWidth = strokePx
                )
            }
        }
    }
}

@Composable
private fun SudokuCell(
    cell: CellState,
    cellNotes: Set<Int>,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isSameArea: Boolean,
    row: Int,
    col: Int,
    lastScoreEvent: ScoreAnimationEvent?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected  -> PrimaryContainer.copy(alpha = 0.35f)
        isHighlighted -> Tertiary.copy(alpha = 0.15f)
        isSameArea  -> SurfaceContainerHigh.copy(alpha = 0.6f)
        else        -> Color.Transparent
    }

    val borderMod = if (isSelected) {
        Modifier.border(2.dp, Primary)
    } else {
        Modifier.border(0.5.dp, Outline.copy(alpha = 0.25f))
    }

    val alphaAnim = remember { Animatable(0f) }
    val offsetYAnim = remember { Animatable(0f) }
    var displayedPoints by remember { mutableStateOf("") }

    LaunchedEffect(lastScoreEvent) {
        if (lastScoreEvent != null && lastScoreEvent.row == row && lastScoreEvent.col == col) {
            displayedPoints = "+${lastScoreEvent.points}"
            launch {
                alphaAnim.snapTo(1f)
                offsetYAnim.snapTo(0f)
                
                launch {
                    offsetYAnim.animateTo(
                        targetValue = -40f,
                        animationSpec = tween(durationMillis = 600)
                    )
                }
                launch {
                    alphaAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 600, delayMillis = 200)
                    )
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .zIndex(if (alphaAnim.value > 0f) 1f else 0f)
            .background(bgColor)
            .then(borderMod)
            .clickable(onClick = onClick)
    ) {
        when {
            cell.value != SudokuBoard.EMPTY -> {
                val textColor = when {
                    cell.isError -> ErrorColor
                    cell.isGiven -> OnSurface
                    else         -> Tertiary
                }
                Text(
                    text      = cell.value.toString(),
                    color     = textColor,
                    style     = MaterialTheme.typography.titleLarge,
                    fontWeight = if (cell.isGiven) FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
            cellNotes.isNotEmpty() -> {
                NoteGrid(notes = cellNotes)
            }
        }

        if (alphaAnim.value > 0f) {
            Text(
                text = displayedPoints,
                color = Color(0xFFFFD54F), // Amarillo
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .offset(y = offsetYAnim.value.dp)
                    .alpha(alphaAnim.value)
            )
        }
    }
}

/** Mini-grid 3×3 para mostrar las notas en lápiz de una celda. */
@Composable
private fun NoteGrid(notes: Set<Int>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
    ) {
        for (noteRow in 0 until 3) {
            Row(Modifier.weight(1f)) {
                for (noteCol in 0 until 3) {
                    val digit = noteRow * 3 + noteCol + 1
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        if (digit in notes) {
                            Text(
                                text      = digit.toString(),
                                color     = OnSurfaceVariant,
                                fontSize  = 8.sp,
                                lineHeight = 8.sp,
                                textAlign = TextAlign.Center,
                                style = LocalTextStyle.current.copy(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false // clave: quita el padding extra de Android
                                    ),
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Center,
                                        trim = LineHeightStyle.Trim.Both
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(
    name           = "SudokuGrid — layout",
    showBackground = true,
    backgroundColor = 0xFF121212 // Simulando fondo oscuro
)
@Composable
fun PreviewSudokuGrid() {
    com.inigo.xudoku.ui.theme.XudokuTheme {
        val dummyCells = Array(9) { r ->
            Array(9) { c ->
                val v = if (r == c) (r + 1) else 0
                CellState(
                    value = v,
                    isGiven = v % 2 != 0,
                    isError = r == 0 && c == 8
                )
            }
        }
        val dummyNotes = mapOf(
            Pair(0, 1) to setOf(1, 2, 3),
            Pair(1, 0) to setOf(4, 5, 6, 7, 8, 9),
            Pair(2, 2) to setOf(7, 8, 9)
        )
        
        Box(modifier = Modifier.padding(16.dp)) {
            SudokuGrid(
                cells = dummyCells,
                notes = dummyNotes,
                selectedCell = Pair(4, 4),
                lastScoreEvent = null,
                onCellClick = { _, _ -> },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
