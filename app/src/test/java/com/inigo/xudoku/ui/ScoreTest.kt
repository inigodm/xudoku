package com.inigo.xudoku.ui

import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.ui.screen.computeScore
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests unitarios para el cálculo de puntuación final (CU-16).
 */
class ScoreTest {

    @Test
    fun `CU-16 computeScore calcula el puntaje base segun la dificultad sin penalizaciones`() {
        // Sin tiempo transcurrido ni errores
        assertEquals(1000, computeScore(0, 0, Difficulty.VERY_EASY))
        assertEquals(2000, computeScore(0, 0, Difficulty.EASY))
        assertEquals(4000, computeScore(0, 0, Difficulty.MEDIUM))
        assertEquals(7000, computeScore(0, 0, Difficulty.HARD))
        assertEquals(12000, computeScore(0, 0, Difficulty.HARDEST))
    }

    @Test
    fun `CU-16 computeScore aplica penalizacion por tiempo y errores`() {
        // VERY_EASY base = 1000. 50 segs -> penalty = 50/10 = 5. Errores = 1 -> penalty = 200.
        // Esperado: 1000 - 5 - 200 = 795
        val score = computeScore(elapsedSeconds = 50, mistakes = 1, difficulty = Difficulty.VERY_EASY)
        assertEquals(795, score)
    }

    @Test
    fun `CU-16 computeScore limita la penalizacion por tiempo a la mitad del puntaje base`() {
        // EASY base = 2000. Mitad max tiempo = 1000.
        // Tiempo masivo = 10000 segs (penalización normal 1000, topada a 1000). Errores = 0.
        // Esperado: 2000 - 1000 = 1000
        val score = computeScore(elapsedSeconds = 10000, mistakes = 0, difficulty = Difficulty.EASY)
        assertEquals(1000, score)
    }

    @Test
    fun `CU-16 computeScore respeta el puntaje minimo de 100 puntos`() {
        // VERY_EASY base = 1000. 10 errores = -2000 penalty.
        // Esperado: tope mínimo 100
        val score = computeScore(elapsedSeconds = 1000, mistakes = 10, difficulty = Difficulty.VERY_EASY)
        assertEquals(100, score)
    }
}
