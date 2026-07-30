package com.inigo.xudoku.ui

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.SudokuBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios del GameViewModel.
 *
 * Patrón de setup:
 *   - [testDispatcher] = UnconfinedTestDispatcher → ejecuta coroutines *eagerly* (sin delay real).
 *   - Se asigna como Main para que viewModelScope lo use.
 *   - Se pasa como ioDispatcher al ViewModel para que withContext() también sea síncrono en tests.
 *   - [vm] se cancela en @After via vm.viewModelScope.cancel() para terminar el timerJob.
 *     Sin esto el proceso queda bloqueado: el timer (while(true)+delay) suspende en el
 *     scheduler virtual y el JVM no termina aunque el test pase.
 *
 * Sin Turbine → se accede directamente a StateFlow.value.
 * Con Turbine → se usa .test { } para verificar la *secuencia* de emisiones.
 *
 * Ver docs/casos-de-uso.md para el catálogo completo de CUs.
 * Ver .agent/skills/resources/testing-viewmodel.md para el patrón completo explicado.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    // vm a nivel de clase para poder cancelarlo en @After
    private lateinit var vm: GameViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        vm = GameViewModel(ioDispatcher = testDispatcher)
    }

    @After
    fun teardown() {
        // Cancela el viewModelScope (y con él el timerJob) para que el proceso no quede bloqueado.
        // viewModelScope no es hijo del scope de test → runTest no lo cancela automáticamente.
        Dispatchers.resetMain()
    }

    // ── CU-01: Iniciar partida ────────────────────────────────────────────────

    /**
     * SIN Turbine: verificamos el estado final del ViewModel accediendo a .value
     * de cada StateFlow después de que el trabajo asíncrono haya terminado.
     *
     * Ventaja: simple y directo cuando solo nos importa el estado final.
     * Desventaja: no podemos verificar estados intermedios (ej: isLoading=true antes de false).
     */
    @Test
    fun `CU-01 startGame reinicia todo el estado y genera un puzzle con las celdas dadas correctas`() = runTest(testDispatcher) {
        // Act
        // Con UnconfinedTestDispatcher las coroutines corren de forma eager (síncrona),
        // así que el estado ya está listo cuando startGame() retorna.
        // NO usar advanceUntilIdle() → el timer tiene un while(true)+delay que nunca terminaría.
        vm.startGame(Difficulty.VERY_EASY)

        // Assert — estado de control
        assertEquals(0, vm.mistakes.value)
        assertEquals(0, vm.elapsedSeconds.value)
        assertFalse(vm.isCompleted.value)
        assertFalse(vm.isLoading.value)
        assertFalse(vm.isNotesMode.value)
        assertNull(vm.selectedCell.value)
        assertEquals(Difficulty.VERY_EASY, vm.difficulty.value)
        assertTrue(vm.notes.value.isEmpty())

        // Assert — el grid tiene exactamente las celdas dadas que corresponden a la dificultad
        val givenCells = (0 until SudokuBoard.SIZE).sumOf { r ->
            (0 until SudokuBoard.SIZE).count { c -> vm.cells.value[r][c].isGiven }
        }
        assertEquals(Difficulty.VERY_EASY.visibleCells, givenCells)

        // Assert — ninguna celda dada está marcada como error
        val noGivenErrors = (0 until SudokuBoard.SIZE).all { r ->
            (0 until SudokuBoard.SIZE).all { c ->
                val cell = vm.cells.value[r][c]
                !(cell.isGiven && cell.isError)
            }
        }
        assertTrue(noGivenErrors)
        vm.viewModelScope.cancel()
    }

    // ── CU-02: Seleccionar celda ─────────────────────────────────────────────

    /**
     * CON Turbine: verificamos las *emisiones* del StateFlow en orden.
     *
     * Ventaja: podemos comprobar el valor inicial Y el valor tras la acción, en secuencia.
     * Esto es clave cuando la lógica produce varios estados intermedios (ej: null → Pair).
     * Desventaja: requiere cancelar el colector al final (.cancelAndIgnoreRemainingEvents()).
     */
    @Test
    fun `CU-02 selectCell emite el par fila-columna indicado en el StateFlow`() = runTest(testDispatcher) {
        vm.selectedCell.test {
            // StateFlow siempre emite su valor actual al colector nuevo → null inicial
            assertNull(awaitItem())

            // Seleccionamos la celda (3, 5)
            vm.selectCell(3, 5)

            // Turbine recibe la nueva emisión en orden
            assertEquals(Pair(3, 5), awaitItem())

            // Seleccionamos otra celda distinta
            vm.selectCell(0, 0)
            assertEquals(Pair(0, 0), awaitItem())

            cancelAndIgnoreRemainingEvents()
            vm.viewModelScope.cancel()
        }
    }

    @Test
    fun `CU-03 introducir numero en modo normal`() = runTest(testDispatcher) {
        vm.selectedCell.test {
            // StateFlow siempre emite su valor actual al colector nuevo → null inicial
            assertNull(awaitItem())

            // Seleccionamos la celda (3, 5)
            vm.selectCell(3, 5)
            vm.enterNumber(game.solution[row, col])

            assertEquals(vm.selectedCell.value, 5)
            cancelAndIgnoreRemainingEvents()
            vm.viewModelScope.cancel()
        }
    }
}
