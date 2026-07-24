package com.inigo.xudoku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
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
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainer
import com.inigo.xudoku.ui.theme.Tertiary

private val difficultyIcons = DifficultyIcons(
    easy    = Icons.Outlined.SentimentSatisfied,
    medium  = Icons.Outlined.SentimentNeutral,
    hard    = Icons.Outlined.Psychology,
    extreme = Icons.Outlined.LocalFireDepartment
)

/** Dificultades visibles en la UI (HARDEST oculto hasta desbloquearse). */
private val visibleDifficulties = listOf(
    Difficulty.VERY_EASY,
    Difficulty.EASY,
    Difficulty.MEDIUM,
    Difficulty.HARD
)

/**
 * Pantalla de selección de dificultad.
 *
 * @param onDifficultySelected  Callback al elegir una dificultad.
 * @param onNavigateToStats     Navegar a Stats vía bottom nav.
 * @param onNavigateToProfile   Navegar a Profile vía bottom nav.
 */
@Composable
fun DifficultyScreen(
    onDifficultySelected: (Difficulty) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        containerColor = Background,
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
                        colors = listOf(SurfaceContainer, Background)
                    )
                )
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))

            // Logo
            Image(
                painter            = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Sudoku logo",
                modifier           = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(22.dp))
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text       = "Selecciona Dificultad",
                style      = MaterialTheme.typography.headlineMedium,
                color      = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = "PON A PRUEBA TU MENTE",
                style = MaterialTheme.typography.labelLarge,
                color = OnSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Tarjetas de dificultad
            visibleDifficulties.forEach { difficulty ->
                DifficultyCard(
                    difficulty = difficulty,
                    visuals    = difficultyVisuals(difficulty, difficultyIcons),
                    onClick    = { onDifficultySelected(difficulty) },
                    modifier   = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            // Barra de progreso global (placeholder hasta implementar persistencia)
            // TODO("conectar a persistencia")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .padding(16.dp)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text("Progreso Global", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                    Text("0%", style = MaterialTheme.typography.labelLarge, color = Tertiary)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress         = { 0f },
                    modifier         = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color            = Tertiary,
                    trackColor       = SurfaceContainer,
                    strokeCap        = StrokeCap.Round,
                    gapSize          = 0.dp,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
