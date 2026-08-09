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
import kotlinx.coroutines.flow.MutableStateFlow
import com.inigo.xudoku.model.progression.ProgressionManager
import com.inigo.xudoku.data.repository.ProgressionRepository
import com.inigo.xudoku.data.repository.ProgressionState
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

    // ── CU-03: Introducir número (modo normal) ────────────────────────────────

    @Test
    fun `CU-03 enterNumber en modo normal actualiza el valor de la celda seleccionada`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)

        // Buscar una celda vacia (no dada)
        var targetRow = -1
        var targetCol = -1
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!vm.cells.value[r][c].isGiven) {
                    targetRow = r
                    targetCol = c
                    break
                }
            }
            if (targetRow != -1) break
        }

        assertTrue("Debe existir al menos una celda no dada", targetRow != -1)

        // Seleccionar la celda e introducir un numero (5)
        vm.selectCell(targetRow, targetCol)
        vm.enterNumber(5)

        // Assert — la celda ahora contiene el número 5 y no es dada
        val cell = vm.cells.value[targetRow][targetCol]
        assertEquals(5, cell.value)
        assertFalse(cell.isGiven)

        vm.viewModelScope.cancel()
    }

    @Test
    fun `CU-03 enterNumber no modifica celdas dadas`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)

        // Buscar una celda dada
        var givenRow = -1
        var givenCol = -1
        var initialValue = 0
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (vm.cells.value[r][c].isGiven) {
                    givenRow = r
                    givenCol = c
                    initialValue = vm.cells.value[r][c].value
                    break
                }
            }
            if (givenRow != -1) break
        }

        assertTrue("Debe existir al menos una celda dada", givenRow != -1)

        // Intentar sobreescribir la celda dada con otro número
        val newValue = if (initialValue == 9) 1 else initialValue + 1
        vm.selectCell(givenRow, givenCol)
        vm.enterNumber(newValue)

        // Assert — el valor de la celda dada no ha cambiado
        assertEquals(initialValue, vm.cells.value[givenRow][givenCol].value)
        assertTrue(vm.cells.value[givenRow][givenCol].isGiven)

        vm.viewModelScope.cancel()
    }

    // ── CU-04: Introducir nota en lápiz (modo notas) ─────────────────────────

    @Test
    fun `CU-04 enterNumber en modo notas anade y quita notas alternadamente`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)

        // Buscar celda vacía no dada
        var targetRow = -1
        var targetCol = -1
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!vm.cells.value[r][c].isGiven) {
                    targetRow = r
                    targetCol = c
                    break
                }
            }
            if (targetRow != -1) break
        }

        vm.selectCell(targetRow, targetCol)
        vm.toggleNotesMode()

        val key = Pair(targetRow, targetCol)

        // Añadir nota 3
        vm.enterNumber(3)
        assertEquals(setOf(3), vm.notes.value[key])
        assertEquals(0, vm.cells.value[targetRow][targetCol].value)

        // Añadir nota 7
        vm.enterNumber(7)
        assertEquals(setOf(3, 7), vm.notes.value[key])

        // Quitar nota 3 (toggle)
        vm.enterNumber(3)
        assertEquals(setOf(7), vm.notes.value[key])

        vm.viewModelScope.cancel()
    }

    // ── CU-05: Activar y desactivar modo notas ────────────────────────────────

    @Test
    fun `CU-05 toggleNotesMode alterna el estado de isNotesMode`() = runTest(testDispatcher) {
        vm.isNotesMode.test {
            assertFalse(awaitItem())

            vm.toggleNotesMode()
            assertTrue(awaitItem())

            vm.toggleNotesMode()
            assertFalse(awaitItem())

            cancelAndIgnoreRemainingEvents()
            vm.viewModelScope.cancel()
        }
    }

    // ── CU-06: Borrar celda seleccionada ─────────────────────────────────────

    @Test
    fun `CU-06 clearSelectedCell limpia valor y notas de celda no dada`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)

        var targetRow = -1
        var targetCol = -1
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!vm.cells.value[r][c].isGiven) {
                    targetRow = r
                    targetCol = c
                    break
                }
            }
            if (targetRow != -1) break
        }

        vm.selectCell(targetRow, targetCol)
        vm.enterNumber(5)
        assertEquals(5, vm.cells.value[targetRow][targetCol].value)

        vm.clearSelectedCell()
        assertEquals(0, vm.cells.value[targetRow][targetCol].value)
        assertFalse(vm.cells.value[targetRow][targetCol].isError)

        vm.viewModelScope.cancel()
    }

    @Test
    fun `CU-06 clearSelectedCell ignora celdas dadas`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)

        var givenRow = -1
        var givenCol = -1
        var initialValue = 0
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (vm.cells.value[r][c].isGiven) {
                    givenRow = r
                    givenCol = c
                    initialValue = vm.cells.value[r][c].value
                    break
                }
            }
            if (givenRow != -1) break
        }

        vm.selectCell(givenRow, givenCol)
        vm.clearSelectedCell()

        assertEquals(initialValue, vm.cells.value[givenRow][givenCol].value)
        assertTrue(vm.cells.value[givenRow][givenCol].isGiven)

        vm.viewModelScope.cancel()
    }

    // ── CU-07: Deshacer último movimiento ────────────────────────────────────

    @Test
    fun `CU-07 undoLastMove restaura valor anterior y gestiona contador de errores`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)

        var targetRow = -1
        var targetCol = -1
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!vm.cells.value[r][c].isGiven) {
                    targetRow = r
                    targetCol = c
                    break
                }
            }
            if (targetRow != -1) break
        }

        vm.selectCell(targetRow, targetCol)
        vm.enterNumber(5)

        val mistakesAfterEnter = vm.mistakes.value

        vm.undoLastMove()

        assertEquals(0, vm.cells.value[targetRow][targetCol].value)
        assertFalse(vm.cells.value[targetRow][targetCol].isError)
        if (mistakesAfterEnter > 0) {
            assertEquals(0, vm.mistakes.value)
        }

        vm.viewModelScope.cancel()
    }

    // ── CU-08: Solicitar pista ────────────────────────────────────────────────

    @Test
    fun `CU-08 requestHint revela la solucion correcta en la celda seleccionada`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)

        var targetRow = -1
        var targetCol = -1
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!vm.cells.value[r][c].isGiven) {
                    targetRow = r
                    targetCol = c
                    break
                }
            }
            if (targetRow != -1) break
        }

        vm.selectCell(targetRow, targetCol)
        vm.requestHint()

        val cell = vm.cells.value[targetRow][targetCol]
        assertTrue(cell.value != 0)
        assertFalse(cell.isError)
        assertFalse(cell.isGiven)

        vm.viewModelScope.cancel()
    }

    @Test
    fun `CU-08 requestHint busca la primera celda vacia si no hay celda seleccionada`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)
        assertNull(vm.selectedCell.value)

        vm.requestHint()

        val selected = vm.selectedCell.value
        assertTrue(selected != null)
        val (r, c) = selected!!
        val cell = vm.cells.value[r][c]
        assertTrue(cell.value != 0)
        assertFalse(cell.isError)

        vm.viewModelScope.cancel()
    }

    // ── CU-09: Completar puzzle (detección automática) ────────────────────────

    @Test
    fun `CU-09 al rellenar todas las celdas correctamente se activa isCompleted`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)

        // Rellenar todas las celdas vacías pidiendo pistas (que colocan la solución correcta)
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!vm.cells.value[r][c].isGiven && vm.cells.value[r][c].value == SudokuBoard.EMPTY) {
                    vm.selectCell(r, c)
                    vm.requestHint()
                }
            }
        }

        assertTrue(vm.isCompleted.value)

        vm.viewModelScope.cancel()
    }

    // ── CU-17: Agotar errores (pantalla de fin de juego) ──────────────────────

    @Test
    fun `CU-17 al alcanzar 3 errores se activa isGameOver`() = runTest(testDispatcher) {
        vm.startGame(Difficulty.VERY_EASY)
        assertFalse(vm.isGameOver.value)

        // Encontrar celdas vacías e introducir números incorrectos para acumular 3 errores
        var mistakesCount = 0
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!vm.cells.value[r][c].isGiven && vm.cells.value[r][c].value == SudokuBoard.EMPTY) {
                    vm.selectCell(r, c)
                    vm.enterNumber(9)
                    if (vm.cells.value[r][c].isError) {
                        mistakesCount++
                        if (mistakesCount == 3) break
                    } else {
                        // Si 9 era la solución correcta, intentamos con 1
                        vm.enterNumber(1)
                        if (vm.cells.value[r][c].isError) {
                            mistakesCount++
                            if (mistakesCount == 3) break
                        }
                    }
                }
            }
            if (mistakesCount == 3) break
        }

        assertEquals(3, vm.mistakes.value)
        assertTrue(vm.isGameOver.value)

        vm.viewModelScope.cancel()
    }

    // ── CU-18 / CU-19: Ganar Experiencia y Subir de Nivel ──────────────────────
    
    @Test
    fun `CU-18 y CU-19 al completar el puzzle evalua si hasLeveledUp cambia a true`() = runTest(testDispatcher) {
        val manager = ProgressionManager()
        val repo = object : ProgressionRepository {
            override val progressionState = MutableStateFlow(ProgressionState(totalXP = 0))
            override suspend fun getProgressionState() = progressionState.value
            override suspend fun updateXP(xpToAdd: Int) {
                progressionState.value = progressionState.value.copy(totalXP = progressionState.value.totalXP + xpToAdd)
            }
            override suspend fun updateStreaks(newDailyStreak: Int, newWinStreak: Int, playDate: Long) {}
            override suspend fun resetWinStreak() {}
            override suspend fun ascendPrestige() {}
        }
        
        val vmWithProgression = GameViewModel(
            ioDispatcher = testDispatcher, 
            progressionRepo = repo, 
            progressionManager = manager
        )
        
        vmWithProgression.startGame(Difficulty.VERY_EASY)
        assertFalse(vmWithProgression.hasLeveledUp.value)

        // Usamos reflexión para simular que han pasado 65 segundos, burlando el anti-farmeo
        val elapsedField = GameViewModel::class.java.getDeclaredField("_elapsedSeconds")
        elapsedField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val elapsedStateFlow = elapsedField.get(vmWithProgression) as MutableStateFlow<Int>
        elapsedStateFlow.value = 65

        // Rellenar todas las celdas vacías con la solución sin usar pistas para no arruinar el score
        val gameField = GameViewModel::class.java.getDeclaredField("game")
        gameField.isAccessible = true
        val game = gameField.get(vmWithProgression) as com.inigo.xudoku.model.SudokuGame
        
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!vmWithProgression.cells.value[r][c].isGiven && vmWithProgression.cells.value[r][c].value == SudokuBoard.EMPTY) {
                    vmWithProgression.selectCell(r, c)
                    vmWithProgression.enterNumber(game.solution.get(r, c))
                }
            }
        }

        assertTrue(vmWithProgression.isCompleted.value)
        
        // Al empezar con 0 XP, la partida en VERY_EASY otorga suficiente XP (aprox 10k) 
        // para subir desde el nivel 1 al menos al nivel 2.
        assertTrue("hasLeveledUp debería ser true ya que se empieza en nivel 1 y la partida da XP suficiente", vmWithProgression.hasLeveledUp.value)

        vmWithProgression.viewModelScope.cancel()
    }
}
