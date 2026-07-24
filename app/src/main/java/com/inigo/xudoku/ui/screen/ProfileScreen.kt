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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.inigo.xudoku.ui.theme.Secondary
import com.inigo.xudoku.ui.theme.SecondaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainer
import com.inigo.xudoku.ui.theme.SurfaceContainerHigh
import com.inigo.xudoku.ui.theme.Tertiary

// TODO("Conectar a datos de usuario — todos los datos son placeholders")

/**
 * Pantalla de perfil de usuario (completamente estática hasta implementar autenticación).
 */
@Composable
fun ProfileScreen(
    onNavigateToPlay: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    Scaffold(
        containerColor = Background,
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
            // Cabecera
            Text(
                "SUDOKU FLOW",
                style      = MaterialTheme.typography.headlineMedium,
                color      = Primary,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Avatar + nombre
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar circular con borde cian y badge de nivel
                Box(contentAlignment = Alignment.BottomCenter) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(3.dp, Tertiary, CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Text(
                            "AG",
                            style      = MaterialTheme.typography.headlineLarge,
                            color      = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Badge de nivel
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SecondaryContainer)
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "LEVEL 1", // TODO
                            style      = MaterialTheme.typography.labelSmall,
                            color      = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "Jugador", // TODO: nombre de usuario
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "NOVATO",  // TODO: rango
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            // Stats cards: partidas jugadas + win rate
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainerHigh)
                        .padding(16.dp)
                ) {
                    Text("GAMES PLAYED", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text("0", style = MaterialTheme.typography.headlineMedium, color = Tertiary, fontWeight = FontWeight.Bold)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainerHigh)
                        .padding(16.dp)
                ) {
                    Text("WIN RATE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text("—", style = MaterialTheme.typography.headlineMedium, color = Secondary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Streak card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerHigh)
                    .padding(16.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text("DAILY STREAK", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Text("0 Days", style = MaterialTheme.typography.headlineMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainer)
                ) {
                    Icon(Icons.Outlined.LocalFireDepartment, null, tint = ErrorColor)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Account settings
            Text(
                "ACCOUNT SETTINGS",
                style      = MaterialTheme.typography.labelLarge,
                color      = OnSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            listOf(
                Triple(Icons.Outlined.EditNote, "Edit Profile", Secondary),
                Triple(Icons.Outlined.Notifications, "Notifications", Tertiary),
                Triple(Icons.Outlined.Security, "Privacy & Safety", Primary)
            ).forEach { (icon, label, color) ->
                SettingsRow(icon = icon, label = label, iconTint = color)
            }

            Spacer(Modifier.height(12.dp))

            // Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, ErrorColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Icon(Icons.Outlined.Logout, null, tint = ErrorColor, modifier = Modifier.size(22.dp))
                Text(
                    "  Log Out",
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = ErrorColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHigh)
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.15f))
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Text(
            label,
            style    = MaterialTheme.typography.bodyLarge,
            color    = OnSurface,
            modifier = Modifier.padding(start = 12.dp).weight(1f)
        )
        Icon(Icons.Outlined.ChevronRight, null, tint = OnSurfaceVariant)
    }
}
