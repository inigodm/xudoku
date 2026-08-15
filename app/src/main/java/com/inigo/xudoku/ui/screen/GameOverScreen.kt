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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.inigo.xudoku.R
import com.inigo.xudoku.ui.components.XudokuBottomBar
import com.inigo.xudoku.ui.components.XudokuTab
import com.inigo.xudoku.ui.theme.Background
import com.inigo.xudoku.ui.theme.ErrorColor
import com.inigo.xudoku.ui.theme.ErrorContainer
import com.inigo.xudoku.ui.theme.OnErrorContainer
import com.inigo.xudoku.ui.theme.OnSurface
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.Outline
import com.inigo.xudoku.ui.theme.Primary
import com.inigo.xudoku.ui.theme.SurfaceContainerHigh
import com.inigo.xudoku.ui.theme.Tertiary
import com.inigo.xudoku.ui.theme.XudokuTheme

/**
 * Pantalla de Fin de Juego (Losing Screen) cuando el jugador agota sus oportunidades.
 *
 * @param elapsedSeconds Tiempo total transcurrido en la partida en segundos.
 * @param mistakes       Número de errores cometidos (por defecto 3).
 * @param onRetry        Callback para ver anuncio / continuar o reintentar.
 * @param onMainMenu     Callback para volver al menú principal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverScreen(
    elapsedSeconds: Int = 0,
    mistakes: Int = 3,
    onRetry: () -> Unit = {},
    onMainMenu: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))

            // Icono gráfico destacado de corazón roto
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(ErrorContainer.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector        = Icons.Outlined.HeartBroken,
                    contentDescription = null,
                    tint               = ErrorColor,
                    modifier           = Modifier.size(56.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Título y Subtítulo
            Text(
                stringResource(R.string.game_over),
                style      = MaterialTheme.typography.displayLarge,
                color      = ErrorColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.out_of_chances),
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Stats Grid (Bento style)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    icon     = Icons.Outlined.Cancel,
                    iconTint = ErrorColor,
                    label    = stringResource(R.string.mistakes_caps),
                    value    = stringResource(R.string.mistakes_formatted, mistakes),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon     = Icons.Outlined.Timer,
                    iconTint = Tertiary,
                    label    = stringResource(R.string.time_caps),
                    value    = elapsedSeconds.toTimeString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(40.dp))

            // Botón Primario — Ver Anuncio para Continuar
            Button(
                onClick  = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ErrorContainer)
            ) {
                Icon(
                    imageVector        = Icons.Outlined.PlayCircle,
                    contentDescription = null,
                    tint               = OnErrorContainer,
                    modifier           = Modifier.size(24.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    stringResource(R.string.watch_ad_to_continue),
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color      = OnErrorContainer
                )
            }

            Spacer(Modifier.height(16.dp))

            // Botón Secundario — Menú Principal
            OutlinedButton(
                onClick  = onMainMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = ErrorColor),
                border   = androidx.compose.foundation.BorderStroke(1.dp, Outline.copy(alpha = 0.3f))
            ) {
                Icon(Icons.Outlined.Home, null, tint = ErrorColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text(
                    stringResource(R.string.main_menu),
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color      = ErrorColor
                )
            }

            Spacer(Modifier.height(32.dp))
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
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerHigh)
            .border(1.dp, ErrorColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Outline)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = OnSurface, fontWeight = FontWeight.Bold)
    }
}

@Preview(
    name           = "GameOverScreen — layout",
    showBackground = true,
    device         = "spec:width=393dp,height=851dp,dpi=420"
)
@Composable
fun PreviewGameOverScreen() {
    XudokuTheme {
        GameOverScreen(
            elapsedSeconds = 765, // 12:45
            mistakes       = 3,
            onRetry        = {},
            onMainMenu     = {}
        )
    }
}
