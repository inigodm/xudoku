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
import org.junit.Assert.assertTrue
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
        viewModel = StatsViewModel(fakeRepository, testDispatcher)
    }
    
    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createDummyResult(
        difficulty: Difficulty = Difficulty.EASY,
        isWin: Boolean = true,
        idSudoku: String = "1",
        xpEarned: Int? = null,
        puntuacionFinal: Int = 100
    ): SudokuGameResult {
        val meta = if (xpEarned != null) "{\"xpEarned\": $xpEarned}" else "{}"
        return SudokuGameResult(
            id = UUID.randomUUID().toString(),
            fechaHoraInicio = Date(),
            fechaHoraFin = Date(1000L * (idSudoku.toLongOrNull() ?: 1L)),
            tiempoEmpleado = 120,
            tiempoPausado = 0,
            dificultad = difficulty,
            nivel = 1,
            identificadorSudoku = idSudoku,
            seed = null,
            tamanoTablero = 9,
            puntuacionPartida = puntuacionFinal,
            puntuacionFinal = puntuacionFinal,
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
            metadata = meta
        )
    }

    @Test
    fun `loadStats populates totalGamesPlayed and translates recent flow labels and reads metadata xp`() = runTest(testDispatcher) {
        // Arrange
        val games = listOf(
            createDummyResult(Difficulty.VERY_EASY, true, "10", 1250),
            createDummyResult(Difficulty.EASY, false, "11", null),
            createDummyResult(Difficulty.MEDIUM, true, "12", 2400),
            createDummyResult(Difficulty.HARD, false, "13", null),
            createDummyResult(Difficulty.HARDEST, true, "14", 5000)
        )
        fakeRepository.results = games
        
        // Act
        viewModel.loadStats(null) // Global
        
        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        
        assertEquals(5, state.totalGamesPlayed)
        assertEquals(3, state.totalGamesWon)
        
        val recentGames = state.recentGames
        assertEquals(5, recentGames.size)
        
        val veryEasyGame = recentGames.find { it.difficultyResId == com.inigo.xudoku.R.string.diff_very_easy }
        assertEquals(1250, veryEasyGame?.xp)
        assertEquals("10", veryEasyGame?.sudokuId)
        
        val hardGame = recentGames.find { it.difficultyResId == com.inigo.xudoku.R.string.diff_medium }
        assertEquals(2400, hardGame?.xp)
        assertEquals("12", hardGame?.sudokuId)
        
        val points = state.pointsEvolution
        assertEquals(5, points.size)
        val mediumPoint = points.find { it.xp == 2400 }
        assertTrue(mediumPoint != null)
        assertEquals(100f, mediumPoint?.score)
    }

    @Test
    fun `loadStats_filters_by_difficulty_correctly`() = runTest(testDispatcher) {
        // Arrange
        val games = listOf(
            createDummyResult(Difficulty.VERY_EASY, true, "10", 1250),
            createDummyResult(Difficulty.HARD, true, "13", 3000),
            createDummyResult(Difficulty.HARD, false, "15", null)
        )
        fakeRepository.results = games
        
        // Act
        viewModel.loadStats(Difficulty.HARD)
        
        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        
        assertEquals(2, state.totalGamesPlayed)
        assertEquals(1, state.totalGamesWon)
        
        val recentGames = state.recentGames
        assertEquals(2, recentGames.size)
        assertTrue(recentGames.all { it.difficultyResId == com.inigo.xudoku.R.string.diff_hard })
    }

    @Test
    fun `loadStats_with_no_data_does_not_crash_and_returns_empty_state`() = runTest(testDispatcher) {
        // Arrange
        fakeRepository.results = emptyList()
        
        // Act
        viewModel.loadStats(Difficulty.MEDIUM)
        
        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(0, state.totalGamesPlayed)
        assertEquals(0, state.totalGamesWon)
        assertEquals("—", state.winRate)
        assertTrue(state.recentGames.isEmpty())
        assertTrue(state.pointsEvolution.isEmpty())
        assertEquals("—", state.bestTime)
        assertEquals(0, state.averageScore)
        assertEquals(0, state.maxScore)
        assertEquals(0, state.averageXp)
        assertEquals(0, state.maxXp)
    }

    @Test
    fun `loadStats calculates average and max score and xp per game and difficulty`() = runTest(testDispatcher) {
        // Arrange
        val games = listOf(
            createDummyResult(Difficulty.HARD, true, "1", xpEarned = 1000, puntuacionFinal = 500),
            createDummyResult(Difficulty.HARD, true, "2", xpEarned = 2000, puntuacionFinal = 1500)
        )
        fakeRepository.results = games
        
        // Act
        viewModel.loadStats(Difficulty.HARD)
        
        // Assert
        val state = viewModel.uiState.value
        assertEquals(1000, state.averageScore)
        assertEquals(1500, state.maxScore)
        assertEquals(1500, state.averageXp)
        assertEquals(2000, state.maxXp)
    }

    @Test
    fun `loadStats correctly classifies failed or non-won games as completed false in recent flow and excludes them from metrics`() = runTest(testDispatcher) {
        // Arrange
        val games = listOf(
            createDummyResult(Difficulty.HARDEST, isWin = true, idSudoku = "1", xpEarned = 5000, puntuacionFinal = 3000),
            createDummyResult(Difficulty.HARDEST, isWin = false, idSudoku = "2", xpEarned = null, puntuacionFinal = 0)
        )
        fakeRepository.results = games

        // Act
        viewModel.loadStats(Difficulty.HARDEST)

        // Assert
        val state = viewModel.uiState.value
        assertEquals(2, state.totalGamesPlayed)
        assertEquals(1, state.totalGamesWon)
        assertEquals("50%", state.winRate)
        assertEquals(3000, state.maxScore)
        assertEquals(3000, state.averageScore)
        assertEquals(5000, state.maxXp)
        assertEquals(5000, state.averageXp)

        val recent = state.recentGames
        assertEquals(2, recent.size)

        val wonGame = recent.find { it.sudokuId == "1" }
        val lostGame = recent.find { it.sudokuId == "2" }

        assertTrue(wonGame?.completed == true)
        assertEquals(5000, wonGame?.xp)

        assertFalse(lostGame?.completed == true)
        org.junit.Assert.assertNull(lostGame?.xp)
    }

    @Test
    fun `loadStats calculates maxStreak and currentStreak correctly`() = runTest(testDispatcher) {
        // Arrange (ordered by date: 1, 2, 3, 4, 5)
        val games = listOf(
            createDummyResult(Difficulty.EASY, isWin = true, idSudoku = "1"),
            createDummyResult(Difficulty.EASY, isWin = true, idSudoku = "2"),
            createDummyResult(Difficulty.EASY, isWin = false, idSudoku = "3"),
            createDummyResult(Difficulty.EASY, isWin = true, idSudoku = "4"),
            createDummyResult(Difficulty.EASY, isWin = true, idSudoku = "5")
        )
        fakeRepository.results = games

        // Act
        viewModel.loadStats(Difficulty.EASY)

        // Assert
        val state = viewModel.uiState.value
        assertEquals(2, state.longestStreak)
        assertEquals(2, state.currentStreak)
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
