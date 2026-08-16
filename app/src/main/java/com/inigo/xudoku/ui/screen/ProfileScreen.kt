package com.inigo.xudoku.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inigo.xudoku.R
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.progression.League
import com.inigo.xudoku.model.progression.RanksList
import com.inigo.xudoku.ui.PointData
import com.inigo.xudoku.ui.ProgressionViewModel
import com.inigo.xudoku.ui.RecentGameUiModel
import com.inigo.xudoku.ui.StatsUiState
import com.inigo.xudoku.ui.StatsViewModel
import com.inigo.xudoku.ui.components.XudokuBottomBar
import com.inigo.xudoku.ui.components.XudokuTab
import com.inigo.xudoku.ui.theme.Background
import com.inigo.xudoku.ui.theme.ErrorColor
import com.inigo.xudoku.ui.theme.OnSurface
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.Primary
import com.inigo.xudoku.ui.theme.PrimaryContainer
import com.inigo.xudoku.ui.theme.Secondary
import com.inigo.xudoku.ui.theme.SurfaceContainer
import com.inigo.xudoku.ui.theme.SurfaceContainerHigh
import com.inigo.xudoku.ui.theme.SurfaceContainerHighest
import com.inigo.xudoku.ui.theme.Tertiary
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

/**
 * Pantalla de perfil de usuario con estadísticas integradas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToPlay: () -> Unit,
    progressionViewModel: ProgressionViewModel = koinViewModel(),
    statsViewModel: StatsViewModel = koinViewModel()
) {
    val progressionState by progressionViewModel.state.collectAsState()
    val statsState by statsViewModel.uiState.collectAsState()

    var selectedFilter by remember { mutableStateOf(StatsFilter.GLOBAL) }

    LaunchedEffect(selectedFilter) {
        statsViewModel.loadStats(selectedFilter.difficulty)
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.sudoku_flow),
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = Primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Menu, stringResource(R.string.menu), tint = Primary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Outlined.Settings, stringResource(R.string.settings), tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            XudokuBottomBar(
                currentTab    = XudokuTab.PROFILE,
                onTabSelected = { tab ->
                    when (tab) {
                        XudokuTab.PLAY -> onNavigateToPlay()
                        else           -> Unit
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Avatar + nombre + nivel ────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.fillMaxWidth()
            ) {
                // Avatar circular con glow Tertiary + badge LEVEL
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier         = Modifier.padding(bottom = 8.dp)
                ) {
                    // Halo de glow detrás del avatar
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .drawBehind {
                                drawAvatarGlow(Tertiary.copy(alpha = 0.4f))
                            }
                    )
                    // Círculo del avatar con medalla de Liga
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier         = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .border(
                                width  = 3.dp,
                                brush  = Brush.linearGradient(listOf(Tertiary, Primary)),
                                shape  = CircleShape
                            )
                            .background(SurfaceContainerHigh)
                    ) {
                        val currentLeague = progressionState.currentRank?.league ?: League.BRONZE
                        Image(
                            painter = painterResource(id = currentLeague.iconResId),
                            contentDescription = currentLeague.colorName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Sub-rank indicator (I, II, III, IV)
                        val leagueRanks = RanksList.ranks.filter { it.league == currentLeague }
                        if (leagueRanks.size > 1) {
                            val rankIndex = leagueRanks.indexOf(progressionState.currentRank).coerceAtLeast(0)
                            val numeral = listOf("I", "II", "III", "IV").getOrElse(rankIndex) { "I" }
                            Text(
                                text = numeral,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Badge de nivel superpuesto
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier         = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(listOf(Tertiary.copy(alpha = 0.9f), Primary.copy(alpha = 0.9f)))
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            stringResource(R.string.level_formatted, progressionState.currentLevel),
                            style      = MaterialTheme.typography.labelSmall,
                            color      = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    stringResource(R.string.player_default),
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color      = OnSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    progressionState.currentRank?.let { stringResource(it.nameResId).uppercase() } ?: stringResource(R.string.rookie),
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // Barra de progreso de XP
                val progressPercent = if (progressionState.xpRequiredForNextLevel > 0) {
                    (progressionState.currentLevelXP.toFloat() / progressionState.xpRequiredForNextLevel)
                } else 0f

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainer)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.xp_formatted, progressionState.currentLevelXP), style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                        Text(stringResource(R.string.xp_formatted, progressionState.xpRequiredForNextLevel), style = MaterialTheme.typography.labelMedium, color = Tertiary)
                    }
                    Spacer(Modifier.height(8.dp))
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
                                .background(Brush.horizontalGradient(colors = listOf(Tertiary, PrimaryContainer)))
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── ESTADÍSTICAS SEGUIDAS DEBAJO ───────────────────────────────

            // ── Chips de filtro ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsFilter.entries.forEach { f ->
                    val selected = f == selectedFilter
                    FilterChip(
                        selected = selected,
                        onClick  = { selectedFilter = f },
                        label    = {
                            Text(
                                stringResource(f.titleResId),
                                style      = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary.copy(alpha = 0.2f),
                            selectedLabelColor     = Primary,
                            containerColor         = SurfaceContainerHigh,
                            labelColor             = OnSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled             = true,
                            selected            = selected,
                            selectedBorderColor = Primary.copy(alpha = 0.5f),
                            borderColor         = Color.Transparent,
                            borderWidth         = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
            }

            // ── Card de logros totales ─────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(SurfaceContainerHigh, SurfaceContainer)
                        )
                    )
                    .padding(24.dp)
            ) {
                Text(
                    stringResource(R.string.total_achievements),
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${statsState.totalGamesWon}",
                    style      = MaterialTheme.typography.displayLarge,
                    color      = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.games_won),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileMiniStat(label = stringResource(R.string.win_rate), value = statsState.winRate, color = Tertiary)
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(OnSurfaceVariant.copy(alpha = 0.2f))
                    )
                    ProfileMiniStat(label = stringResource(R.string.streak), value = "${statsState.currentStreak}", color = Secondary)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Gráfica de evolución ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        Icons.Outlined.TrendingUp,
                        null,
                        tint     = Tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.points_evolution),
                        style      = MaterialTheme.typography.labelLarge,
                        color      = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerHigh)
                        .padding(12.dp)
                ) {
                    if (statsState.pointsEvolution.isNotEmpty()) {
                        ProfileStatsLineChart(dataPoints = statsState.pointsEvolution)
                    } else {
                        Text(
                            text = stringResource(R.string.not_enough_data),
                            modifier = Modifier.align(Alignment.Center),
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Difficulty Split ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerHigh)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.WorkspacePremium,
                        null,
                        tint     = Secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.difficulty_split),
                        style      = MaterialTheme.typography.labelLarge,
                        color      = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                val veryEasyCount = (statsState.difficultySplit.getOrNull(0) ?: 0f)
                val easyCount     = (statsState.difficultySplit.getOrNull(1) ?: 0f)
                val mediumCount   = (statsState.difficultySplit.getOrNull(2) ?: 0f)
                val hardCount     = (statsState.difficultySplit.getOrNull(3) ?: 0f)
                val extremeCount  = (statsState.difficultySplit.getOrNull(4) ?: 0f)

                listOf(
                    Triple(stringResource(R.string.diff_very_easy), Tertiary,   veryEasyCount),
                    Triple(stringResource(R.string.diff_easy),     Secondary,  easyCount),
                    Triple(stringResource(R.string.diff_medium),   Primary,    mediumCount),
                    Triple(stringResource(R.string.diff_hard),     ErrorColor, hardCount),
                    Triple(stringResource(R.string.diff_hardest),  OnSurface,  extremeCount)
                ).forEach { (label, color, fraction) ->
                    Row(
                        verticalAlignment    = Alignment.CenterVertically,
                        modifier             = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Text(
                            label,
                            style    = MaterialTheme.typography.bodyMedium,
                            color    = OnSurface,
                            modifier = Modifier.width(64.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceContainerHighest)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${(fraction * 100).toInt()}%",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = OnSurfaceVariant,
                            modifier = Modifier.width(56.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Stat cards individuales ────────────────────────────────────
            listOf(
                Triple(Icons.Outlined.Timer,                 stringResource(R.string.best_time),       statsState.bestTime),
                Triple(Icons.Outlined.AccessTime,            stringResource(R.string.average_time),    statsState.averageTime),
                Triple(Icons.Outlined.LocalFireDepartment,   stringResource(R.string.longest_streak),  "${statsState.longestStreak}"),
                Triple(Icons.Outlined.WorkspacePremium,      stringResource(R.string.average_score),   "${statsState.averageScore}"),
                Triple(Icons.Outlined.WorkspacePremium,      stringResource(R.string.max_score),       "${statsState.maxScore}"),
                Triple(Icons.Outlined.WorkspacePremium,      stringResource(R.string.average_xp),      stringResource(R.string.xp_formatted, statsState.averageXp)),
                Triple(Icons.Outlined.WorkspacePremium,      stringResource(R.string.max_xp),          stringResource(R.string.xp_formatted, statsState.maxXp))
            ).forEach { (icon, label, value) ->
                ProfileStatItemRow(
                    icon     = icon,
                    label    = label,
                    value    = value,
                    iconTint = Tertiary
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(4.dp))

            // ── Recent Flow ────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.recent_flow),
                    style      = MaterialTheme.typography.labelLarge,
                    color      = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { /* TODO */ }) {
                    Text(
                        stringResource(R.string.view_all),
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (statsState.recentGames.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_completed_games),
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                statsState.recentGames.forEach { game ->
                    ProfileRecentFlowRow(game = game)
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Subcomponentes ────────────────────────────────────────────────────────────

@Composable
private fun ProfileMiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(
            value,
            style      = MaterialTheme.typography.headlineMedium,
            color      = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileStatItemRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f))
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.padding(start = 12.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.Medium)
            Text(value, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun ProfileRecentFlowRow(game: RecentGameUiModel) {
    val diffLabel = stringResource(id = game.difficultyResId)
    val titleLabel = stringResource(id = R.string.game_label_formatted, diffLabel, game.sudokuId)
    val xpLabel = if (game.xp != null) stringResource(R.string.xp_earned_formatted, game.xp) else stringResource(R.string.dnf)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (game.completed) Tertiary.copy(alpha = 0.12f)
                    else ErrorColor.copy(alpha = 0.12f)
                )
        ) {
            Icon(
                imageVector = if (game.completed) Icons.Outlined.CheckCircle
                              else Icons.Outlined.AccessTime,
                contentDescription = null,
                tint       = if (game.completed) Tertiary else ErrorColor,
                modifier   = Modifier.size(20.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                titleLabel,
                style      = MaterialTheme.typography.bodyMedium,
                color      = OnSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                game.subtitle,
                style    = MaterialTheme.typography.labelSmall,
                color    = OnSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                game.time,
                style      = MaterialTheme.typography.bodyMedium,
                color      = OnSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                xpLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (game.completed) Tertiary else ErrorColor
            )
        }
    }
}

/** Gráfica de línea dinámica con datos de puntos. */
@Composable
private fun ProfileStatsLineChart(dataPoints: List<PointData>) {
    if (dataPoints.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    val minY = (dataPoints.minOfOrNull { it.score } ?: 0f).coerceAtLeast(0f)
    val maxY = (dataPoints.maxOfOrNull { it.score } ?: 100f).coerceAtLeast(minY + 10f)

    val range = maxY - minY
    val yStep = range / 3
    val yLabels = listOf(
        "${(maxY).toInt()}",
        "${(minY + yStep * 2).toInt()}",
        "${(minY + yStep).toInt()}",
        "${(minY).toInt()}"
    )

    val xLabels = dataPoints.indices.map { "${it + 1}" }

    val tooltipTexts = dataPoints.map { data ->
        stringResource(R.string.tooltip_score_xp, data.score.toInt(), data.xp)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier              = Modifier
                .align(Alignment.TopStart)
                .padding(start = 0.dp, end = 4.dp),
            verticalArrangement   = Arrangement.SpaceBetween
        ) {
            yLabels.forEach { label ->
                Text(
                    label,
                    style    = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                    color    = OnSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 28.dp, bottom = 18.dp, top = 4.dp, end = 4.dp)
                .pointerInput(dataPoints) {
                    detectTapGestures { offset ->
                        val n = dataPoints.size
                        val stepX = if (n > 1) size.width / (n - 1).toFloat() else size.width / 2f
                        val index = (offset.x / stepX).roundToInt()
                        if (index in dataPoints.indices) {
                            selectedPointIndex = index
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val n = dataPoints.size
            val stepX = if (n > 1) w / (n - 1).toFloat() else w / 2f

            val hSteps = 4
            for (i in 0 until hSteps) {
                val yVal = minY + (range / (hSteps - 1)) * i
                val yPos = h - ((yVal - minY) / (maxY - minY)) * h
                if (yPos.isNaN()) continue
                drawLine(
                    color       = OnSurfaceVariant.copy(alpha = 0.12f),
                    start       = Offset(0f, yPos),
                    end         = Offset(w, yPos),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val linePath = Path()
            dataPoints.forEachIndexed { i, data ->
                val x = i * stepX
                val y = h - ((data.score - minY) / (maxY - minY)) * h
                if (i == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
            }

            val fillPath = Path().apply {
                addPath(linePath)
                lineTo((dataPoints.size - 1) * stepX, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path  = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Tertiary.copy(alpha = 0.25f), Color.Transparent)
                )
            )

            drawPath(
                path  = linePath,
                brush = Brush.linearGradient(
                    colors = listOf(Tertiary, Primary)
                ),
                style = Stroke(
                    width    = 2.5.dp.toPx(),
                    cap      = StrokeCap.Round,
                    join     = StrokeJoin.Round
                )
            )

            dataPoints.forEachIndexed { i, data ->
                val x = i * stepX
                val y = h - ((data.score - minY) / (maxY - minY)) * h

                val isSelected = selectedPointIndex == i
                val outerRadius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx()
                val innerRadius = if (isSelected) 3.dp.toPx() else 2.dp.toPx()

                drawCircle(color = Tertiary, radius = outerRadius, center = Offset(x, y))
                drawCircle(color = Background, radius = innerRadius, center = Offset(x, y))

                if (isSelected) {
                    val tooltipText = tooltipTexts[i]
                    val textLayoutResult = textMeasurer.measure(
                        text = tooltipText,
                        style = TextStyle(color = Background, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )

                    val padding = 8.dp.toPx()
                    val tooltipWidth = textLayoutResult.size.width + padding * 2
                    val tooltipHeight = textLayoutResult.size.height + padding * 2

                    val tooltipX = (x - tooltipWidth / 2).coerceIn(0f, w - tooltipWidth)
                    val tooltipY = (y - tooltipHeight - 12.dp.toPx()).coerceAtLeast(0f)

                    drawRoundRect(
                        color = OnSurface,
                        topLeft = Offset(tooltipX, tooltipY),
                        size = Size(tooltipWidth, tooltipHeight),
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )

                    drawText(
                        textMeasurer = textMeasurer,
                        text = tooltipText,
                        style = TextStyle(color = Background, fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        topLeft = Offset(tooltipX + padding, tooltipY + padding)
                    )
                }
            }
        }

        Row(
            modifier              = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 28.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            xLabels.forEach { label ->
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                    color = OnSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/** Efecto de glow radial detrás del avatar. */
private fun DrawScope.drawAvatarGlow(color: Color) {
    val center = Offset(size.width / 2f, size.height / 2f)
    for (i in 0..6) {
        drawCircle(
            color  = color.copy(alpha = color.alpha * (1f - i / 6f) * 0.3f),
            radius = size.minDimension / 2f + i * 8f,
            center = center
        )
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview(
    name           = "ProfileScreen",
    showBackground = true,
    device         = "spec:width=393dp,height=851dp,dpi=420"
)
@Composable
fun PreviewProfileScreen() {
    com.inigo.xudoku.ui.theme.XudokuTheme {
        ProfileScreen(
            onNavigateToPlay = {}
        )
    }
}
