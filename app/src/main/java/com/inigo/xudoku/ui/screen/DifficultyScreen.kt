package com.inigo.xudoku.ui.screen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import com.inigo.xudoku.ui.ProgressionViewModel
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inigo.xudoku.R
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.ui.components.DifficultyCard
import com.inigo.xudoku.ui.components.DifficultyIcons
import com.inigo.xudoku.ui.components.XudokuBottomBar
import com.inigo.xudoku.ui.components.XudokuTab
import com.inigo.xudoku.ui.components.difficultyVisuals
import com.inigo.xudoku.ui.theme.Background
import com.inigo.xudoku.ui.theme.OnSurface
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.Primary
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainer
import com.inigo.xudoku.ui.theme.Tertiary

private val difficultyIcons = DifficultyIcons(
    easy    = Icons.Outlined.SentimentSatisfied,
    medium  = Icons.Outlined.SentimentNeutral,
    hard    = Icons.Outlined.Psychology,
    extreme = Icons.Outlined.LocalFireDepartment
)

/** Dificultades visibles en la UI. */
private val visibleDifficulties = listOf(
    Difficulty.VERY_EASY,
    Difficulty.EASY,
    Difficulty.MEDIUM,
    Difficulty.HARD,
    Difficulty.HARDEST
)


/**
 * Pantalla de selección de dificultad.
 *
 * @param onDifficultySelected  Callback al elegir una dificultad.
 * @param onNavigateToStats     Navegar a Stats vía bottom nav.
 * @param onNavigateToProfile   Navegar a Profile vía bottom nav.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultyScreen(
    onDifficultySelected: (Difficulty) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToProfile: () -> Unit,
    progressionViewModel: ProgressionViewModel = koinViewModel()
) {
    val progressionState by progressionViewModel.state.collectAsState()
    
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SUDOKU",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = Primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    // Espacio invisible para centrar el título visualmente
                    Box(Modifier.width(48.dp))
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector        = Icons.Outlined.Settings,
                            contentDescription = "Ajustes",
                            tint               = Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            XudokuBottomBar(
                currentTab    = XudokuTab.PLAY,
                onTabSelected = { tab ->
                    when (tab) {
                        XudokuTab.STATS   -> onNavigateToStats()
                        XudokuTab.PROFILE -> onNavigateToProfile()
                        else              -> Unit
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors  = listOf(SurfaceContainer, Background),
                        center  = Offset(Float.POSITIVE_INFINITY / 2f, 0f),
                        radius  = 1200f
                    )
                )
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // Logo con halo de glow
            Box(contentAlignment = Alignment.Center) {
                // Capa de glow difuminado
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .drawBehind {
                            drawGlowCircle(color = Primary.copy(alpha = 0.25f), radius = size.minDimension * 0.85f)
                        }
                )
                Image(
                    painter            = painterResource(R.mipmap.ic_launcher),
                    contentDescription = "Sudoku logo",
                    modifier           = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(22.dp))
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text       = "Selecciona Dificultad",
                style      = MaterialTheme.typography.headlineMedium,
                color      = OnSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // Tarjetas de dificultad
            visibleDifficulties.forEach { difficulty ->
                val requiredLevel = when(difficulty) {
                    Difficulty.VERY_EASY -> 1
                    Difficulty.EASY -> 1
                    Difficulty.MEDIUM -> 2
                    Difficulty.HARD -> 5
                    Difficulty.HARDEST -> 15
                }
                val isLocked = progressionState.currentLevel < requiredLevel
                
                DifficultyCard(
                    difficulty = difficulty,
                    visuals    = difficultyVisuals(difficulty, difficultyIcons),
                    isLocked   = isLocked,
                    onClick    = { if (!isLocked) onDifficultySelected(difficulty) },
                    modifier   = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            // Barra de progreso global
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .padding(16.dp)
            ) {
                val progressPercent = if (progressionState.xpRequiredForNextLevel > 0) {
                    (progressionState.currentLevelXP.toFloat() / progressionState.xpRequiredForNextLevel)
                } else 0f
                val progressPercentInt = (progressPercent * 100).toInt()
                
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nivel ${progressionState.currentLevel}", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                    Text("$progressPercentInt%", style = MaterialTheme.typography.labelLarge, color = Tertiary)
                }
                Spacer(Modifier.height(8.dp))
                // Progress bar con gradient Tertiary→PrimaryContainer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(com.inigo.xudoku.ui.theme.SurfaceContainerHighest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressPercent)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Tertiary, PrimaryContainer)
                                )
                            )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Dibuja un círculo difuminado para simular el glow detrás del logo. */
private fun DrawScope.drawGlowCircle(color: Color, radius: Float) {
    for (i in 0..8) {
        val alpha = (1f - i / 8f) * color.alpha * 0.15f
        drawCircle(
            color  = color.copy(alpha = alpha),
            radius = radius + i * 6f
        )
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview(
    name       = "DifficultyScreen",
    showBackground = true,
    device     = "spec:width=393dp,height=851dp,dpi=420"
)
@androidx.compose.runtime.Composable
fun PreviewDifficultyScreen() {
    com.inigo.xudoku.ui.theme.XudokuTheme {
        DifficultyScreen(
            onDifficultySelected = {},
            onNavigateToStats    = {},
            onNavigateToProfile  = {}
        )
    }
}
