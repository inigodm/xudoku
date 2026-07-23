package com.inigo.xudoku.model

import java.util.Random

/**
 * Result of puzzle generation: the [puzzle] to be solved by the player
 * and the fully filled [solution].
 */
data class SudokuGame(
    val puzzle: SudokuBoard,
    val solution: SudokuBoard
)

/**
 * Generates playable Sudoku puzzles with a unique solution.
 */
object SudokuGenerator {

    private const val TOTAL_CELLS = SudokuBoard.SIZE * SudokuBoard.SIZE
    private const val MAX_RETRIES = 5

    /**
     * Generates a Sudoku puzzle for the given [difficulty].
     *
     * The algorithm:
     * 1. Creates a fully solved board.
     * 2. Iterates through cells in random order, removing each one only if
     *    the resulting puzzle still has exactly one solution.
     * 3. Stops when the target number of visible cells is reached.
     *
     * If a single pass doesn't reach the target (common for HARDEST),
     * the generation is retried up to [MAX_RETRIES] times, keeping the
     * best result.
     */
    fun generateGame(
        difficulty: Difficulty,
        random: Random = Random()
    ): SudokuGame {
        var bestPuzzle: SudokuBoard? = null
        var bestFilledCount = TOTAL_CELLS

        repeat(MAX_RETRIES) {
            val solution = SudokuBoard.generateComplete(random)
            val puzzle = solution.copy()
            val filledCount = removeCells(puzzle, difficulty.visibleCells, random)

            if (filledCount <= bestFilledCount) {
                bestPuzzle = puzzle
                bestFilledCount = filledCount
            }
            if (filledCount <= difficulty.visibleCells) return SudokuGame(puzzle, solution)
        }

        // Return the best attempt even if we didn't fully reach the target
        val solution = bestPuzzle!!.copy()
        solution.solve()
        return SudokuGame(bestPuzzle!!, solution)
    }

    /**
     * Removes cells from [board] one at a time in random order,
     * keeping only removals that preserve a unique solution.
     * Returns the number of filled cells remaining.
     */
    private fun removeCells(
        board: SudokuBoard,
        targetVisible: Int,
        random: Random
    ): Int {
        val positions = (0 until TOTAL_CELLS).shuffled(random)
        var filledCount = TOTAL_CELLS

        for (pos in positions) {
            if (filledCount <= targetVisible) break

            val row = pos / SudokuBoard.SIZE
            val col = pos % SudokuBoard.SIZE
            if (board.isEmpty(row, col)) continue

            val backup = board[row, col]
            board.clear(row, col)

            if (board.countSolutions(2) != 1) {
                board[row, col] = backup
            } else {
                filledCount--
            }
        }
        return filledCount
    }
}
