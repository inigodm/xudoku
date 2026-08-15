package com.inigo.xudoku.model.progression

import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.min

class ProgressionManager {

    /**
     * Calcula la experiencia ganada en base a los puntos de la partida, los bonificadores
     * obtenidos y las rachas actuales.
     */
    fun calculateXP(
        score: Int,
        playTimeMs: Long,
        bonuses: List<BonusType>,
        dailyStreak: Int,
        winStreak: Int,
        prestigeStars: Int
    ): Int {
        val baseXP = floor(score.toDouble().pow(ProgressionConfig.XP_BASE_EXPONENT)).toInt()
        var multiplier = 1.0
        
        // Rachas
        val dailyBonus = dailyStreak * ProgressionConfig.DAILY_STREAK_BONUS_PER_DAY
        multiplier += min(dailyBonus, ProgressionConfig.DAILY_STREAK_MAX_BONUS)
        
        val winBonus = winStreak * ProgressionConfig.WIN_STREAK_BONUS_PER_WIN
        multiplier += min(winBonus, ProgressionConfig.WIN_STREAK_MAX_BONUS)
        
        // Bonificadores de la partida
        bonuses.forEach { bonus ->
            multiplier += bonus.percentageValue 
        }
        
        // Estrellas de prestigio
        multiplier += prestigeStars * ProgressionConfig.PRESTIGE_XP_BONUS_PER_STAR
        
        return (baseXP * multiplier).toInt()
    }
    
    /**
     * Devuelve la cantidad de experiencia TOTAL acumulada que se necesita para
     * alcanzar un nivel determinado (N).
     */
    fun getRequiredTotalXPForLevel(level: Int): Long {
        if (level <= 1) return 0L
        val lvlDouble = level.toDouble()
        val partA = ProgressionConfig.LEVEL_CURVE_COEFF_A * lvlDouble.pow(ProgressionConfig.LEVEL_CURVE_EXP_A)
        val partB = ProgressionConfig.LEVEL_CURVE_COEFF_B * lvlDouble.pow(ProgressionConfig.LEVEL_CURVE_EXP_B)
        return floor(partA + partB).toLong()
    }

    /**
     * Devuelve el objeto Rank correspondiente a un nivel dado.
     */
    fun getRankForLevel(level: Int): Rank {
        // Rangos cada ProgressionConfig.LEVELS_PER_RANK niveles
        // Rango índice: (N-1) / 4. Máximo índice = 24.
        val rankIndex = min((level - 1) / ProgressionConfig.LEVELS_PER_RANK, RanksList.ranks.size - 1) 
        return RanksList.ranks[rankIndex] 
    }
    
    /**
     * Devuelve el nivel que corresponde a una cantidad de XP total acumulada.
     */
    fun getLevelFromTotalXP(totalXP: Long): Int {
        var currentLevel = 1
        while (currentLevel < ProgressionConfig.MAX_BASE_LEVEL) {
            val xpForNext = getRequiredTotalXPForLevel(currentLevel + 1)
            if (totalXP >= xpForNext) {
                currentLevel++
            } else {
                break
            }
        }
        return currentLevel
    }
}
