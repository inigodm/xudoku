package com.inigo.xudoku.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

// TODO("Conectar a persistencia — todos los datos son placeholders")

/**
 * Pantalla de estadísticas (completamente estática hasta implementar persistencia).
 */
@Composable
fun StatsScreen(
    onNavigateToPlay: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val filters = listOf("Global", "Fácil", "Medio", "Difícil", "Extremo")
    var selectedFilter by remember { mutableStateOf("Global") }

    Scaffold(
        containerColor = Background,
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
            // Cabecera
            Text(
                "SUDOKU FLOW",
                style      = MaterialTheme.typography.headlineMedium,
                color      = Primary,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            // Chips de filtro
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                filters.forEach { f ->
                    FilterChip(
                        selected = f == selectedFilter,
                        onClick  = { selectedFilter = f },
                        label    = { Text(f, style = MaterialTheme.typography.labelLarge) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor    = PrimaryContainer,
                            selectedLabelColor        = OnSurface,
                            containerColor            = SurfaceContainerHigh,
                            labelColor                = OnSurfaceVariant
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Card de logros totales
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceContainerHigh)
                    .padding(24.dp)
            ) {
                Text("TOTAL ACHIEVEMENTS", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
                Text(
                    "0",
                    style      = MaterialTheme.typography.displayLarge,
                    color      = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text("Games Won", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WIN RATE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        Text("—", style = MaterialTheme.typography.headlineMedium, color = Tertiary, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("STREAK", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        Text("0", style = MaterialTheme.typography.headlineMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Gráfica de evolución
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "Evolución de Puntos",
                    style      = MaterialTheme.typography.labelLarge,
                    color      = OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainerHigh)
                        .padding(12.dp)
                ) {
                    // Placeholder vacío de gráfica
                    Canvas(Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        // Línea base
                        drawLine(
                            color       = OnSurfaceVariant.copy(alpha = 0.3f),
                            start       = Offset(0f, h),
                            end         = Offset(w, h),
                            strokeWidth = 1.dp.toPx()
                        )
                        // Texto placeholder
                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Juega partidas para ver tu evolución",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Difficulty split
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerHigh)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Difficulty Split",
                        style      = MaterialTheme.typography.labelLarge,
                        color      = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                listOf("Fácil" to Tertiary, "Medio" to Secondary, "Difícil" to Primary, "Extremo" to ErrorColor)
                    .forEach { (label, color) ->
                        Row(
                            verticalAlignment    = Alignment.CenterVertically,
                            modifier             = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurface, modifier = Modifier.width(60.dp))
                            LinearProgressIndicator(
                                progress   = { 0f },
                                modifier   = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color      = color,
                                trackColor = SurfaceContainer,
                                strokeCap  = StrokeCap.Round,
                                gapSize    = 0.dp
                            )
                            Text("0", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
            }

            Spacer(Modifier.height(16.dp))

            // Stat cards individuales
            listOf(
                Triple(Icons.Outlined.Timer, "Best Time", "—"),
                Triple(Icons.Outlined.AccessTime, "Average Time", "—"),
                Triple(Icons.Outlined.LocalFireDepartment, "Longest Streak", "0 Days")
            ).forEach { (icon, label, value) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
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
                        Icon(icon, null, tint = Secondary)
                    }
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                        Text(value, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Recent Flow
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recent Flow", style = MaterialTheme.typography.labelLarge, color = OnSurface, fontWeight = FontWeight.Bold)
                Text("View All", style = MaterialTheme.typography.labelLarge, color = Primary)
            }
            Spacer(Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerHigh)
                    .padding(24.dp)
            ) {
                Text(
                    "Sin partidas jugadas aún",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
