package com.inigo.xudoku.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.ui.screen.DifficultyScreen
import com.inigo.xudoku.ui.screen.GameOverScreen
import com.inigo.xudoku.ui.screen.GameScreen
import com.inigo.xudoku.ui.screen.ProfileScreen
import com.inigo.xudoku.ui.screen.SplashScreen
import com.inigo.xudoku.ui.screen.StatsScreen
import com.inigo.xudoku.ui.screen.VictoryScreen

/** Destinos de navegación de la app. */
internal object Dest {
    const val SPLASH     = "splash"
    const val DIFFICULTY = "difficulty"
    const val GAME       = "game/{difficultyName}"
    const val VICTORY    = "victory/{seconds}/{mistakes}/{difficultyName}/{score}/{isNewHighScore}/{hasLeveledUp}"
    const val GAME_OVER  = "game_over/{seconds}/{mistakes}"
    const val STATS      = "stats"
    const val PROFILE    = "profile"

    fun game(difficulty: Difficulty)                             = "game/${difficulty.name}"
    fun victory(seconds: Int, mistakes: Int, difficulty: Difficulty, score: Int, isNewHighScore: Boolean, hasLeveledUp: Boolean) =
        "victory/$seconds/$mistakes/${difficulty.name}/$score/$isNewHighScore/$hasLeveledUp"
    fun gameOver(seconds: Int, mistakes: Int) = "game_over/$seconds/$mistakes"
}

/**
 * Grafo de navegación principal de xudoku.
 * Se instancia una sola vez en [MainActivity].
 */
@Composable
fun XudokuNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = Dest.SPLASH
    ) {

        // ── Splash ──────────────────────────────────────────────────────────
        composable(Dest.SPLASH) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Dest.DIFFICULTY) {
                        popUpTo(Dest.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Selección de dificultad ──────────────────────────────────────────
        composable(Dest.DIFFICULTY) {
            DifficultyScreen(
                onDifficultySelected = { difficulty ->
                    navController.navigate(Dest.game(difficulty))
                },
                onNavigateToStats    = { navController.navigate(Dest.STATS) },
                onNavigateToProfile  = { navController.navigate(Dest.PROFILE) }
            )
        }

        // ── Tablero de juego ─────────────────────────────────────────────────
        composable(
            route     = Dest.GAME,
            arguments = listOf(navArgument("difficultyName") { type = NavType.StringType })
        ) { entry ->
            val difficultyName = entry.arguments?.getString("difficultyName") ?: Difficulty.VERY_EASY.name
            val difficulty     = runCatching { Difficulty.valueOf(difficultyName) }.getOrDefault(Difficulty.VERY_EASY)
            val gameViewModel: GameViewModel = org.koin.androidx.compose.koinViewModel()

            GameScreen(
                difficulty       = difficulty,
                viewModel        = gameViewModel,
                onGameCompleted  = { seconds, mistakes, diff, score, isNew, hasLeveledUp ->
                    navController.navigate(Dest.victory(seconds, mistakes, diff, score, isNew, hasLeveledUp)) {
                        popUpTo(Dest.DIFFICULTY) // limpiar back stack del juego
                    }
                },
                onGameOver       = { seconds, mistakes ->
                    navController.navigate(Dest.gameOver(seconds, mistakes)) {
                        popUpTo(Dest.DIFFICULTY)
                    }
                },
                onNavigateBack   = {
                    navController.popBackStack(Dest.DIFFICULTY, inclusive = false)
                }
            )
        }

        // ── Victoria ─────────────────────────────────────────────────────────
        composable(
            route     = Dest.VICTORY,
            arguments = listOf(
                navArgument("seconds")        { type = NavType.IntType    },
                navArgument("mistakes")       { type = NavType.IntType    },
                navArgument("difficultyName") { type = NavType.StringType },
                navArgument("score")          { type = NavType.IntType    },
                navArgument("isNewHighScore") { type = NavType.BoolType   },
                navArgument("hasLeveledUp")   { type = NavType.BoolType   }
            )
        ) { entry ->
            val args       = entry.arguments!!
            val difficulty = runCatching {
                Difficulty.valueOf(args.getString("difficultyName") ?: "")
            }.getOrDefault(Difficulty.VERY_EASY)

            VictoryScreen(
                elapsedSeconds = args.getInt("seconds"),
                mistakes       = args.getInt("mistakes"),
                difficulty     = difficulty,
                score          = args.getInt("score"),
                isNewHighScore = args.getBoolean("isNewHighScore"),
                hasLeveledUp   = args.getBoolean("hasLeveledUp"),
                onNextLevel    = {
                    // Nueva partida con la misma dificultad
                    navController.navigate(Dest.game(difficulty)) {
                        popUpTo(Dest.DIFFICULTY)
                    }
                },
                onMainMenu     = {
                    navController.navigate(Dest.DIFFICULTY) {
                        popUpTo(Dest.DIFFICULTY) { inclusive = true }
                    }
                }
            )
        }

        // ── Fin de juego ─────────────────────────────────────────────────────
        composable(
            route     = Dest.GAME_OVER,
            arguments = listOf(
                navArgument("seconds")  { type = NavType.IntType },
                navArgument("mistakes") { type = NavType.IntType }
            )
        ) { entry ->
            val args = entry.arguments!!
            GameOverScreen(
                elapsedSeconds = args.getInt("seconds"),
                mistakes       = args.getInt("mistakes"),
                onRetry        = {
                    navController.navigate(Dest.DIFFICULTY) {
                        popUpTo(Dest.DIFFICULTY) { inclusive = true }
                    }
                },
                onMainMenu     = {
                    navController.navigate(Dest.DIFFICULTY) {
                        popUpTo(Dest.DIFFICULTY) { inclusive = true }
                    }
                }
            )
        }

        // ── Estadísticas ─────────────────────────────────────────────────────
        composable(Dest.STATS) {
            val statsViewModel: com.inigo.xudoku.ui.StatsViewModel = org.koin.androidx.compose.koinViewModel()
            StatsScreen(
                viewModel = statsViewModel,
                onNavigateToPlay    = {
                    navController.navigate(Dest.DIFFICULTY) {
                        popUpTo(Dest.DIFFICULTY) { inclusive = true }
                    }
                },
                onNavigateToProfile = { navController.navigate(Dest.PROFILE) }
            )
        }

        // ── Perfil ───────────────────────────────────────────────────────────
        composable(Dest.PROFILE) {
            ProfileScreen(
                onNavigateToPlay  = {
                    navController.navigate(Dest.DIFFICULTY) {
                        popUpTo(Dest.DIFFICULTY) { inclusive = true }
                    }
                },
                onNavigateToStats = { navController.navigate(Dest.STATS) }
            )
        }
    }
}
