package com.inigo.xudoku.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Menu
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.roundToInt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.inigo.xudoku.ui.theme.SurfaceContainerLow
import com.inigo.xudoku.ui.theme.Tertiary

import com.inigo.xudoku.ui.StatsUiState
import com.inigo.xudoku.ui.StatsViewModel
import com.inigo.xudoku.ui.RecentGameUiModel

/**
 * Pantalla de estadísticas.
 * Datos completamente estáticos hasta implementar persistencia.
 */
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onNavigateToPlay: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val filters = listOf("Global", "Fácil", "Medio", "Difícil", "Extremo", "Imposible")
    var selectedFilter by remember { mutableStateOf("Global") }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(selectedFilter) {
        viewModel.loadStats(selectedFilter)
    }

    StatsScreenContent(
        state = state,
        filters = filters,
        selectedFilter = selectedFilter,
        onFilterSelected = { selectedFilter = it },
        onNavigateToPlay = onNavigateToPlay,
        onNavigateToProfile = onNavigateToProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreenContent(
    state: StatsUiState,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onNavigateToPlay: () -> Unit,
    onNavigateToProfile: () -> Unit
) {

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SUDOKU FLOW",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = Primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Menu, "Menú", tint = Primary)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Settings, "Ajustes", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            XudokuBottomBar(
                currentTab    = XudokuTab.STATS,
                onTabSelected = { tab ->
                    when (tab) {
                        XudokuTab.PLAY    -> onNavigateToPlay()
                        XudokuTab.PROFILE -> onNavigateToProfile()
                        else              -> Unit
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

            // ── Chips de filtro ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { f ->
                    val selected = f == selectedFilter
                    FilterChip(
                        selected = selected,
                        onClick  = { onFilterSelected(f) },
                        label    = {
                            Text(
                                f,
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
                            enabled          = true,
                            selected         = selected,
                            selectedBorderColor = Primary.copy(alpha = 0.5f),
                            borderColor      = Color.Transparent,
                            borderWidth      = 1.dp,
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
                    "TOTAL ACHIEVEMENTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${state.totalGamesWon}",
                    style      = MaterialTheme.typography.displayLarge,
                    color      = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Games Won",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MiniStat(label = "WIN RATE", value = state.winRate, color = Tertiary)
                    // Separador vertical
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(OnSurfaceVariant.copy(alpha = 0.2f))
                    )
                    MiniStat(label = "STREAK", value = "${state.currentStreak}", color = Secondary)
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
                        "Evolución de Puntos",
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
                    if (state.pointsEvolution.isNotEmpty()) {
                        StatsLineChart(dataPoints = state.pointsEvolution)
                    } else {
                        Text(
                            text = "No hay datos suficientes",
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
                        "Difficulty Split",
                        style      = MaterialTheme.typography.labelLarge,
                        color      = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                val veryEasyCount = (state.difficultySplit.getOrNull(0) ?: 0f)
                val easyCount     = (state.difficultySplit.getOrNull(1) ?: 0f)
                val mediumCount   = (state.difficultySplit.getOrNull(2) ?: 0f)
                val hardCount     = (state.difficultySplit.getOrNull(3) ?: 0f)
                val extremeCount  = (state.difficultySplit.getOrNull(4) ?: 0f)

                listOf(
                    Triple("Fácil",     Tertiary,   veryEasyCount),
                    Triple("Medio",     Secondary,  easyCount),
                    Triple("Difícil",   Primary,    mediumCount),
                    Triple("Extremo",   ErrorColor, hardCount),
                    Triple("Imposible", OnSurface, extremeCount)
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
                                    .fillMaxWidth(fraction as Float)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color as Color)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${(fraction as Float * 100).toInt()}%",
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
                Triple(Icons.Outlined.Timer,                 "Best Time",       state.bestTime),
                Triple(Icons.Outlined.AccessTime,            "Average Time",    state.averageTime),
                Triple(Icons.Outlined.LocalFireDepartment,   "Longest Streak",  "${state.longestStreak} Games")
            ).forEach { (icon, label, value) ->
                StatRow(
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
                    "Recent Flow",
                    style      = MaterialTheme.typography.labelLarge,
                    color      = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "View All",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary
                )
            }

            Spacer(Modifier.height(8.dp))

            if (state.recentGames.isEmpty()) {
                Text(
                    text = "Aún no has completado partidas.",
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                state.recentGames.forEach { game ->
                    RecentFlowRow(game = game)
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Subcomponentes ────────────────────────────────────────────────────────────

@Composable
private fun MiniStat(label: String, value: String, color: Color) {
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
private fun StatRow(
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
private fun RecentFlowRow(game: RecentGameUiModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Icono check/error
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
        // Label + fecha
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                game.label,
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
        // Tiempo + XP
        Column(horizontalAlignment = Alignment.End) {
            Text(
                game.time,
                style      = MaterialTheme.typography.bodyMedium,
                color      = OnSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                game.xp,
                style = MaterialTheme.typography.labelSmall,
                color = if (game.completed) Tertiary else ErrorColor
            )
        }
    }
}

/** Gráfica de línea dinámica con datos de puntos. */
@Composable
private fun StatsLineChart(dataPoints: List<Float>) {
    if (dataPoints.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    val minY = (dataPoints.minOrNull() ?: 0f).coerceAtLeast(0f)
    val maxY = (dataPoints.maxOrNull() ?: 100f).coerceAtLeast(minY + 10f)
    
    // Create roughly evenly spaced Y labels
    val range = maxY - minY
    val yStep = range / 3
    val yLabels = listOf(
        "${(maxY).toInt()}",
        "${(minY + yStep * 2).toInt()}",
        "${(minY + yStep).toInt()}",
        "${(minY).toInt()}"
    )

    // X Labels are just indices for the recent games
    val xLabels = dataPoints.indices.map { "${it + 1}" }

    Box(modifier = Modifier.fillMaxSize()) {
        // Ejes Y (etiquetas a la izquierda)
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

        // Canvas de la línea
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

            // Grid lines horizontales
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

            // Path de la línea
            val linePath = Path()
            dataPoints.forEachIndexed { i, value ->
                val x = i * stepX
                val y = h - ((value - minY) / (maxY - minY)) * h
                if (i == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
            }

            // Fill gradient bajo la línea
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

            // Línea principal con gradient Tertiary→Primary
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

            // Puntos en la línea y Tooltip
            dataPoints.forEachIndexed { i, value ->
                val x = i * stepX
                val y = h - ((value - minY) / (maxY - minY)) * h
                
                val isSelected = selectedPointIndex == i
                val outerRadius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx()
                val innerRadius = if (isSelected) 3.dp.toPx() else 2.dp.toPx()
                
                drawCircle(color = Tertiary, radius = outerRadius, center = Offset(x, y))
                drawCircle(color = Background, radius = innerRadius, center = Offset(x, y))
                
                if (isSelected) {
                    val score = value.toInt()
                    val tooltipText = "Score: $score\n+$score XP"
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

        // Ejes X (etiquetas en la parte inferior)
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

// ── Preview ──────────────────────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview(
    name           = "StatsScreen",
    showBackground = true,
    device         = "spec:width=393dp,height=851dp,dpi=420"
)
@androidx.compose.runtime.Composable
fun PreviewStatsScreen() {
    com.inigo.xudoku.ui.theme.XudokuTheme {
        StatsScreenContent(
            state = StatsUiState(
                isLoading = false,
                totalGamesWon = 15,
                winRate = "75%",
                currentStreak = 3,
                longestStreak = 5,
                pointsEvolution = listOf(100f, 120f, 90f, 150f, 160f, 140f),
                bestTime = "04:30",
                averageTime = "06:15",
                recentGames = listOf(
                    RecentGameUiModel("Medium #1", "NOV 12", "05:00", "+120 XP", true),
                    RecentGameUiModel("Hard #2", "NOV 11", "10:00", "DNF", false)
                ),
                difficultySplit = listOf(0.4f, 0.2f, 0.2f, 0.1f, 0.1f)
            ),
            filters = listOf("Global", "Fácil", "Medio", "Difícil", "Extremo", "Imposible"),
            selectedFilter = "Global",
            onFilterSelected = {},
            onNavigateToPlay    = {},
            onNavigateToProfile = {}
        )
    }
}
