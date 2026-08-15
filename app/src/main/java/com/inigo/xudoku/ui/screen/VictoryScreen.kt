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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp
import com.inigo.xudoku.R
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.ui.components.XudokuBottomBar
import com.inigo.xudoku.ui.components.XudokuTab
import com.inigo.xudoku.ui.theme.Background
import com.inigo.xudoku.ui.theme.ErrorColor
import com.inigo.xudoku.ui.theme.OnSurface
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.Primary
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.Secondary
import com.inigo.xudoku.ui.theme.SecondaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainer
import com.inigo.xudoku.ui.theme.SurfaceContainerHigh
import com.inigo.xudoku.ui.theme.SurfaceContainerHighest
import com.inigo.xudoku.ui.theme.Tertiary
import kotlin.math.sin
import kotlin.random.Random

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import com.inigo.xudoku.ui.ProgressionViewModel

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VictoryScreen(
    elapsedSeconds: Int,
    mistakes: Int,
    difficulty: Difficulty,
    score: Int,
    isNewHighScore: Boolean,
    hasLeveledUp: Boolean = false,
    hasRankedUp: Boolean = false,
    earnedXP: Int = 0,
    onNextLevel: () -> Unit,
    onMainMenu: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    progressionViewModel: ProgressionViewModel = koinViewModel()
) {
    val progressionState by progressionViewModel.state.collectAsState()
    
    VictoryScreenContent(
        elapsedSeconds = elapsedSeconds,
        mistakes = mistakes,
        difficulty = difficulty,
        score = score,
        isNewHighScore = isNewHighScore,
        hasLeveledUp = hasLeveledUp,
        hasRankedUp = hasRankedUp,
        earnedXP = earnedXP,
        onNextLevel = onNextLevel,
        onMainMenu = onMainMenu,
        onNavigateToProfile = onNavigateToProfile,
        progressionState = progressionState
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun VictoryScreenContent(
    elapsedSeconds: Int,
    mistakes: Int,
    difficulty: Difficulty,
    score: Int,
    isNewHighScore: Boolean,
    hasLeveledUp: Boolean = false,
    hasRankedUp: Boolean = false,
    earnedXP: Int = 0,
    onNextLevel: () -> Unit,
    onMainMenu: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    progressionState: com.inigo.xudoku.ui.ProgressionUIState
) {
    var showLevelUpDialog by remember { mutableStateOf(hasLeveledUp) }
    var showRankUpDialog by remember { mutableStateOf(hasRankedUp) }

    if (showLevelUpDialog) {
        AlertDialog(
            onDismissRequest = { showLevelUpDialog = false },
            title = { Text(stringResource(R.string.level_up), color = Primary, fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.level_up_desc_formatted, progressionState.currentLevel), color = OnSurface) },
            confirmButton = {
                Button(
                    onClick = { showLevelUpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(stringResource(R.string.great), color = Background)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    if (showRankUpDialog && !showLevelUpDialog) {
        AlertDialog(
            onDismissRequest = { showRankUpDialog = false },
            title = { Text(stringResource(R.string.rank_up), color = Secondary, fontWeight = FontWeight.Black) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.rank_up_desc), color = OnSurface, modifier = Modifier.padding(bottom = 16.dp))
                    
                    progressionState.currentRank?.let { rank ->
                        val currentLeague = rank.league
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                            Image(
                                painter = painterResource(id = currentLeague.iconResId),
                                contentDescription = currentLeague.colorName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            val leagueRanks = com.inigo.xudoku.model.progression.RanksList.ranks.filter { it.league == currentLeague }
                            if (leagueRanks.size > 1) {
                                val rankIndex = leagueRanks.indexOf(rank).coerceAtLeast(0)
                                val numeral = listOf("I", "II", "III", "IV").getOrElse(rankIndex) { "I" }
                                Text(
                                    text = numeral,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 8.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            // Epic confetti directly over the medal!
                            ConfettiLayer()
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = rank.nameResId).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRankUpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                ) {
                    Text(stringResource(R.string.awesome), color = Background)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.sudoku_title),
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = Primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMainMenu) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(R.string.back), tint = Primary)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Settings, stringResource(R.string.settings), tint = Primary)
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
                        XudokuTab.PLAY    -> onMainMenu()
                        XudokuTab.PROFILE -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
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
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(16.dp))

                // Badge "NUEVA MARCA"
                if (isNewHighScore) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(SecondaryContainer)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Outlined.EmojiEvents, null, tint = Secondary, modifier = Modifier.size(18.dp))
                        Text(
                            "  ${stringResource(R.string.new_record)}",
                            style      = MaterialTheme.typography.labelLarge,
                            color      = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    stringResource(R.string.victory),
                    style      = MaterialTheme.typography.displayLarge,
                    color      = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.level_completed_successfully),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Tertiary
                )

                Spacer(Modifier.height(24.dp))

                // Card de puntuación final — glass con gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SecondaryContainer.copy(alpha = 0.35f),
                                    SurfaceContainer.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Stars, null, tint = Secondary, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.final_score),
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurfaceVariant
                        )
                        Text(
                            "%,d".format(score),
                            style      = MaterialTheme.typography.displayLarge,
                            color      = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+ %,d XP".format(earnedXP),
                            style = MaterialTheme.typography.titleMedium,
                            color = Secondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Stats: tiempo y errores
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon     = Icons.Outlined.Timer,
                        iconTint = Tertiary,
                        label    = stringResource(R.string.time_caps),
                        value    = elapsedSeconds.toTimeString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon     = Icons.Outlined.Cancel,
                        iconTint = if (mistakes == 0) Tertiary else ErrorColor,
                        label    = stringResource(R.string.mistakes_caps),
                        value    = stringResource(R.string.mistakes_formatted, mistakes),
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
                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(stringResource(R.string.difficulty_caps), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        Text(
                            difficulty.toFriendlyString(),
                            style      = MaterialTheme.typography.headlineMedium,
                            color      = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Dots de dificultad
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val filledDots = when (difficulty) {
                            Difficulty.VERY_EASY -> 1
                            Difficulty.EASY      -> 2
                            Difficulty.MEDIUM    -> 3
                            Difficulty.HARD      -> 4
                            Difficulty.HARDEST   -> 4
                        }
                        repeat(4) { i ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(if (i < filledDots) Secondary else OnSurfaceVariant.copy(alpha = 0.3f))
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Barra de nivel/XP
                Column(modifier = Modifier.fillMaxWidth()) {
                    val progressPercent = if (progressionState.xpRequiredForNextLevel > 0) {
                        (progressionState.currentLevelXP.toFloat() / progressionState.xpRequiredForNextLevel)
                    } else 0f
                    
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.level_only_formatted, progressionState.currentLevel), style = MaterialTheme.typography.labelLarge, color = Primary, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.xp_progress_formatted, progressionState.currentLevelXP, progressionState.xpRequiredForNextLevel), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressPercent)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(Tertiary, PrimaryContainer))
                                )
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Botón primario — Siguiente Nivel
                Button(
                    onClick  = onNextLevel,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                ) {
                    Text(
                        stringResource(R.string.next_level),
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color      = OnSurface
                    )
                    Spacer(Modifier.size(8.dp))
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, tint = OnSurface, modifier = Modifier.size(18.dp))
                }

                Spacer(Modifier.height(12.dp))

                // Botón secundario — Menú principal
                OutlinedButton(
                    onClick  = onMainMenu,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, OnSurfaceVariant.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Outlined.Home, null, modifier = Modifier.size(18.dp))
                    Text("  ${stringResource(R.string.main_menu)}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .padding(16.dp)
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = OnSurface, fontWeight = FontWeight.Bold)
    }
}

