package com.inigo.xudoku.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inigo.xudoku.data.repository.GameHistoryRepository
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.history.SudokuGameResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

data class RecentGameUiModel(
    @StringRes val difficultyResId: Int,
    val sudokuId: String,
    val subtitle: String,
    val time: String,
    val xp: Int?,
    val completed: Boolean
)

data class PointData(
    val score: Float,
    val xp: Int
)

data class StatsUiState(
    val isLoading: Boolean = true,
    val totalGamesPlayed: Int = 0,
    val totalGamesWon: Int = 0,
    val winRate: String = "—",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val pointsEvolution: List<PointData> = emptyList(),
    val bestTime: String = "—",
    val averageTime: String = "—",
    val recentGames: List<RecentGameUiModel> = emptyList(),
    val difficultySplit: List<Float> = listOf(0f, 0f, 0f, 0f, 0f) // VeryEasy, Easy, Medium, Hard, Extreme
)

class StatsViewModel(
    private val historyRepo: GameHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    fun loadStats(difficulty: Difficulty? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val allResults = withContext(Dispatchers.IO) {
                historyRepo.getAllResults()
            }
            
            val filteredResults = if (difficulty == null) {
                allResults
            } else {
                allResults.filter { it.dificultad == difficulty }
            }
            
            val newState = calculateStats(filteredResults, allResults)
            
            _uiState.update { newState }
        }
    }

    private fun extractXp(game: SudokuGameResult): Int {
        return try {
            val json = JSONObject(game.metadata)
            if (json.has("xpEarned")) json.getInt("xpEarned") else game.puntuacionFinal
        } catch (e: Exception) {
            game.puntuacionFinal
        }
    }

    private fun calculateStats(
        results: List<SudokuGameResult>, 
        allResults: List<SudokuGameResult> // Used for difficulty split to maintain global view
    ): StatsUiState {
        if (results.isEmpty()) {
            return StatsUiState(isLoading = false, difficultySplit = calculateDifficultySplit(allResults))
        }

        val gamesPlayed = results.size
        val completedGames = results.filter { it.completado || it.victoria }
        val gamesWon = completedGames.size
        
        val winRate = if (results.isNotEmpty()) {
            "${((gamesWon.toFloat() / results.size) * 100).toInt()}%"
        } else "—"

        // Points evolution (last 15 games)
        // Ensure chronological order for chart (oldest to newest)
        val sortedForChart = results.sortedBy { it.fechaHoraFin }
        val last15 = sortedForChart.takeLast(15).map { 
            PointData(it.puntuacionFinal.toFloat(), extractXp(it)) 
        }

        // Time calculations
        val bestTimeSeconds = completedGames.minByOrNull { it.tiempoEmpleado }?.tiempoEmpleado
        val bestTime = bestTimeSeconds?.let { formatTime(it) } ?: "—"

        val avgTimeSeconds = if (completedGames.isNotEmpty()) {
            completedGames.map { it.tiempoEmpleado }.average().toLong()
        } else null
        val averageTime = avgTimeSeconds?.let { formatTime(it) } ?: "—"

        // Streak calculation (consecutive wins)
        // Results from DB are usually ordered DESC (newest first). Let's sort DESC to be sure.
        val sortedDesc = results.sortedByDescending { it.fechaHoraFin }
        var currentStreak = 0
        for (game in sortedDesc) {
            if (game.victoria) currentStreak++
            else break
        }

        var maxStreak = 0
        var tempStreak = 0
        // Calculate max streak going chronological
        for (game in sortedForChart) {
            if (game.victoria) {
                tempStreak++
                if (tempStreak > maxStreak) maxStreak = tempStreak
            } else {
                tempStreak = 0
            }
        }

        // Recent Flow
        val recentGames = sortedDesc.take(10).map { game ->
            val diffResId = when (game.dificultad) {
                Difficulty.VERY_EASY -> com.inigo.xudoku.R.string.diff_very_easy
                Difficulty.EASY -> com.inigo.xudoku.R.string.diff_easy
                Difficulty.MEDIUM -> com.inigo.xudoku.R.string.diff_medium
                Difficulty.HARD -> com.inigo.xudoku.R.string.diff_hard
                Difficulty.HARDEST -> com.inigo.xudoku.R.string.diff_hardest
            }
            RecentGameUiModel(
                difficultyResId = diffResId,
                sudokuId = game.identificadorSudoku ?: "0",
                subtitle = dateFormat.format(game.fechaHoraFin).uppercase(),
                time = formatTime(game.tiempoEmpleado),
                xp = if (game.victoria) extractXp(game) else null,
                completed = game.victoria
            )
        }

        val difficultySplit = calculateDifficultySplit(results)

        return StatsUiState(
            isLoading = false,
            totalGamesPlayed = gamesPlayed,
            totalGamesWon = gamesWon,
            winRate = winRate,
            currentStreak = currentStreak,
            longestStreak = maxStreak,
            pointsEvolution = last15,
            bestTime = bestTime,
            averageTime = averageTime,
            recentGames = recentGames,
            difficultySplit = difficultySplit
        )
    }

    private fun calculateDifficultySplit(results: List<SudokuGameResult>): List<Float> {
        val total = results.size.toFloat()
        if (total == 0f) return listOf(0f, 0f, 0f, 0f, 0f)

        val veryEasyCount = results.count { it.dificultad == Difficulty.VERY_EASY }
        val easyCount = results.count { it.dificultad == Difficulty.EASY }
        val mediumCount = results.count { it.dificultad == Difficulty.MEDIUM }
        val hardCount = results.count { it.dificultad == Difficulty.HARD }
        val extremeCount = results.count { it.dificultad == Difficulty.HARDEST }

        return listOf(
            veryEasyCount / total,
            easyCount / total,
            mediumCount / total,
            hardCount / total,
            extremeCount / total
        )
    }

    private fun formatTime(seconds: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds)
        val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }
}
