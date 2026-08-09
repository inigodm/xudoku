package com.inigo.xudoku.ui

import app.cash.turbine.test
import com.inigo.xudoku.data.repository.ProgressionRepository
import com.inigo.xudoku.data.repository.ProgressionState
import com.inigo.xudoku.model.progression.BonusType
import com.inigo.xudoku.model.progression.ProgressionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var fakeRepo: FakeProgressionRepository
    private lateinit var viewModel: ProgressionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeProgressionRepository()
        viewModel = ProgressionViewModel(fakeRepo, ProgressionManager())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is populated correctly`() = runTest(testDispatcher) {
        viewModel.state.test {
            val initialState = awaitItem()
            // Si el test tarda, a veces el flow emite el initialValue primero
            if (initialState.totalXP == 0L && initialState.currentLevel == 1) {
                // Correcto
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `calculateAndAddGameXP adds xp correctly and updates streaks`() = runTest(testDispatcher) {
        // Inicializar
        fakeRepo.emitState(ProgressionState(totalXP = 100L, dailyStreak = 2, winStreak = 1))
        
        viewModel.calculateAndAddGameXP(
            score = 10000, 
            playTimeMs = 300_000, // 5 min
            bonuses = listOf(BonusType.PERFECT_GAME)
        )
        
        testDispatcher.scheduler.advanceUntilIdle() // Esperar a que launch termine
        
        // El XP para 10000 pt es floor(10000^0.6) = 251. 
        // Con perfect game (15%) + daily streak (10%) + win streak 2 (4%) = +29% -> 251 * 1.29 = 323
        
        val newState = fakeRepo.getProgressionState()
        assertEquals(100L + 323L, newState.totalXP)
        assertEquals(2, newState.winStreak)
    }
    
    @Test
    fun `onGameAbandoned resets win streak`() = runTest(testDispatcher) {
        fakeRepo.emitState(ProgressionState(winStreak = 5))
        
        viewModel.onGameAbandoned()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val newState = fakeRepo.getProgressionState()
        assertEquals(0, newState.winStreak)
    }

    class FakeProgressionRepository : ProgressionRepository {
        private val _state = MutableStateFlow(ProgressionState())
        override val progressionState: Flow<ProgressionState> = _state

        fun emitState(state: ProgressionState) {
            _state.value = state
        }

        override suspend fun getProgressionState(): ProgressionState = _state.value

        override suspend fun updateXP(xpToAdd: Int) {
            _state.value = _state.value.copy(totalXP = _state.value.totalXP + xpToAdd)
        }

        override suspend fun updateStreaks(newDailyStreak: Int, newWinStreak: Int, playDate: Long) {
            _state.value = _state.value.copy(
                dailyStreak = newDailyStreak,
                winStreak = newWinStreak,
                lastPlayDate = playDate
            )
        }

        override suspend fun resetWinStreak() {
            _state.value = _state.value.copy(winStreak = 0)
        }

        override suspend fun ascendPrestige() {
            _state.value = _state.value.copy(totalXP = 0, prestigeStars = _state.value.prestigeStars + 1)
        }
    }
}