/** Capa de confeti animado con Canvas. Partículas de colores cian y violeta. */
@Composable
private fun ConfettiLayer() {
    val particles = remember {
        List(40) {
            ConfettiParticle(
                x     = Random.nextFloat(),
                y     = Random.nextFloat() * -0.5f,
                color = when (Random.nextInt(4)) {
                    0    -> Primary
                    1    -> Secondary
                    2    -> Tertiary
                    else -> PrimaryContainer
                },
                size  = Random.nextFloat() * 10f + 5f,
                speed = Random.nextFloat() * 0.002f + 0.001f,
                angle = Random.nextFloat() * 360f
            )
        }
    }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        // Animamos a un valor muy alto para que sea infinito (duración de ~22 horas a la misma velocidad)
        anim.animateTo(10000f, tween(80_000_000, easing = LinearEasing))
    }

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val yPos = ((p.y + anim.value * p.speed * 800f) % 1.2f) * size.height
            val xPos = (p.x + sin((anim.value * 3f + p.angle) * 0.05f) * 0.05f) * size.width
            drawRect(
                color   = p.color,
                topLeft = androidx.compose.ui.geometry.Offset(xPos, yPos),
                size    = androidx.compose.ui.geometry.Size(p.size, p.size * 0.6f),
                alpha   = 0.8f
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float, val y: Float, val color: Color,
    val size: Float, val speed: Float, val angle: Float
)

@Composable
private fun Difficulty.toFriendlyString(): String = when (this) {
    Difficulty.VERY_EASY -> stringResource(R.string.diff_very_easy)
    Difficulty.EASY      -> stringResource(R.string.diff_easy)
    Difficulty.MEDIUM    -> stringResource(R.string.diff_medium)
    Difficulty.HARD      -> stringResource(R.string.diff_hard)
    Difficulty.HARDEST   -> stringResource(R.string.diff_hardest)
}

// ── Preview ──────────────────────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview(
    name           = "VictoryScreen",
    showBackground = true,
    device         = "spec:width=393dp,height=851dp,dpi=420"
)
@androidx.compose.runtime.Composable
fun PreviewVictoryScreen() {
    com.inigo.xudoku.ui.theme.XudokuTheme {
        VictoryScreenContent(
            elapsedSeconds = 525,       // 08:45
            mistakes       = 0,
            difficulty     = Difficulty.MEDIUM,
            score          = 24_580,
            isNewHighScore = true,
            hasLeveledUp   = true,
            hasRankedUp    = true,
            earnedXP       = 14500,
            onNextLevel    = {},
            onMainMenu     = {},
            progressionState = com.inigo.xudoku.ui.ProgressionUIState(
                totalXP = 15000,
                currentLevel = 10,
                currentLevelXP = 500,
                xpRequiredForNextLevel = 1000,
                currentRank = com.inigo.xudoku.model.progression.RanksList.ranks[9], // Gold II
                dailyStreak = 1,
                winStreak = 3,
                prestigeStars = 0,
                lastPlayDate = 0L
            )
        )
    }
}
