package com.inigo.xudoku.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.SudokuBoard
import com.inigo.xudoku.model.SudokuGame
import com.inigo.xudoku.model.SudokuGenerator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.inigo.xudoku.model.scoring.ScoreManager
import com.inigo.xudoku.model.scoring.ScoreConfig
import com.inigo.xudoku.data.repository.GameHistoryRepository
import com.inigo.xudoku.model.history.SudokuGameResult
import java.util.Date
import java.util.UUID

/** Estado inmutable de una celda del tablero visible en la UI. */
data class CellState(
    val value: Int,       // 0 = vacía; 1-9 = número
    val isGiven: Boolean, // true → número pre-rellenado del puzzle, no editable
    val isError: Boolean  // true → el jugador introdujo un número incorrecto
)

/** Evento emitido cuando se ganan puntos para animar en la UI. */
data class ScoreAnimationEvent(
    val row: Int,
    val col: Int,
    val points: Int,
    val isTripleCombo: Boolean
)

/** Un movimiento guardado para poder deshacerlo. */
private data class GameMove(
    val row: Int,
    val col: Int,
    val previousValue: Int,
    val previousNotes: Set<Int>,
    val previousWasError: Boolean
)

/** ViewModel que gestiona el estado mutable de una partida de Sudoku. */
class GameViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val historyRepo: GameHistoryRepository? = null
) : ViewModel() {

    private lateinit var game: SudokuGame
    private var gameStartTime: Date? = null

    // ── Estado expuesto ──────────────────────────────────────────────────────

    private val _cells = MutableStateFlow(emptyBoard())
    /** Grid 9×9 de estados de celda. */
    val cells: StateFlow<Array<Array<CellState>>> = _cells.asStateFlow()

    private val _notes = MutableStateFlow<Map<Pair<Int, Int>, Set<Int>>>(emptyMap())
    /** Notas en lápiz por celda: (row, col) → conjunto de dígitos anotados. */
    val notes: StateFlow<Map<Pair<Int, Int>, Set<Int>>> = _notes.asStateFlow()

    private val _selectedCell = MutableStateFlow<Pair<Int, Int>?>(null)
    /** Celda actualmente seleccionada, o null si ninguna. */
    val selectedCell: StateFlow<Pair<Int, Int>?> = _selectedCell.asStateFlow()

    private val _isNotesMode = MutableStateFlow(false)
    /** true → el teclado escribe notas en lápiz en vez de valores definitivos. */
    val isNotesMode: StateFlow<Boolean> = _isNotesMode.asStateFlow()

    private val _mistakes = MutableStateFlow(0)
    /** Número de errores cometidos en la partida actual (máx 3 en el diseño). */
    val mistakes: StateFlow<Int> = _mistakes.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    /** Segundos transcurridos desde que empezó la partida. */
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    /** true → el puzzle está completamente resuelto sin errores. */
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    /** true → el jugador agotó los 3 errores permitidos. */
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    private val _difficulty = MutableStateFlow<Difficulty?>(null)
    /** Dificultad de la partida en curso. */
    val difficulty: StateFlow<Difficulty?> = _difficulty.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    /** true mientras se genera el puzzle en background. */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val scoreManager = ScoreManager()

    private val _currentScore = MutableStateFlow(0)
    /** Puntuación acumulada durante la partida. */
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()

    private val _scoreEvents = MutableSharedFlow<ScoreAnimationEvent>(extraBufferCapacity = 10)
    /** Eventos de animación de puntuación para la UI. */
    val scoreEvents: SharedFlow<ScoreAnimationEvent> = _scoreEvents.asSharedFlow()

    // ── Estado interno ───────────────────────────────────────────────────────

    private val moveHistory = ArrayDeque<GameMove>()
    private var timerJob: Job? = null

    // ── Acciones ─────────────────────────────────────────────────────────────

    /** Genera un nuevo puzzle con la dificultad dada y reinicia todo el estado. */
    fun startGame(difficulty: Difficulty) {
        timerJob?.cancel()
        moveHistory.clear()
        gameStartTime = Date()
        _mistakes.value      = 0
        _elapsedSeconds.value = 0
        _selectedCell.value  = null
        _notes.value         = emptyMap()
        _isNotesMode.value   = false
        _isCompleted.value   = false
        _isGameOver.value    = false
        _difficulty.value    = difficulty
        _isLoading.value     = true
        
        scoreManager.reset()
        _currentScore.value  = 0

        viewModelScope.launch {
            val generatedGame = withContext(ioDispatcher) {
                SudokuGenerator.generateGame(difficulty)
            }
            game = generatedGame
            _cells.value = Array(SudokuBoard.SIZE) { row ->
                Array(SudokuBoard.SIZE) { col ->
                    val v = game.puzzle[row, col]
                    CellState(value = v, isGiven = v != SudokuBoard.EMPTY, isError = false)
                }
            }
            _isLoading.value = false
            startTimer()
        }
    }

    /** Selecciona una celda (dadas también son seleccionables para resaltar). */
    fun selectCell(row: Int, col: Int) {
        _selectedCell.value = Pair(row, col)
    }

    /** Introduce un número en la celda seleccionada (o lo anota en modo notas). */
    fun enterNumber(number: Int) {
        val (row, col) = _selectedCell.value ?: return
        val cell = _cells.value[row][col]
        if (cell.isGiven || _isGameOver.value) return

        val key = Pair(row, col)

        if (_isNotesMode.value) {
            val current = _notes.value[key] ?: emptySet()
            saveMove(row, col, cell, current)
            val updated = if (number in current) current - number else current + number
            _notes.update { it + (key to updated) }
        } else {
            val correct = game.solution[row, col]
            val isError = number != correct
            saveMove(row, col, cell, _notes.value[key] ?: emptySet())
            updateCell(row, col) { CellState(value = number, isGiven = false, isError = isError) }
            _notes.update { it - key }
            if (isError) {
                scoreManager.recordMistake()
                _mistakes.update { count ->
                    val newCount = count + 1
                    if (newCount >= 3) {
                        _isGameOver.value = true
                        timerJob?.cancel()
                        saveGameResult()
                    }
                    newCount
                }
            } else {
                val grid = _cells.value
                val (isRowComplete, isColComplete, isBlockComplete, isLastCell) = checkRegionCompletion(row, col, grid)
                val moveResult = scoreManager.calculateAndAddMoveScore(
                    difficulty = _difficulty.value!!,
                    currentTimeSeconds = _elapsedSeconds.value,
                    filledCellsRatio = calculateFilledRatio(),
                    isRowComplete = isRowComplete,
                    isColComplete = isColComplete,
                    isBlockComplete = isBlockComplete,
                    isLastCell = isLastCell
                )
                _currentScore.value = scoreManager.currentScore
                _scoreEvents.tryEmit(ScoreAnimationEvent(row, col, moveResult.totalPoints, moveResult.isTripleCombo))
            }
            checkCompletion()
        }
    }

    /** Borra el contenido de la celda seleccionada (si no es una celda dada). */
    fun clearSelectedCell() {
        val (row, col) = _selectedCell.value ?: return
        val cell = _cells.value[row][col]
        if (cell.isGiven) return
        val key = Pair(row, col)
        saveMove(row, col, cell, _notes.value[key] ?: emptySet())
        updateCell(row, col) { CellState(0, false, false) }
        _notes.update { it - key }
    }

    /** Alterna entre modo normal y modo notas en lápiz. */
    fun toggleNotesMode() {
        _isNotesMode.update { !it }
    }

    /** Deshace el último movimiento registrado. */
    fun undoLastMove() {
        val move = moveHistory.removeLastOrNull() ?: return
        val key = Pair(move.row, move.col)
        val current = _cells.value[move.row][move.col]
        // Revertir el contador de errores si corresponde
        if (current.isError && !move.previousWasError) {
            _mistakes.update { maxOf(0, it - 1) }
        }
        updateCell(move.row, move.col) {
            CellState(value = move.previousValue, isGiven = false, isError = move.previousWasError)
        }
        if (move.previousNotes.isNotEmpty()) {
            _notes.update { it + (key to move.previousNotes) }
        } else {
            _notes.update { it - key }
        }
        _isCompleted.value = false
    }

    /**
     * Revela el valor correcto en la celda seleccionada (o la primera vacía si
     * no hay celda seleccionada).
     */
    fun requestHint() {
        val target = _selectedCell.value?.takeIf { (r, c) ->
            val cell = _cells.value[r][c]
            !cell.isGiven && cell.value == SudokuBoard.EMPTY
        } ?: run {
            // Buscar la primera celda vacía no dada
            for (r in 0 until SudokuBoard.SIZE) {
                for (c in 0 until SudokuBoard.SIZE) {
                    val cell = _cells.value[r][c]
                    if (!cell.isGiven && cell.value == SudokuBoard.EMPTY) {
                        return applyHint(r, c)
                    }
                }
            }
            return
        }
        applyHint(target.first, target.second)
    }

    // ── Helpers internos ─────────────────────────────────────────────────────

    private fun applyHint(row: Int, col: Int) {
        if (_cells.value[row][col].isGiven) return
        val correct = game.solution[row, col]
        scoreManager.recordHint()
        updateCell(row, col) { CellState(value = correct, isGiven = false, isError = false) }
        _notes.update { it - Pair(row, col) }
        _selectedCell.value = Pair(row, col)
        checkCompletion()
    }

    private fun saveMove(row: Int, col: Int, cell: CellState, notes: Set<Int>) {
        moveHistory.addLast(
            GameMove(
                row              = row,
                col              = col,
                previousValue    = cell.value,
                previousNotes    = notes,
                previousWasError = cell.isError
            )
        )
    }

    private fun updateCell(row: Int, col: Int, transform: (CellState) -> CellState) {
        _cells.update { current ->
            val newGrid = Array(SudokuBoard.SIZE) { r -> current[r].copyOf() }
            newGrid[row][col] = transform(current[row][col])
            newGrid
        }
    }

    private fun checkCompletion() {
        val board = _cells.value
        val complete = (0 until SudokuBoard.SIZE).all { r ->
            (0 until SudokuBoard.SIZE).all { c ->
                board[r][c].value != SudokuBoard.EMPTY && !board[r][c].isError
            }
        }
        if (complete) {
            _isCompleted.value = true
            timerJob?.cancel()
            saveGameResult()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000L)
                _elapsedSeconds.update { it + 1 }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun getFinalScore(): Int {
        return scoreManager.calculateFinalScore(
            difficulty = _difficulty.value ?: Difficulty.EASY,
            totalTimeSeconds = _elapsedSeconds.value
        )
    }

    private fun checkRegionCompletion(row: Int, col: Int, grid: Array<Array<CellState>>): List<Boolean> {
        val isRowComplete = (0 until SudokuBoard.SIZE).all { c -> grid[row][c].value != SudokuBoard.EMPTY && !grid[row][c].isError }
        val isColComplete = (0 until SudokuBoard.SIZE).all { r -> grid[r][col].value != SudokuBoard.EMPTY && !grid[r][col].isError }
        
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        var isBlockComplete = true
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (grid[r][c].value == SudokuBoard.EMPTY || grid[r][c].isError) {
                    isBlockComplete = false
                }
            }
        }
        
        val isLastCell = (0 until SudokuBoard.SIZE).all { r ->
            (0 until SudokuBoard.SIZE).all { c ->
                grid[r][c].value != SudokuBoard.EMPTY && !grid[r][c].isError
            }
        }
        
        return listOf(isRowComplete, isColComplete, isBlockComplete, isLastCell)
    }

    private fun calculateFilledRatio(): Float {
        var filled = 0
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                val cell = _cells.value[r][c]
                if (cell.value != SudokuBoard.EMPTY && !cell.isError) filled++
            }
        }
        return filled.toFloat() / (SudokuBoard.SIZE * SudokuBoard.SIZE)
    }

    private fun emptyBoard(): Array<Array<CellState>> =
        Array(SudokuBoard.SIZE) { Array(SudokuBoard.SIZE) { CellState(0, false, false) } }
        
    private fun saveGameResult() {
        val repo = historyRepo ?: return
        val startTime = gameStartTime ?: Date()
        val endTime = Date()
        val difficultyEnum = _difficulty.value ?: Difficulty.EASY
        val isWin = _isCompleted.value
        
        var placed = 0
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                val cell = _cells.value[r][c]
                if (!cell.isGiven && cell.value != SudokuBoard.EMPTY) placed++
            }
        }
        
        val result = SudokuGameResult(
            id = UUID.randomUUID().toString(),
            fechaHoraInicio = startTime,
            fechaHoraFin = endTime,
            tiempoEmpleado = _elapsedSeconds.value.toLong(),
            tiempoPausado = 0L,
            dificultad = difficultyEnum,
            nivel = 1,
            identificadorSudoku = null,
            seed = null,
            tamanoTablero = SudokuBoard.SIZE,
            puntuacionPartida = scoreManager.currentScore,
            puntuacionFinal = getFinalScore(),
            multiplicadorDificultad = ScoreConfig.getDifficultyMultiplier(difficultyEnum),
            multiplicadorTiempo = 1.0f,
            ayudasMostrarNumero = scoreManager.hintsUsed,
            ayudasResolverCasilla = 0,
            ayudasComprobarErrores = 0,
            totalAyudas = scoreManager.hintsUsed,
            erroresCometidos = _mistakes.value,
            partidaPerfecta = _mistakes.value == 0,
            movimientosTotales = moveHistory.size,
            numerosColocados = placed,
            porcentajeCompletadoManual = calculateFilledRatio(),
            porcentajeCompletadoConAyudas = 0f,
            completado = isWin,
            abandono = false,
            victoria = isWin,
            versionJuego = 1,
            versionAlgoritmoPuntuacion = 1,
            metadata = "{}"
        )
        
        viewModelScope.launch {
            repo.saveGameResult(result)
        }
    }
}
