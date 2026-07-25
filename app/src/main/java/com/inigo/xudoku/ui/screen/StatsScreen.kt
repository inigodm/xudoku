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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

// TODO("Conectar a persistencia — todos los datos son placeholders")

// Datos de muestra representativos (se reemplazarán por persistencia real)
private data class RecentGame(
    val label: String,
    val subtitle: String,
    val time: String,
    val xp: String,
    val completed: Boolean
)

private val sampleRecentGames = listOf(
    RecentGame("Hard #402",   "YESTERDAY, 9:20 PM", "12:45", "+150 XP", true),
    RecentGame("Medium #891", "NOV 12, 11:45 AM",   "07:22", "+85 XP",  true),
    RecentGame("Expert #12",  "NOV 11, 4:30 PM",    "--:--", "DNF",     false)
)

/**
 * Pantalla de estadísticas.
 * Datos completamente estáticos hasta implementar persistencia.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateToPlay: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val filters = listOf("Global", "Fácil", "Medio", "Difícil", "Extremo")
    var selectedFilter by remember { mutableStateOf("Global") }

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
                        onClick  = { selectedFilter = f },
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
                    "0",
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
                    MiniStat(label = "WIN RATE", value = "—", color = Tertiary)
                    // Separador vertical
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(OnSurfaceVariant.copy(alpha = 0.2f))
                    )
                    MiniStat(label = "STREAK", value = "0", color = Secondary)
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
                    StatsLineChart()
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
                listOf(
                    Triple("Fácil",  Tertiary,   "0 games"),
                    Triple("Medio",  Secondary,  "0 games"),
                    Triple("Difícil", Primary,   "0 games"),
                    Triple("Extremo", ErrorColor, "0 games")
                ).forEach { (label, color, count) ->
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
                                    .fillMaxWidth(0f)   // TODO: fracción real
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            count,
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
                Triple(Icons.Outlined.Timer,                 "Best Time",       "—"),
                Triple(Icons.Outlined.AccessTime,            "Average Time",    "—"),
                Triple(Icons.Outlined.LocalFireDepartment,   "Longest Streak",  "0 Days")
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

            sampleRecentGames.forEach { game ->
                RecentFlowRow(game = game)
                Spacer(Modifier.height(6.dp))
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
private fun RecentFlowRow(game: RecentGame) {
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

/** Gráfica de línea placeholder con datos de muestra. Se conectará a persistencia. */
@Composable
private fun StatsLineChart() {
    // Datos de muestra — TODO: conectar a persistencia
    val dataPoints = listOf(10f, 11.5f, 13f, 12f, 15f, 17f, 16f, 19f, 21f, 22.5f)
    val minY = 10f
    val maxY = 25f
    val yLabels = listOf("25k", "20k", "15k", "10k")
    val xLabels = listOf("G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "G10")

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
        ) {
            val w = size.width
            val h = size.height
            val n = dataPoints.size
            val stepX = w / (n - 1).toFloat()

            // Grid lines horizontales
            listOf(10f, 15f, 20f, 25f).forEach { y ->
                val yPos = h - ((y - minY) / (maxY - minY)) * h
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

            // Puntos en la línea
            dataPoints.forEachIndexed { i, value ->
                val x = i * stepX
                val y = h - ((value - minY) / (maxY - minY)) * h
                drawCircle(color = Tertiary, radius = 3.5.dp.toPx(), center = Offset(x, y))
                drawCircle(color = Background, radius = 2.dp.toPx(), center = Offset(x, y))
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
        StatsScreen(
            onNavigateToPlay    = {},
            onNavigateToProfile = {}
        )
    }
}
