package com.inigo.xudoku.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.inigo.xudoku.model.progression.League
import com.inigo.xudoku.model.progression.RanksList
import androidx.compose.ui.res.stringResource
import com.inigo.xudoku.R
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

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import org.koin.androidx.compose.koinViewModel
import com.inigo.xudoku.ui.ProgressionViewModel
import com.inigo.xudoku.ui.StatsViewModel

/**
 * Pantalla de perfil de usuario.
 * Datos de progresión integrados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToPlay: () -> Unit,
    onNavigateToStats: () -> Unit,
    progressionViewModel: ProgressionViewModel = koinViewModel(),
    statsViewModel: StatsViewModel = koinViewModel()
) {
    val progressionState by progressionViewModel.state.collectAsState()
    val statsState by statsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        statsViewModel.loadStats(null)
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
                        XudokuTab.PLAY  -> onNavigateToPlay()
                        XudokuTab.STATS -> onNavigateToStats()
                        else            -> Unit
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

            // ── Avatar + nombre ────────────────────────────────────────────
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

                    // Badge de nivel — superpuesto en la parte inferior del círculo
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
                    stringResource(R.string.player_default),   // TODO: nombre de usuario real
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
                val progressPercentInt = (progressPercent * 100).toInt()
                
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
                            .background(com.inigo.xudoku.ui.theme.SurfaceContainerHighest)
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

            // ── Stats cards ────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatCard(
                    label    = stringResource(R.string.games_played_caps),
                    value    = "${statsState.totalGamesPlayed}",
                    color    = Primary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                ProfileStatCard(
                    label    = stringResource(R.string.win_rate_caps),
                    value    = statsState.winRate,
                    color    = Secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Wins & Streak card ─────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatCard(
                    label    = stringResource(R.string.wins_caps),
                    value    = "${statsState.totalGamesWon}",
                    color    = Tertiary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                ProfileStatCard(
                    label    = stringResource(R.string.daily_streak_caps),
                    value    = "${progressionState.dailyStreak}",
                    color    = ErrorColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Account Settings ───────────────────────────────────────────
            Text(
                stringResource(R.string.account_settings),
                style      = MaterialTheme.typography.labelLarge,
                color      = OnSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            listOf(
                Triple(Icons.Outlined.EditNote,    stringResource(R.string.edit_profile),    Secondary),
                Triple(Icons.Outlined.Notifications, stringResource(R.string.notifications),  Tertiary),
                Triple(Icons.Outlined.Security,    stringResource(R.string.privacy_and_safety), Primary)
            ).forEach { (icon, label, color) ->
                ProfileSettingsRow(icon = icon, label = label, iconTint = color)
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(12.dp))

            // ── Log Out ────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, ErrorColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Outlined.Logout,
                    null,
                    tint     = ErrorColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.log_out),
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = ErrorColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Subcomponentes ────────────────────────────────────────────────────────────

@Composable
private fun ProfileStatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .padding(16.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProfileSettingsRow(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.14f))
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Text(
            label,
            style    = MaterialTheme.typography.bodyLarge,
            color    = OnSurface,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )
        Icon(Icons.Outlined.ChevronRight, null, tint = OnSurfaceVariant)
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
@androidx.compose.runtime.Composable
fun PreviewProfileScreen() {
    com.inigo.xudoku.ui.theme.XudokuTheme {
        ProfileScreen(
            onNavigateToPlay  = {},
            onNavigateToStats = {}
        )
    }
}
