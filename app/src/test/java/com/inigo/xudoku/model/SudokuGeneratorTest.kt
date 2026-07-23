package com.inigo.xudoku.model

import org.junit.Assert.*
import org.junit.Test
import java.util.Random

class SudokuGeneratorTest {

    // ── generateGame basics ──────────────────────────────────────────────

    @Test
    fun `generated puzzle has exactly one solution`() {
        val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(42))
        assertEquals(1, game.puzzle.countSolutions(2))
    }

    @Test
    fun `solution is a valid completed board`() {
        val game = SudokuGenerator.generateGame(Difficulty.MEDIUM, Random(99))
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                assertNotEquals(
                    "Solution cell ($r,$c) should not be empty",
                    SudokuBoard.EMPTY, game.solution[r, c]
                )
            }
        }
    }

    @Test
    fun `solution satisfies all Sudoku constraints`() {
        val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(7))
        val sol = game.solution
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                val value = sol[r, c]
                sol.clear(r, c)
                assertTrue("Value $value at ($r,$c) violates a constraint", sol.isValid(r, c, value))
                sol[r, c] = value
            }
        }
    }

    @Test
    fun `puzzle visible cells match solution`() {
        val game = SudokuGenerator.generateGame(Difficulty.MEDIUM, Random(55))
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!game.puzzle.isEmpty(r, c)) {
                    assertEquals(
                        "Visible cell ($r,$c) must match solution",
                        game.solution[r, c], game.puzzle[r, c]
                    )
                }
            }
        }
    }

    @Test
    fun `puzzle and solution are independent copies`() {
        val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(1))
        val firstPuzzleCell = game.puzzle[0, 0]
        game.solution[0, 0] = SudokuBoard.EMPTY
        assertEquals(
            "Mutating solution must not affect puzzle",
            firstPuzzleCell, game.puzzle[0, 0]
        )
    }

    // ── difficulty-based clue counts ─────────────────────────────────────

    @Test
    fun `VERY_EASY produces roughly 40 visible cells`() {
        val game = SudokuGenerator.generateGame(Difficulty.VERY_EASY, Random(10))
        val filled = countFilled(game.puzzle)
        assertTrue("Expected ~40 visible cells, got $filled", filled in 35..45)
    }

    @Test
    fun `EASY produces roughly 30 visible cells`() {
        val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(20))
        val filled = countFilled(game.puzzle)
        assertTrue("Expected ~30 visible cells, got $filled", filled in 25..35)
    }

    @Test
    fun `HARD produces at most 25 visible cells`() {
        val game = SudokuGenerator.generateGame(Difficulty.HARD, Random(30))
        val filled = countFilled(game.puzzle)
        assertTrue("Expected ≤25 visible cells, got $filled", filled <= 25)
    }

    // ── countSolutions ───────────────────────────────────────────────────

    @Test
    fun `countSolutions returns 0 for an invalid board`() {
        val board = SudokuBoard()
        board[0, 0] = 5
        board[0, 1] = 5
        assertEquals(0, board.countSolutions(2))
    }

    @Test
    fun `countSolutions returns 1 for a fully solved board`() {
        val board = SudokuBoard.generateComplete(Random(42))
        assertEquals(1, board.countSolutions(2))
    }

    // ── Visual tests (run manually to inspect output) ──────────────────

    @Test
    fun `print solution and puzzle for visual inspection`() {
        val game = SudokuGenerator.generateGame(Difficulty.EASY, Random(42))
        printSolution(game.solution)
        println()
        printPuzzle(game.puzzle)
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun printSolution(board: SudokuBoard) {
        println("╔═════════╤═════════╤═════════╗  SOLUTION")
        for (r in 0 until SudokuBoard.SIZE) {
            if (r > 0 && r % 3 == 0) println("╟─────────┼─────────┼─────────╢")
            val sb = StringBuilder("║")
            for (c in 0 until SudokuBoard.SIZE) {
                sb.append(" ${board[r, c]}")
                if (c % 3 == 2) sb.append(" ║") else sb.append(" │"[0])
            }
            // replace the trailing │ artifacts – not needed, each group ends with ║
            println(sb)
        }
        println("╚═════════╧═════════╧═════════╝")
    }

    private fun printPuzzle(board: SudokuBoard) {
        val filled = countFilled(board)
        println("╔═════════╤═════════╤═════════╗  PUZZLE ($filled clues)")
        for (r in 0 until SudokuBoard.SIZE) {
            if (r > 0 && r % 3 == 0) println("╟─────────┼─────────┼─────────╢")
            val sb = StringBuilder("║")
            for (c in 0 until SudokuBoard.SIZE) {
                val v = board[r, c]
                sb.append(if (v == SudokuBoard.EMPTY) " ·" else " $v")
                if (c % 3 == 2) sb.append(" ║") else sb.append(" │"[0])
            }
            println(sb)
        }
        println("╚═════════╧═════════╧═════════╝")
    }

    private fun countFilled(board: SudokuBoard): Int {
        var count = 0
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (!board.isEmpty(r, c)) count++
            }
        }
        return count
    }
}
