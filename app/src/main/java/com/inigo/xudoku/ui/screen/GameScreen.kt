package com.inigo.xudoku.ui.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.SudokuBoard
import com.inigo.xudoku.ui.GameViewModel
import com.inigo.xudoku.ui.components.NumberPad
import com.inigo.xudoku.ui.components.SudokuGrid
import com.inigo.xudoku.ui.theme.Background
import com.inigo.xudoku.ui.theme.ErrorColor
import com.inigo.xudoku.ui.theme.OnSecondaryContainer
import com.inigo.xudoku.ui.theme.OnSurface
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.Primary
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.SecondaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainerHigh
import com.inigo.xudoku.ui.theme.SurfaceContainerHighest
import com.inigo.xudoku.ui.theme.Tertiary

/** Label localizado de la dificultad. */
private fun Difficulty.labelEs() = when (this) {
    Difficulty.VERY_EASY -> "Fácil"
    Difficulty.EASY      -> "Medio"
    Difficulty.MEDIUM    -> "Difícil"
    Difficulty.HARD      -> "Extremo"
    Difficulty.HARDEST   -> "Imposible"
}

/** Formatea segundos en MM:SS. */
fun Int.toTimeString(): String =
    "%02d:%02d".format(this / 60, this % 60)

/**
 * Pantalla de juego activo.
 *
 * @param difficulty      Dificultad de la partida (se usa para iniciar el ViewModel).
 * @param viewModel       ViewModel con el estado mutable de la partida.
 * @param onGameCompleted Callback cuando el puzzle se completa: (seconds, mistakes, difficulty, score).
 * @param onNavigateBack  Volver a la pantalla de selección.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    difficulty: Difficulty,
    viewModel: GameViewModel,
    onGameCompleted: (seconds: Int, mistakes: Int, difficulty: Difficulty, score: Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    // Iniciar partida cuando cambie la dificultad
    LaunchedEffect(difficulty) {
        viewModel.startGame(difficulty)
    }

    val cells        by viewModel.cells.collectAsState()
    val notes        by viewModel.notes.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val isNotesMode  by viewModel.isNotesMode.collectAsState()
    val mistakes     by viewModel.mistakes.collectAsState()
    val elapsed      by viewModel.elapsedSeconds.collectAsState()
    val isCompleted  by viewModel.isCompleted.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()

    // Navegar a Victoria cuando el puzzle esté completo
    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            val score = computeScore(elapsed, mistakes, difficulty)
            onGameCompleted(elapsed, mistakes, difficulty, score)
        }
    }

    // Número activo en la celda seleccionada (para resaltar tecla del teclado)
    val selectedNumber = selectedCell?.let { (r, c) ->
        cells[r][c].value.takeIf { it != SudokuBoard.EMPTY }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    // Columna centrada: SUDOKU + chip + timer
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "SUDOKU",
                            style      = MaterialTheme.typography.headlineMedium,
                            color      = Primary,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Chip de dificultad — pill con SecondaryContainer
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(SecondaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    difficulty.labelEs(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSecondaryContainer
                                )
                            }
                            Text(
                                elapsed.toTimeString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Atrás", tint = Primary)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Settings, "Ajustes", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // HUD: errores + nivel
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("MISTAKES", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text(
                        "$mistakes/3",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = if (mistakes > 0) ErrorColor else OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Level 1", // TODO("conectar a progreso de usuario")
                        style = MaterialTheme.typography.labelLarge,
                        color = Tertiary
                    )
                    // Progress bar con gradient Tertiary→Primary
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SurfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0f) // TODO: conectar a nivel de usuario
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(Tertiary, PrimaryContainer))
                                )
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Grid 9×9
            if (isLoading) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Generando puzzle…", color = OnSurfaceVariant)
                }
            } else {
                SudokuGrid(
                    cells        = cells,
                    notes        = notes,
                    selectedCell = selectedCell,
                    onCellClick  = { r, c -> viewModel.selectCell(r, c) },
                    modifier     = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Toolbar de acciones
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    icon    = Icons.Outlined.Undo,
                    label   = "Deshacer",
                    onClick = { viewModel.undoLastMove() }
                )
                ActionButton(
                    icon    = Icons.Outlined.Backspace,
                    label   = "Borrar",
                    onClick = { viewModel.clearSelectedCell() }
                )
                // Notas con badge cuando está activo
                BadgedBox(
                    badge = {
                        if (isNotesMode) Badge(containerColor = Tertiary)
                    }
                ) {
                    ActionButton(
                        icon      = Icons.Outlined.EditNote,
                        label     = "Notas",
                        onClick   = { viewModel.toggleNotesMode() },
                        isActive  = isNotesMode,
                        modifier  = Modifier
                    )
                }
                ActionButton(
                    icon    = Icons.Outlined.Lightbulb,
                    label   = "Pista",
                    onClick = { viewModel.requestHint() }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Teclado numérico
            NumberPad(
                onNumberClick  = { viewModel.enterNumber(it) },
                onDeleteClick  = { viewModel.clearSelectedCell() },
                selectedNumber = selectedNumber,
                modifier       = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        IconButton(
            onClick  = onClick,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isActive) PrimaryContainer
                    else SurfaceContainerHigh
                )
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = if (isActive) OnSurface else OnSurfaceVariant,
                modifier           = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (isActive) Primary else OnSurfaceVariant)
    }
}

/** Calcula una puntuación simple basada en tiempo, errores y dificultad. */
private fun computeScore(elapsedSeconds: Int, mistakes: Int, difficulty: Difficulty): Int {
    val baseScore = when (difficulty) {
        Difficulty.VERY_EASY -> 1_000
        Difficulty.EASY      -> 2_000
        Difficulty.MEDIUM    -> 4_000
        Difficulty.HARD      -> 7_000
        Difficulty.HARDEST   -> 12_000
    }
    val timePenalty  = (elapsedSeconds / 10).coerceAtMost(baseScore / 2)
    val errorPenalty = mistakes * 200
    return (baseScore - timePenalty - errorPenalty).coerceAtLeast(100)
}

// ── Preview ──────────────────────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview(
    name           = "GameScreen — layout",
    showBackground = true,
    device         = "spec:width=393dp,height=851dp,dpi=420"
)
@androidx.compose.runtime.Composable
fun PreviewGameScreen() {
    com.inigo.xudoku.ui.theme.XudokuTheme {
        val vm = GameViewModel()
        GameScreen(
            difficulty      = com.inigo.xudoku.model.Difficulty.MEDIUM,
            viewModel       = vm,
            onGameCompleted = { _, _, _, _ -> },
            onNavigateBack  = {}
        )
    }
}
