package com.inigo.xudoku.ui

import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.scoring.ScoreConfig
import com.inigo.xudoku.model.scoring.ScoreManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

/**
 * Tests unitarios para el cálculo de puntuación del nuevo sistema avanzado.
 */
class ScoreTest {

    @Test
    fun `calcula puntos base de movimiento con multiplicadores de tiempo y progreso`() {
        val manager = ScoreManager()
        
        // Dificultad EASY (base 35)
        // Tiempo: 2s (<=5s -> x2.0)
        // Progreso: 10% (<=33% -> x1.0)
        // Esperado: 35 * 2.0 * 1.0 = 70
        val result = manager.calculateAndAddMoveScore(
            difficulty = Difficulty.EASY,
            currentTimeSeconds = 2,
            filledCellsRatio = 0.1f,
            isRowComplete = false,
            isColComplete = false,
            isBlockComplete = false,
            isLastCell = false
        )
        
        assertEquals(70, result.totalPoints)
        assertEquals(70, manager.currentScore)
    }

    @Test
    fun `agrega bonus por completar regiones y triple combo`() {
        val manager = ScoreManager()
        
        // Dificultad MEDIUM (base 50)
        // Tiempo: 100s desde inicio, pero lastMoveTime=0 -> 100s penaliza? No, si lastMoveTime es 0 y score es 0 no hay penalizacion.
        // Simulamos un movimiento previo para establecer lastMoveTime
        manager.calculateAndAddMoveScore(Difficulty.MEDIUM, 10, 0.5f, false, false, false, false)
        val scoreBefore = manager.currentScore
        
        // Tiempo: 12s despues (12s -> x1.2)
        // Progreso: 50% (x1.2)
        // Base: 50 * 1.2 * 1.2 = 72
        // Bonus: Fila(200) + Col(200) + Bloque(250) + TripleCombo(1000) = 1650
        // Total esperado = 72 + 1650 = 1722
        val result = manager.calculateAndAddMoveScore(
            difficulty = Difficulty.MEDIUM,
            currentTimeSeconds = 22,
            filledCellsRatio = 0.5f,
            isRowComplete = true,
            isColComplete = true,
            isBlockComplete = true,
            isLastCell = false
        )
        
        assertEquals(1722, result.totalPoints)
        assertTrue(result.isTripleCombo)
        assertEquals(scoreBefore + 1722, manager.currentScore)
    }

    @Test
    fun `calcula la puntuacion final con penalizaciones por ayudas y multiplicador de dificultad`() {
        val manager = ScoreManager()
        
        // Dificultad HARD
        // currentScore lo seteamos haciendo un movimiento perfecto final (lastCell=true)
        manager.calculateAndAddMoveScore(Difficulty.HARD, 10, 1.0f, false, false, false, true)
        val score = manager.currentScore
        
        // Usamos 2 pistas
        manager.recordHint()
        manager.recordHint()
        
        // Finalizamos a los 2100 segundos (targetTime HARD = 2100s -> timeFactor = 1.0)
        val finalScore = manager.calculateFinalScore(Difficulty.HARD, 2100)
        
        // Calculo manual esperado:
        // finalScore = currentScore * dificultad(3.2) * time(1.0) * penaltyHints(0.75^2) * noMistakes(1.1)
        val expected = (score.toFloat() * 3.2f * 1.0f * (0.75f * 0.75f) * 1.10f).roundToInt()
        
        assertEquals(expected, finalScore)
    }
}
