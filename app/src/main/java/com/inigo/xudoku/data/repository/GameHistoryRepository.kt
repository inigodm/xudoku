package com.inigo.xudoku.data.repository

import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.history.SudokuGameResult
import kotlinx.coroutines.flow.Flow

interface GameHistoryRepository {
    suspend fun saveGameResult(result: SudokuGameResult)
    suspend fun getAllResults(): List<SudokuGameResult>
    fun getAllResultsFlow(): Flow<List<SudokuGameResult>>
    suspend fun getResultById(id: String): SudokuGameResult?
    suspend fun deleteResult(id: String)
    suspend fun clearHistory()
    
    suspend fun getTopScores(limit: Int = 10): List<SudokuGameResult>
    suspend fun getBestTimeForDifficulty(difficulty: Difficulty): SudokuGameResult?
    suspend fun getResultsByDifficulty(difficulty: Difficulty): List<SudokuGameResult>
    suspend fun getResultsByDateRange(startDate: Long, endDate: Long): List<SudokuGameResult>
    suspend fun getResultsByLevel(level: Int): List<SudokuGameResult>
}
