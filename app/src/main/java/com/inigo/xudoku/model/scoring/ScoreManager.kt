package com.inigo.xudoku.model.scoring

import com.inigo.xudoku.model.Difficulty
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Gestor de puntuación durante la partida. Mantiene el estado de la puntuación acumulada,
 * errores, ayudas y tiempo del último movimiento para calcular multiplicadores de rapidez.
 */
class ScoreManager {
    var currentScore: Int = 0
        private set

    var hintsUsed: Int = 0
        private set

    var mistakesMade: Int = 0
        private set

    private var lastMoveTime: Int = 0

    fun reset() {
        currentScore = 0
        lastMoveTime = 0
        hintsUsed = 0
        mistakesMade = 0
    }

    fun recordMistake() {
        mistakesMade++
    }

    fun recordHint() {
        hintsUsed++
    }

    data class MoveResult(
        val totalPoints: Int,
        val isTripleCombo: Boolean,
        val isFinalCell: Boolean
    )

    /**
     * Calcula los puntos obtenidos en un movimiento correcto y los suma a la puntuación de la partida.
     */
    fun calculateAndAddMoveScore(
        difficulty: Difficulty,
        currentTimeSeconds: Int,
        filledCellsRatio: Float,
        isRowComplete: Boolean,
        isColComplete: Boolean,
        isBlockComplete: Boolean,
        isLastCell: Boolean
    ): MoveResult {
        val basePoints = ScoreConfig.getBasePoints(difficulty)
        
        val elapsedSinceLastMove = if (currentScore == 0 && lastMoveTime == 0) {
            0 // Primer movimiento no tiene penalización de tiempo
        } else {
            currentTimeSeconds - lastMoveTime
        }
        
        val speedMultiplier = ScoreConfig.getSpeedMultiplier(elapsedSinceLastMove)
        val progressMultiplier = ScoreConfig.getProgressMultiplier(filledCellsRatio)
        
        var movePoints = (basePoints * speedMultiplier * progressMultiplier).roundToInt()
        
        if (isRowComplete) movePoints += ScoreConfig.BONUS_ROW
        if (isColComplete) movePoints += ScoreConfig.BONUS_COL
        if (isBlockComplete) movePoints += ScoreConfig.BONUS_BLOCK
        
        val isTripleCombo = isRowComplete && isColComplete && isBlockComplete
        if (isTripleCombo) movePoints += ScoreConfig.BONUS_TRIPLE_COMBO
        
        if (isLastCell) movePoints += ScoreConfig.BONUS_FINAL_CELL
        
        currentScore += movePoints
        lastMoveTime = currentTimeSeconds
        
        return MoveResult(
            totalPoints = movePoints,
            isTripleCombo = isTripleCombo,
            isFinalCell = isLastCell
        )
    }

    /**
     * Calcula la puntuación final aplicando multiplicadores y penalizaciones sobre la puntuación base acumulada.
     */
    fun calculateFinalScore(difficulty: Difficulty, totalTimeSeconds: Int): Int {
        var finalScore = currentScore.toFloat()
        
        // Multiplicador de dificultad
        finalScore *= ScoreConfig.getDifficultyMultiplier(difficulty)
        
        // Factor tiempo (maximo 2.0, minimo 0.5)
        val targetTime = ScoreConfig.getTargetTimeSeconds(difficulty)
        // Evitar division por 0
        val safeTime = if (totalTimeSeconds <= 0) 1 else totalTimeSeconds
        val timeFactor = (targetTime.toFloat() / safeTime.toFloat())
            .coerceIn(ScoreConfig.MIN_TIME_FACTOR, ScoreConfig.MAX_TIME_FACTOR)
        finalScore *= timeFactor
        
        // Penalización por ayudas (acumulativas: score * (1 - 0.25)^hints)
        val penaltyFactor = (1.0f - ScoreConfig.PENALTY_HINT_PERCENT).coerceAtLeast(0.01f).pow(hintsUsed)
        finalScore *= penaltyFactor
        
        // Bonus finales
        if (hintsUsed == 0) {
            finalScore *= ScoreConfig.BONUS_NO_HINTS
        }
        if (mistakesMade == 0) {
            finalScore *= ScoreConfig.BONUS_NO_MISTAKES
        }
        
        return finalScore.roundToInt().coerceAtLeast(0)
    }
}
