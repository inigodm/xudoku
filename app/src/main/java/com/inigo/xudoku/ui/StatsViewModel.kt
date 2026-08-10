package com.inigo.xudoku.ui

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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

data class RecentGameUiModel(
    val label: String,
    val subtitle: String,
    val time: String,
    val xp: String,
    val completed: Boolean
)

data class StatsUiState(
    val isLoading: Boolean = true,
    val totalGamesPlayed: Int = 0,
    val totalGamesWon: Int = 0,
    val winRate: String = "—",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val pointsEvolution: List<Float> = emptyList(),
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

    fun loadStats(filter: String = "Global") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val allResults = withContext(Dispatchers.IO) {
                historyRepo.getAllResults()
            }
            
            val filteredResults = if (filter == "Global") {
                allResults
            } else {
                val diff = mapFilterToDifficulty(filter)
                if (diff != null) allResults.filter { it.dificultad == diff } else allResults
            }
            
            val newState = calculateStats(filteredResults, allResults)
            
            _uiState.update { newState }
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
        val last15 = sortedForChart.takeLast(15).map { it.puntuacionFinal.toFloat() }

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
            val diffLabel = when (game.dificultad) {
                Difficulty.VERY_EASY -> "Fácil"
                Difficulty.EASY -> "Medio"
                Difficulty.MEDIUM -> "Difícil"
                Difficulty.HARD -> "Extremo"
                Difficulty.HARDEST -> "Imposible"
            }
            RecentGameUiModel(
                label = "$diffLabel #${game.identificadorSudoku ?: "0"}",
                subtitle = dateFormat.format(game.fechaHoraFin).uppercase(),
                time = formatTime(game.tiempoEmpleado),
                xp = if (game.victoria) "+${game.puntuacionFinal} XP" else "DNF",
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

    private fun mapFilterToDifficulty(filter: String): Difficulty? {
        return when (filter) {
            "Fácil" -> Difficulty.VERY_EASY
            "Medio" -> Difficulty.EASY
            "Difícil" -> Difficulty.MEDIUM
            "Extremo" -> Difficulty.HARD
            "Imposible" -> Difficulty.HARDEST
            else -> null
        }
    }

    private fun formatTime(seconds: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds)
        val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }
}
