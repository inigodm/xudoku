package com.inigo.xudoku.model

/**
 * Represents a 9×9 Sudoku board.
 * Cells contain values 1–9, or [EMPTY] (0) for unfilled positions.
 */
class SudokuBoard(
    private val cells: Array<IntArray> = Array(SIZE) { IntArray(SIZE) }
) {

    operator fun get(row: Int, col: Int): Int = cells[row][col]

    operator fun set(row: Int, col: Int, value: Int) {
        require(value in EMPTY..SIZE) { "Value must be between $EMPTY and $SIZE" }
        cells[row][col] = value
    }

    fun clear(row: Int, col: Int) {
        cells[row][col] = EMPTY
    }

    fun isEmpty(row: Int, col: Int): Boolean = cells[row][col] == EMPTY

    /**
     * Returns `true` if placing [number] at ([row], [col]) does not violate
     * any Sudoku constraint (row, column, and 3×3 box uniqueness).
     * The cell is assumed to be empty before the check.
     */
    fun isValid(row: Int, col: Int, number: Int): Boolean {
        // Row check
        for (c in 0 until SIZE) {
            if (cells[row][c] == number) return false
        }
        // Column check
        for (r in 0 until SIZE) {
            if (cells[r][col] == number) return false
        }
        // 3×3 box check
        val boxRowStart = (row / BOX_SIZE) * BOX_SIZE
        val boxColStart = (col / BOX_SIZE) * BOX_SIZE
        for (r in boxRowStart until boxRowStart + BOX_SIZE) {
            for (c in boxColStart until boxColStart + BOX_SIZE) {
                if (cells[r][c] == number) return false
            }
        }
        return true
    }

    /**
     * Solves the board in-place using deterministic backtracking.
     * Returns `true` if a solution was found, `false` if the board is unsolvable.
     */
    fun solve(): Boolean {
        // Fast-fail: check that pre-filled values don't violate any constraint
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) {
                val v = cells[r][c]
                if (v != EMPTY) {
                    cells[r][c] = EMPTY
                    if (!isValid(r, c, v)) {
                        cells[r][c] = v
                        return false
                    }
                    cells[r][c] = v
                }
            }
        }
        return solveFrom(0)
    }

    private fun solveFrom(index: Int): Boolean {
        val nextEmpty = (index until SIZE * SIZE).firstOrNull { isEmpty(it / SIZE, it % SIZE) }
            ?: return true // No empty cells left → solved

        val row = nextEmpty / SIZE
        val col = nextEmpty % SIZE

        for (num in 1..SIZE) {
            if (isValid(row, col, num)) {
                cells[row][col] = num
                if (solveFrom(nextEmpty + 1)) return true
                cells[row][col] = EMPTY
            }
        }
        return false
    }

    /**
     * Counts the number of distinct solutions for the current board state,
     * stopping as soon as [limit] solutions are found.
     * Operates on a copy so the board is not mutated.
     */
    fun countSolutions(limit: Int = 2): Int {
        require(limit >= 1) { "limit must be at least 1" }
        val clone = copy()
        // Fast-fail on invalid pre-filled values
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) {
                val v = clone.cells[r][c]
                if (v != EMPTY) {
                    clone.cells[r][c] = EMPTY
                    if (!clone.isValid(r, c, v)) return 0
                    clone.cells[r][c] = v
                }
            }
        }
        return clone.countFrom(0, limit)
    }

    private fun countFrom(index: Int, limit: Int): Int {
        val nextEmpty = (index until SIZE * SIZE).firstOrNull { isEmpty(it / SIZE, it % SIZE) }
            ?: return 1

        val row = nextEmpty / SIZE
        val col = nextEmpty % SIZE
        var count = 0
        for (num in 1..SIZE) {
            if (isValid(row, col, num)) {
                cells[row][col] = num
                count += countFrom(nextEmpty + 1, limit)
                cells[row][col] = EMPTY
                if (count >= limit) break
            }
        }
        return count
    }

    /**
     * Returns a deep copy of this board.
     */
    fun copy(): SudokuBoard = SudokuBoard(Array(SIZE) { cells[it].copyOf() })

    override fun toString(): String = buildString {
        for (r in 0 until SIZE) {
            if (r > 0 && r % BOX_SIZE == 0) appendLine("------+-------+------")
            for (c in 0 until SIZE) {
                if (c > 0 && c % BOX_SIZE == 0) append("| ")
                val v = cells[r][c]
                append(if (v == EMPTY) ". " else "$v ")
            }
            appendLine()
        }
    }

    companion object {
        const val SIZE = 9
        const val BOX_SIZE = 3
        const val EMPTY = 0

        /**
         * Generates a fully filled, valid Sudoku board using randomised backtracking.
         */
        fun generateComplete(random: java.util.Random = java.util.Random()): SudokuBoard {
            val board = SudokuBoard()
            fillRandomised(board, 0, random)
            return board
        }

        private fun fillRandomised(board: SudokuBoard, index: Int, random: java.util.Random): Boolean {
            val nextEmpty = (index until SIZE * SIZE)
                .firstOrNull { board.isEmpty(it / SIZE, it % SIZE) }
                ?: return true

            val row = nextEmpty / SIZE
            val col = nextEmpty % SIZE
            val candidates = (1..SIZE).shuffled(random)

            for (num in candidates) {
                if (board.isValid(row, col, num)) {
                    board.cells[row][col] = num
                    if (fillRandomised(board, nextEmpty + 1, random)) return true
                    board.cells[row][col] = EMPTY
                }
            }
            return false
        }
    }
}
