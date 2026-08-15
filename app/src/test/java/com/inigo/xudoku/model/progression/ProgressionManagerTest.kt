package com.inigo.xudoku.model.progression

import org.junit.Test
import org.junit.Assert.assertEquals

import com.inigo.xudoku.model.Difficulty
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class ProgressionManagerTest {
    @Test
    fun testCalculateXP() {
        val manager = ProgressionManager()
        val xp = manager.calculateXP(
            score = 14480,
            playTimeMs = 252000L,
            bonuses = listOf(BonusType.PERFECT_GAME, BonusType.BLIND_SUDOKU),
            dailyStreak = 0,
            winStreak = 1,
            prestigeStars = 0
        )
        println("XP IS $xp")
        assertEquals(413, xp)
    }

    @Test
    fun testIsDifficultyUnlocked_veryEasyAlwaysUnlocked() {
        val manager = ProgressionManager()
        assertTrue(manager.isDifficultyUnlocked(Difficulty.VERY_EASY, 0L))
    }

    @Test
    fun testIsDifficultyUnlocked_thresholds() {
        val manager = ProgressionManager()
        // EASY requires 500 XP
        assertFalse(manager.isDifficultyUnlocked(Difficulty.EASY, 499L))
        assertTrue(manager.isDifficultyUnlocked(Difficulty.EASY, 500L))

        // MEDIUM requires 1500 XP
        assertFalse(manager.isDifficultyUnlocked(Difficulty.MEDIUM, 1499L))
        assertTrue(manager.isDifficultyUnlocked(Difficulty.MEDIUM, 1500L))

        // HARD requires 3500 XP
        assertFalse(manager.isDifficultyUnlocked(Difficulty.HARD, 3499L))
        assertTrue(manager.isDifficultyUnlocked(Difficulty.HARD, 3500L))

        // HARDEST requires 7500 XP
        assertFalse(manager.isDifficultyUnlocked(Difficulty.HARDEST, 7499L))
        assertTrue(manager.isDifficultyUnlocked(Difficulty.HARDEST, 7500L))
    }
}
