package com.inigo.xudoku.model.progression

import com.inigo.xudoku.model.Difficulty

/**
 * Contiene todas las constantes configurables del sistema de progresión
 * para que sean fáciles de ajustar en el futuro sin modificar la lógica principal.
 */
object ProgressionConfig {
    /**
     * XP total acumulada necesaria para desbloquear cada nivel de dificultad.
     * Puedes modificar estos valores libremente para ajustar el balance del juego.
     */
    val DIFFICULTY_UNLOCK_XP: Map<Difficulty, Long> = mapOf(
        Difficulty.VERY_EASY to 0L,      // Desbloqueado desde el inicio
        Difficulty.EASY      to 500L,    // Requiere 500 XP (~2-3 partidas muy fáciles)
        Difficulty.MEDIUM    to 1500L,   // Requiere 1,500 XP
        Difficulty.HARD      to 3500L,   // Requiere 3,500 XP
        Difficulty.HARDEST   to 7500L    // Requiere 7,500 XP
    )

    // Fórmulas de XP Base
    const val XP_BASE_EXPONENT = 0.6
    
    // Curva de Niveles: XP_Total(N) = A * N^EXP_A + B * N^EXP_B
    const val LEVEL_CURVE_COEFF_A = 150.0
    const val LEVEL_CURVE_EXP_A = 1.5
    const val LEVEL_CURVE_COEFF_B = 6.0
    const val LEVEL_CURVE_EXP_B = 2.2
    
    const val MAX_BASE_LEVEL = 100
    const val LEVELS_PER_RANK = 4
    
    // Rachas
    const val DAILY_STREAK_BONUS_PER_DAY = 0.05
    const val DAILY_STREAK_MAX_BONUS = 0.25
    
    const val WIN_STREAK_BONUS_PER_WIN = 0.02
    const val WIN_STREAK_MAX_BONUS = 0.10
    
    // Prestigio
    const val PRESTIGE_XP_BONUS_PER_STAR = 0.05
}
