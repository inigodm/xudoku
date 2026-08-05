package com.inigo.xudoku.model.scoring

import com.inigo.xudoku.model.Difficulty

/**
 * Contiene todas las constantes configurables para el sistema de puntuación.
 */
object ScoreConfig {
    /** Puntos base obtenidos por un acierto según la dificultad. */
    fun getBasePoints(difficulty: Difficulty): Int = when (difficulty) {
        Difficulty.VERY_EASY -> 20
        Difficulty.EASY      -> 35
        Difficulty.MEDIUM    -> 50
        Difficulty.HARD      -> 70
        Difficulty.HARDEST   -> 100
    }

    /** Multiplicador final de puntuación según la dificultad. */
    fun getDifficultyMultiplier(difficulty: Difficulty): Float = when (difficulty) {
        Difficulty.VERY_EASY -> 1.0f
        Difficulty.EASY      -> 1.5f
        Difficulty.MEDIUM    -> 2.2f
        Difficulty.HARD      -> 3.2f
        Difficulty.HARDEST   -> 4.5f
    }

    /** Tiempo objetivo en segundos para completar la partida, según dificultad. */
    fun getTargetTimeSeconds(difficulty: Difficulty): Int = when (difficulty) {
        Difficulty.VERY_EASY -> 300  // 5 min
        Difficulty.EASY      -> 600  // 10 min
        Difficulty.MEDIUM    -> 1200 // 20 min
        Difficulty.HARD      -> 2100 // 35 min
        Difficulty.HARDEST   -> 3600 // 60 min
    }

    /** Multiplicador por rapidez: segundos transcurridos desde el último acierto. */
    fun getSpeedMultiplier(elapsedSecondsSinceLastMove: Int): Float = when {
        elapsedSecondsSinceLastMove <= 5  -> 2.0f
        elapsedSecondsSinceLastMove <= 10 -> 1.5f
        elapsedSecondsSinceLastMove <= 20 -> 1.2f
        else                              -> 1.0f
    }

    /** Multiplicador por progreso: porcentaje del tablero resuelto (0.0f a 1.0f). */
    fun getProgressMultiplier(completionRatio: Float): Float = when {
        completionRatio <= 0.33f -> 1.0f
        completionRatio <= 0.66f -> 1.2f
        completionRatio <= 0.90f -> 1.5f
        else                     -> 2.0f
    }

    const val BONUS_ROW = 200
    const val BONUS_COL = 200
    const val BONUS_BLOCK = 250
    const val BONUS_FINAL_CELL = 1000
    const val BONUS_TRIPLE_COMBO = 1000

    const val PENALTY_HINT_PERCENT = 0.25f // 25% por resolver una casilla automáticamente
    // Si hubiese otras ayudas:
    // const val PENALTY_SHOW_NUMBER_PERCENT = 0.15f
    // const val PENALTY_CHECK_ERRORS_PERCENT = 0.10f

    const val BONUS_NO_HINTS = 1.10f
    const val BONUS_NO_MISTAKES = 1.10f

    const val MIN_TIME_FACTOR = 0.5f
    const val MAX_TIME_FACTOR = 2.0f
}
