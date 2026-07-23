package com.inigo.xudoku.model

/**
 * Sudoku difficulty levels defined by the number of pre-filled (visible) cells.
 * Fewer visible cells generally means a harder puzzle.
 */
enum class Difficulty(val visibleCells: Int) {
    VERY_EASY(40),
    EASY(30),
    MEDIUM(25),
    HARD(20),
    HARDEST(17)
}
