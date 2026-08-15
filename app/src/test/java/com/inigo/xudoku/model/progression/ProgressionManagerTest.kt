package com.inigo.xudoku.model.progression

import org.junit.Test
import org.junit.Assert.assertEquals

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
        assertEquals(571, xp)
    }
}
