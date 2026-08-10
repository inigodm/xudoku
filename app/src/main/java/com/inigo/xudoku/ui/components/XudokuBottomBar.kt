package com.inigo.xudoku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.inigo.xudoku.R
import com.inigo.xudoku.ui.theme.OnSecondaryContainer
import com.inigo.xudoku.ui.theme.OnSurfaceVariant
import com.inigo.xudoku.ui.theme.SecondaryContainer
import com.inigo.xudoku.ui.theme.SurfaceContainerLow

/** Tabs de la barra de navegación inferior. */
enum class XudokuTab(@StringRes val labelResId: Int) {
    PLAY(R.string.tab_play),
    STATS(R.string.tab_stats),
    BADGES(R.string.tab_badges),
    PROFILE(R.string.tab_profile)
}

/**
 * Barra de navegación inferior global.
 * Badges está deshabilitado (no hay pantalla diseñada aún).
 *
 * @param currentTab     Tab actualmente activo.
 * @param onTabSelected  Callback al pulsar un tab.
 */
@Composable
fun XudokuBottomBar(
    currentTab: XudokuTab,
    onTabSelected: (XudokuTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = SurfaceContainerLow,
        modifier       = modifier
    ) {
        XudokuTab.entries.forEach { tab ->
            val selected = tab == currentTab
            val disabled = tab == XudokuTab.BADGES
            val label = stringResource(id = tab.labelResId)

            NavigationBarItem(
                selected = selected,
                onClick  = { if (!disabled) onTabSelected(tab) },
                enabled  = !disabled,
                label    = {
                    Text(
                        text  = label,
                        modifier = if (disabled) Modifier.alpha(0.4f) else Modifier
                    )
                },
                icon = {
                    val icon = when (tab) {
                        XudokuTab.PLAY    -> Icons.Outlined.GridView
                        XudokuTab.STATS   -> Icons.Outlined.Leaderboard
                        XudokuTab.BADGES  -> Icons.Outlined.EmojiEvents
                        XudokuTab.PROFILE -> Icons.Outlined.Person
                    }
                    if (selected) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SecondaryContainer)
                        ) {
                            Icon(
                                imageVector        = icon,
                                contentDescription = label,
                                tint               = OnSecondaryContainer
                            )
                        }
                    } else {
                        Icon(
                            imageVector        = icon,
                            contentDescription = label,
                            tint               = if (disabled) OnSurfaceVariant.copy(alpha = 0.4f)
                                                 else OnSurfaceVariant,
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color.Unspecified,
                    selectedTextColor   = OnSecondaryContainer,
                    indicatorColor      = Color.Transparent, // usamos el Box circular propio
                    unselectedIconColor = OnSurfaceVariant,
                    unselectedTextColor = OnSurfaceVariant
                )
            )
        }
    }
}
