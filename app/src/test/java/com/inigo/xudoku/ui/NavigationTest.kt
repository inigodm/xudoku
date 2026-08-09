package com.inigo.xudoku.ui

import com.inigo.xudoku.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests unitarios para las rutas y flujos de navegación (CU-10 a CU-15).
 */
class NavigationTest {

    @Test
    fun `CU-10 Splash a Dificultad define la ruta de destino`() {
        assertEquals("splash", Dest.SPLASH)
        assertEquals("difficulty", Dest.DIFFICULTY)
    }

    @Test
    fun `CU-11 Seleccionar dificultad genera la ruta correcta para la pantalla de juego`() {
        val routeVeryEasy = Dest.game(Difficulty.VERY_EASY)
        val routeHard = Dest.game(Difficulty.HARD)

        assertEquals("game/VERY_EASY", routeVeryEasy)
        assertEquals("game/HARD", routeHard)
    }

    @Test
    fun `CU-12 Completar puzzle genera la ruta de victoria con todos los parametros`() {
        val route = Dest.victory(
            seconds = 120,
            mistakes = 1,
            difficulty = Difficulty.MEDIUM,
            score = 3800,
            isNewHighScore = true,
            hasLeveledUp = true
        )

        assertEquals("victory/120/1/MEDIUM/3800/true/true", route)
    }

    @Test
    fun `CU-13 Jugar de nuevo navega a una nueva partida con la misma dificultad`() {
        val difficulty = Difficulty.HARD
        val newGameRoute = Dest.game(difficulty)

        assertEquals("game/HARD", newGameRoute)
    }

    @Test
    fun `CU-14 Volver al menu principal desde Victoria redirige a Dificultad`() {
        assertEquals("difficulty", Dest.DIFFICULTY)
    }

    @Test
    fun `CU-15 Volver atras desde partida mantiene el destino Dificultad`() {
        assertEquals("difficulty", Dest.DIFFICULTY)
    }

    @Test
    fun `CU-17 Agotar errores genera la ruta de fin de juego con segundos y errores`() {
        val route = Dest.gameOver(seconds = 765, mistakes = 3)
        assertEquals("game_over/765/3", route)
    }
}
