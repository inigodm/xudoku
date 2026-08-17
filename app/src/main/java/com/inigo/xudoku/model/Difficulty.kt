package com.inigo.xudoku.model

/**
 * Sudoku difficulty levels defined by the number of pre-filled (visible) cells.
 * Fewer visible cells generally means a harder puzzle.
 */
enum class Difficulty(val visibleCells: Int, val maxHints: Int) {
    VERY_EASY(40, 3),
    EASY(30, 3),
    MEDIUM(25, 2),
    HARD(20, 1),
    HARDEST(17, 0)
}
