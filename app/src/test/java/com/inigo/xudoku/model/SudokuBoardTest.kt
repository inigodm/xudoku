package com.inigo.xudoku.model

import org.junit.Assert.*
import org.junit.Test
import java.util.Random

class SudokuBoardTest {

    // ── isValid ──────────────────────────────────────────────────────────

    @Test
    fun `isValid rejects duplicate in same row`() {
        val board = SudokuBoard()
        board[0, 0] = 5
        assertFalse(board.isValid(0, 4, 5))
    }

    @Test
    fun `isValid rejects duplicate in same column`() {
        val board = SudokuBoard()
        board[0, 0] = 5
        assertFalse(board.isValid(4, 0, 5))
    }

    @Test
    fun `isValid rejects duplicate in same 3x3 box`() {
        val board = SudokuBoard()
        board[0, 0] = 5
        assertFalse(board.isValid(2, 2, 5))
    }

    @Test
    fun `isValid accepts number that violates no constraint`() {
        val board = SudokuBoard()
        board[0, 0] = 5
        assertTrue(board.isValid(3, 3, 5))
    }

    // ── solve ────────────────────────────────────────────────────────────

    @Test
    fun `solve completes a valid partial board`() {
        val board = SudokuBoard()
        // Set up a known solvable top row minus last cell
        val topRow = intArrayOf(5, 3, 4, 6, 7, 8, 9, 1, 0)
        for (c in topRow.indices) board[0, c] = topRow[c]

        assertTrue(board.solve())
        assertEquals(2, board[0, 8])
    }

    @Test
    fun `solve returns false for an unsolvable board`() {
        val board = SudokuBoard()
        // Two 5s in the same row → unsolvable
        board[0, 0] = 5
        board[0, 1] = 5
        assertFalse(board.solve())
    }

    // ── generateComplete ────────────────────────────────────────────────

    @Test
    fun `generateComplete produces a fully filled board`() {
        val board = SudokuBoard.generateComplete(Random(42))
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                assertNotEquals("Cell ($r,$c) should not be empty", SudokuBoard.EMPTY, board[r, c])
            }
        }
    }

    @Test
    fun `generateComplete produces a board with no rule violations`() {
        val board = SudokuBoard.generateComplete(Random(123))
        for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                val value = board[r, c]
                board.clear(r, c)
                assertTrue(
                    "Value $value at ($r,$c) should be valid",
                    board.isValid(r, c, value)
                )
                board[r, c] = value
            }
        }
    }

    @Test
    fun `generateComplete with different seeds produces different boards`() {
        val board1 = SudokuBoard.generateComplete(Random(1))
        val board2 = SudokuBoard.generateComplete(Random(2))
        var differ = false
        outer@ for (r in 0 until SudokuBoard.SIZE) {
            for (c in 0 until SudokuBoard.SIZE) {
                if (board1[r, c] != board2[r, c]) { differ = true; break@outer }
            }
        }
        assertTrue("Different seeds should produce different boards", differ)
    }

    // ── copy ─────────────────────────────────────────────────────────────

    @Test
    fun `copy creates an independent deep copy`() {
        val original = SudokuBoard.generateComplete(Random(7))
        val copy = original.copy()
        copy[0, 0] = SudokuBoard.EMPTY
        assertNotEquals(original[0, 0], copy[0, 0])
    }
}
