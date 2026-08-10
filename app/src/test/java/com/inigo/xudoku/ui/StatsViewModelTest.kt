package com.inigo.xudoku.ui

import com.inigo.xudoku.data.repository.GameHistoryRepository
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.history.SudokuGameResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var fakeRepository: FakeGameHistoryRepository
    private lateinit var viewModel: StatsViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeGameHistoryRepository()
        viewModel = StatsViewModel(fakeRepository)
    }
    
    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createDummyResult(
        difficulty: Difficulty = Difficulty.EASY,
        isWin: Boolean = true,
        idSudoku: String = "1"
    ): SudokuGameResult {
        return SudokuGameResult(
            id = UUID.randomUUID().toString(),
            fechaHoraInicio = Date(),
            fechaHoraFin = Date(),
            tiempoEmpleado = 120,
            tiempoPausado = 0,
            dificultad = difficulty,
            nivel = 1,
            identificadorSudoku = idSudoku,
            seed = null,
            tamanoTablero = 9,
            puntuacionPartida = 100,
            puntuacionFinal = 100,
            multiplicadorDificultad = 1f,
            multiplicadorTiempo = 1f,
            ayudasMostrarNumero = 0,
            ayudasResolverCasilla = 0,
            ayudasComprobarErrores = 0,
            totalAyudas = 0,
            erroresCometidos = 0,
            partidaPerfecta = true,
            movimientosTotales = 40,
            numerosColocados = 40,
            porcentajeCompletadoManual = 100f,
            porcentajeCompletadoConAyudas = 0f,
            completado = isWin,
            abandono = !isWin,
            victoria = isWin,
            versionJuego = 1,
            versionAlgoritmoPuntuacion = 1,
            metadata = "{}"
        )
    }

    @Test
    fun `loadStats populates totalGamesPlayed and translates recent flow labels`() = runTest(testDispatcher) {
        // Arrange
        val games = listOf(
            createDummyResult(Difficulty.VERY_EASY, true, "10"),
            createDummyResult(Difficulty.EASY, false, "11"),
            createDummyResult(Difficulty.MEDIUM, true, "12"),
            createDummyResult(Difficulty.HARD, false, "13"),
            createDummyResult(Difficulty.HARDEST, true, "14")
        )
        fakeRepository.results = games
        
        // Act
        viewModel.loadStats("Global")
        
        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        
        // totalGamesPlayed correctly mapped
        assertEquals(5, state.totalGamesPlayed)
        assertEquals(3, state.totalGamesWon)
        
        // Labels localized mapping correctly
        val recentGames = state.recentGames
        assertEquals(5, recentGames.size)
        // Check translation
        val mappedLabels = recentGames.map { it.label }
        assert(mappedLabels.contains("Fácil #10"))
        assert(mappedLabels.contains("Medio #11"))
        assert(mappedLabels.contains("Difícil #12"))
        assert(mappedLabels.contains("Extremo #13"))
        assert(mappedLabels.contains("Imposible #14"))
    }
}

class FakeGameHistoryRepository : GameHistoryRepository {
    var results = listOf<SudokuGameResult>()
    
    override suspend fun saveGameResult(result: SudokuGameResult) {}
    override suspend fun getAllResults(): List<SudokuGameResult> = results
    override fun getAllResultsFlow(): Flow<List<SudokuGameResult>> = flowOf(results)
    override suspend fun getResultById(id: String): SudokuGameResult? = results.find { it.id == id }
    override suspend fun deleteResult(id: String) {}
    override suspend fun clearHistory() {}
    override suspend fun getTopScores(limit: Int): List<SudokuGameResult> = results.sortedByDescending { it.puntuacionFinal }.take(limit)
    override suspend fun getBestTimeForDifficulty(difficulty: Difficulty): SudokuGameResult? = results.filter { it.dificultad == difficulty && it.victoria }.minByOrNull { it.tiempoEmpleado }
    override suspend fun getResultsByDifficulty(difficulty: Difficulty): List<SudokuGameResult> = results.filter { it.dificultad == difficulty }
    override suspend fun getResultsByDateRange(startDate: Long, endDate: Long): List<SudokuGameResult> = results
    override suspend fun getResultsByLevel(level: Int): List<SudokuGameResult> = results.filter { it.nivel == level }
}
