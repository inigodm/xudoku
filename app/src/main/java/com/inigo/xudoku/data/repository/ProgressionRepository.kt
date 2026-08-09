package com.inigo.xudoku.data.repository

import kotlinx.coroutines.flow.Flow

data class ProgressionState(
    val totalXP: Long = 0L,
    val dailyStreak: Int = 0,
    val winStreak: Int = 0,
    val prestigeStars: Int = 0,
    val lastPlayDate: Long = 0L // Timestamp of the last finished game
)

interface ProgressionRepository {
    val progressionState: Flow<ProgressionState>
    
    suspend fun getProgressionState(): ProgressionState
    
    suspend fun updateXP(xpToAdd: Int)
    
    suspend fun updateStreaks(
        newDailyStreak: Int, 
        newWinStreak: Int, 
        playDate: Long
    )
    
    suspend fun resetWinStreak()
    
    suspend fun ascendPrestige()
}
