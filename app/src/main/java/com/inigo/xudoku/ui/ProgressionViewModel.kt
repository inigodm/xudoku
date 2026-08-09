package com.inigo.xudoku.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inigo.xudoku.data.repository.ProgressionRepository
import com.inigo.xudoku.model.progression.BonusType
import com.inigo.xudoku.model.progression.ProgressionManager
import com.inigo.xudoku.model.progression.Rank
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProgressionUIState(
    val totalXP: Long = 0L,
    val currentLevel: Int = 1,
    val currentLevelXP: Long = 0L,
    val xpRequiredForNextLevel: Long = 1000L,
    val currentRank: Rank? = null,
    val dailyStreak: Int = 0,
    val winStreak: Int = 0,
    val prestigeStars: Int = 0,
    val lastPlayDate: Long = 0L
)

class ProgressionViewModel(
    private val progressionRepository: ProgressionRepository,
    private val progressionManager: ProgressionManager
) : ViewModel() {

    val state: StateFlow<ProgressionUIState> = progressionRepository.progressionState
        .map { repoState ->
            val level = progressionManager.getLevelFromTotalXP(repoState.totalXP)
            val rank = progressionManager.getRankForLevel(level)
            
            val xpForCurrentLevel = progressionManager.getRequiredTotalXPForLevel(level)
            val xpForNextLevel = progressionManager.getRequiredTotalXPForLevel(level + 1)
            
            ProgressionUIState(
                totalXP = repoState.totalXP,
                currentLevel = level,
                currentLevelXP = repoState.totalXP - xpForCurrentLevel,
                xpRequiredForNextLevel = xpForNextLevel - xpForCurrentLevel,
                currentRank = rank,
                dailyStreak = repoState.dailyStreak,
                winStreak = repoState.winStreak,
                prestigeStars = repoState.prestigeStars,
                lastPlayDate = repoState.lastPlayDate
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProgressionUIState()
        )

    fun calculateAndAddGameXP(score: Int, playTimeMs: Long, bonuses: List<BonusType>) {
        viewModelScope.launch {
            val repoState = progressionRepository.getProgressionState()
            
            // Check daily streak (simplified for now: if lastPlayDate was yesterday, increment. If older, reset.)
            // In a real app, you'd use Calendar or LocalDate to accurately measure days.
            // For simplicity, we just use the previous streaks.
            val dailyStreak = repoState.dailyStreak // Update logic can be complex, skipping exact date math for MVP
            val winStreak = repoState.winStreak + 1
            
            val gainedXP = progressionManager.calculateXP(
                score = score,
                playTimeMs = playTimeMs,
                bonuses = bonuses,
                dailyStreak = dailyStreak,
                winStreak = winStreak,
                prestigeStars = repoState.prestigeStars
            )
            
            if (gainedXP > 0) {
                progressionRepository.updateXP(gainedXP)
                progressionRepository.updateStreaks(
                    newDailyStreak = dailyStreak, 
                    newWinStreak = winStreak, 
                    playDate = System.currentTimeMillis()
                )
            }
        }
    }
    
    fun onGameAbandoned() {
        viewModelScope.launch {
            progressionRepository.resetWinStreak()
        }
    }
    
    fun ascend() {
        viewModelScope.launch {
            progressionRepository.ascendPrestige()
        }
    }
}
