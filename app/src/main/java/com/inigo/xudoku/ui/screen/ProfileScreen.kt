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

// TODO("Conectar a datos de usuario — todos los datos son placeholders")

/**
 * Pantalla de perfil de usuario.
 * Datos completamente estáticos hasta implementar autenticación/persistencia.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToPlay: () -> Unit,
    onNavigateToStats: () -> Unit
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
                    // Círculo del avatar
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
                        // Iniciales del usuario — se reemplazará por imagen real
                        Text(
                            "AG",
                            style      = MaterialTheme.typography.headlineLarge,
                            color      = Primary,
                            fontWeight = FontWeight.Bold
                        )
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
                            "LEVEL 1",
                            style      = MaterialTheme.typography.labelSmall,
                            color      = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Jugador",   // TODO: nombre de usuario real
                    style      = MaterialTheme.typography.headlineLarge,
                    color      = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "NOVATO",    // TODO: rango basado en XP
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Stats cards ────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(
                    label    = "GAMES PLAYED",
                    value    = "0",
                    color    = Tertiary,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    label    = "WIN RATE",
                    value    = "—",
                    color    = Secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Streak card ────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerHigh)
                    .padding(16.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "DAILY STREAK",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        "0 Days",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainer)
                ) {
                    Icon(
                        Icons.Outlined.LocalFireDepartment,
                        null,
                        tint     = ErrorColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Account Settings ───────────────────────────────────────────
            Text(
                "ACCOUNT SETTINGS",
                style      = MaterialTheme.typography.labelLarge,
                color      = OnSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            listOf(
                Triple(Icons.Outlined.EditNote,    "Edit Profile",    Secondary),
                Triple(Icons.Outlined.Notifications,"Notifications",  Tertiary),
                Triple(Icons.Outlined.Security,    "Privacy & Safety", Primary)
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
                    "Log Out",
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
