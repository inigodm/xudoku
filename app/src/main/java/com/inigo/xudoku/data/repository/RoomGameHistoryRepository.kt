package com.inigo.xudoku.data.repository

import com.inigo.xudoku.data.local.SudokuGameResultDao
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.history.SudokuGameResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomGameHistoryRepository(
    private val dao: SudokuGameResultDao
) : GameHistoryRepository {

    override suspend fun saveGameResult(result: SudokuGameResult) {
        withContext(Dispatchers.IO) {
            dao.insert(result)
        }
    }

    override suspend fun getAllResults(): List<SudokuGameResult> = withContext(Dispatchers.IO) {
        return@withContext dao.getAll()
    }

    override fun getAllResultsFlow(): Flow<List<SudokuGameResult>> {
        return dao.getAllFlow()
    }

    override suspend fun getResultById(id: String): SudokuGameResult? = withContext(Dispatchers.IO) {
        return@withContext dao.getById(id)
    }

    override suspend fun deleteResult(id: String) {
        withContext(Dispatchers.IO) {
            dao.deleteById(id)
        }
    }

    override suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
        }
    }

    override suspend fun getTopScores(limit: Int): List<SudokuGameResult> = withContext(Dispatchers.IO) {
        return@withContext dao.getTopScores(limit)
    }

    override suspend fun getBestTimeForDifficulty(difficulty: Difficulty): SudokuGameResult? = withContext(Dispatchers.IO) {
        return@withContext dao.getBestTimeByDifficulty(difficulty.name)
    }

    override suspend fun getResultsByDifficulty(difficulty: Difficulty): List<SudokuGameResult> = withContext(Dispatchers.IO) {
        return@withContext dao.getByDifficulty(difficulty.name)
    }

    override suspend fun getResultsByDateRange(startDate: Long, endDate: Long): List<SudokuGameResult> = withContext(Dispatchers.IO) {
        return@withContext dao.getByDateRange(startDate, endDate)
    }

    override suspend fun getResultsByLevel(level: Int): List<SudokuGameResult> = withContext(Dispatchers.IO) {
        return@withContext dao.getByLevel(level)
    }
}
