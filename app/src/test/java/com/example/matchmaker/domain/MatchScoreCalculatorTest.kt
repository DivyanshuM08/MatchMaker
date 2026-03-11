package com.example.matchmaker.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class MatchScoreCalculatorTest {

    @Test
    fun sameAgeSameCity_returns100() {
        assertEquals(100, MatchScoreCalculator.compute(30, "Sydney", 30, "Sydney"))
    }

    @Test
    fun sameAgeDifferentCity_returns50() {
        assertEquals(50, MatchScoreCalculator.compute(30, "Sydney", 30, "Melbourne"))
    }

    @Test
    fun differentAgeSameCity_returns50PlusAgeScore() {
        // Age diff 5 -> ageScore = 50 - 10 = 40, city 50 -> 90
        assertEquals(90, MatchScoreCalculator.compute(30, "Sydney", 35, "Sydney"))
    }

    @Test
    fun differentAgeDifferentCity_returnsOnlyAgeScore() {
        // Age diff 0 -> 50, city 0 -> 50
        assertEquals(50, MatchScoreCalculator.compute(30, "Sydney", 30, "Auckland"))
        // Age diff 10 -> 50 - 20 = 30, city 0 -> 30
        assertEquals(30, MatchScoreCalculator.compute(30, "Sydney", 40, "Melbourne"))
    }

    @Test
    fun cityMatch_isCaseInsensitive() {
        assertEquals(100, MatchScoreCalculator.compute(25, "Sydney", 25, "sydney"))
        assertEquals(100, MatchScoreCalculator.compute(25, "SYDNEY", 25, "Sydney"))
    }

    @Test
    fun largeAgeDiff_capsAgeScoreAtZero() {
        // Age diff 30 -> 50 - 60 = -10 -> max(0, -10) = 0, city 0 -> 0
        assertEquals(0, MatchScoreCalculator.compute(30, "Sydney", 60, "Melbourne"))
    }

    @Test
    fun totalCappedAt100() {
        assertEquals(100, MatchScoreCalculator.compute(30, "Sydney", 30, "sydney"))
    }

    @Test
    fun zeroAgeDiff_sameCity_returns100() {
        assertEquals(100, MatchScoreCalculator.compute(0, "City", 0, "City"))
    }
}
