package com.inigo.xudoku.model.progression

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import com.inigo.xudoku.R

class ProgressionManagerTest {

    private val progressionManager = ProgressionManager()

    @Test
    fun `calculateXP returns 0 if playtime is too short (exploit)`() {
        val xp = progressionManager.calculateXP(
            score = 100000,
            playTimeMs = 30_000L, // Menos del mínimo
            bonuses = emptyList(),
            dailyStreak = 5,
            winStreak = 5,
            prestigeStars = 0
        )
        assertEquals(0, xp)
    }

    @Test
    fun `calculateXP applies correct base math`() {
        // Base XP for 11000 points is ~265
        val xp = progressionManager.calculateXP(
            score = 11000,
            playTimeMs = 120_000L,
            bonuses = emptyList(),
            dailyStreak = 0,
            winStreak = 0,
            prestigeStars = 0
        )
        assertEquals(265, xp)
    }

    @Test
    fun `calculateXP applies bonuses correctly`() {
        val baseScore = 11000 // base XP = 265
        // daily streak = 5 -> +0.25
        // win streak = 2 -> +0.04
        // perfect game -> +0.15
        // Total multiplier = 1 + 0.25 + 0.04 + 0.15 = 1.44
        // 265 * 1.44 = 381.6 -> 381
        val xp = progressionManager.calculateXP(
            score = baseScore,
            playTimeMs = 120_000L,
            bonuses = listOf(BonusType.PERFECT_GAME),
            dailyStreak = 5,
            winStreak = 2,
            prestigeStars = 0
        )
        assertEquals(381, xp)
    }
    
    @Test
    fun `calculateXP respects streak maximums`() {
        val baseScore = 11000 // base XP = 265
        // daily streak = 10 -> capped at 0.25
        // win streak = 10 -> capped at 0.10
        // Total multiplier = 1 + 0.25 + 0.10 = 1.35
        // 265 * 1.35 = 357.75 -> 357
        val xp = progressionManager.calculateXP(
            score = baseScore,
            playTimeMs = 120_000L,
            bonuses = emptyList(),
            dailyStreak = 10,
            winStreak = 10,
            prestigeStars = 0
        )
        assertEquals(357, xp)
    }

    @Test
    fun `getRequiredTotalXPForLevel calculates correctly`() {
        assertEquals(0L, progressionManager.getRequiredTotalXPForLevel(1))
        
        // Level 2 -> 150 * 2^1.5 + 6 * 2^2.2 
        // 150 * 2.8284 + 6 * 4.5947
        // 424.26 + 27.56 = 451
        val level2XP = progressionManager.getRequiredTotalXPForLevel(2)
        assertTrue(level2XP in 450L..452L)
        
        // Level 100 -> 150 * 1000 + 6 * 25118 = 150000 + 150713 = 300713
        val level100XP = progressionManager.getRequiredTotalXPForLevel(100)
        assertTrue(level100XP in 300000L..301000L)
    }

    @Test
    fun `getLevelFromTotalXP works correctly`() {
        assertEquals(1, progressionManager.getLevelFromTotalXP(0L))
        
        val level2XP = progressionManager.getRequiredTotalXPForLevel(2)
        assertEquals(2, progressionManager.getLevelFromTotalXP(level2XP))
        assertEquals(1, progressionManager.getLevelFromTotalXP(level2XP - 1))
        
        val level100XP = progressionManager.getRequiredTotalXPForLevel(100)
        assertEquals(100, progressionManager.getLevelFromTotalXP(level100XP))
        assertEquals(100, progressionManager.getLevelFromTotalXP(level100XP + 10000))
    }

    @Test
    fun `getRankForLevel returns correct rank`() {
        val rankLvl1 = progressionManager.getRankForLevel(1)
        assertEquals(R.string.rank_novato, rankLvl1.nameResId)
        
        val rankLvl4 = progressionManager.getRankForLevel(4)
        assertEquals(R.string.rank_novato, rankLvl4.nameResId)

        val rankLvl5 = progressionManager.getRankForLevel(5)
        assertEquals(R.string.rank_aprendiz, rankLvl5.nameResId)

        val rankLvl100 = progressionManager.getRankForLevel(100)
        assertEquals(R.string.rank_xudoku_supremo, rankLvl100.nameResId)
        
        // Capped beyond 100
        val rankLvl105 = progressionManager.getRankForLevel(105)
        assertEquals(R.string.rank_xudoku_supremo, rankLvl105.nameResId)
    }
}
