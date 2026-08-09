package com.inigo.xudoku.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SharedPreferencesProgressionRepository(context: Context) : ProgressionRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("xudoku_progression", Context.MODE_PRIVATE)
    
    private val _progressionState = MutableStateFlow(loadStateFromPrefs())
    override val progressionState: Flow<ProgressionState> = _progressionState.asStateFlow()

    private fun loadStateFromPrefs(): ProgressionState {
        return ProgressionState(
            totalXP = prefs.getLong(KEY_TOTAL_XP, 0L),
            dailyStreak = prefs.getInt(KEY_DAILY_STREAK, 0),
            winStreak = prefs.getInt(KEY_WIN_STREAK, 0),
            prestigeStars = prefs.getInt(KEY_PRESTIGE_STARS, 0),
            lastPlayDate = prefs.getLong(KEY_LAST_PLAY_DATE, 0L)
        )
    }

    override suspend fun getProgressionState(): ProgressionState = withContext(Dispatchers.IO) {
        _progressionState.value
    }

    override suspend fun updateXP(xpToAdd: Int) = withContext(Dispatchers.IO) {
        val currentState = _progressionState.value
        val newXP = currentState.totalXP + xpToAdd
        
        prefs.edit().putLong(KEY_TOTAL_XP, newXP).apply()
        
        _progressionState.value = currentState.copy(totalXP = newXP)
    }

    override suspend fun updateStreaks(
        newDailyStreak: Int,
        newWinStreak: Int,
        playDate: Long
    ) = withContext(Dispatchers.IO) {
        val currentState = _progressionState.value
        
        prefs.edit()
            .putInt(KEY_DAILY_STREAK, newDailyStreak)
            .putInt(KEY_WIN_STREAK, newWinStreak)
            .putLong(KEY_LAST_PLAY_DATE, playDate)
            .apply()
            
        _progressionState.value = currentState.copy(
            dailyStreak = newDailyStreak,
            winStreak = newWinStreak,
            lastPlayDate = playDate
        )
    }

    override suspend fun resetWinStreak() = withContext(Dispatchers.IO) {
        val currentState = _progressionState.value
        
        prefs.edit().putInt(KEY_WIN_STREAK, 0).apply()
        
        _progressionState.value = currentState.copy(winStreak = 0)
    }

    override suspend fun ascendPrestige() = withContext(Dispatchers.IO) {
        val currentState = _progressionState.value
        val newStars = currentState.prestigeStars + 1
        
        prefs.edit()
            .putLong(KEY_TOTAL_XP, 0L)
            .putInt(KEY_PRESTIGE_STARS, newStars)
            // Mantener rachas y última fecha jugada
            .apply()
            
        _progressionState.value = currentState.copy(
            totalXP = 0L,
            prestigeStars = newStars
        )
    }

    companion object {
        private const val KEY_TOTAL_XP = "total_xp"
        private const val KEY_DAILY_STREAK = "daily_streak"
        private const val KEY_WIN_STREAK = "win_streak"
        private const val KEY_PRESTIGE_STARS = "prestige_stars"
        private const val KEY_LAST_PLAY_DATE = "last_play_date"
    }
}
