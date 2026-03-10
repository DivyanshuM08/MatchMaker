package com.example.matchmaker.domain

import kotlin.math.abs
import kotlin.math.max

/**
 * Computes a match score (0–100) between "my" profile and a card profile.
 */
object MatchScoreCalculator {

    /**
     * Computes match score for a card given "my" profile and the card's age and city
     * @return score from 0 to 100
     */
    fun compute(
        myAge: Int,
        myCity: String,
        cardAge: Int,
        cardCity: String
    ): Int {
        val ageDiff = abs(myAge - cardAge)
        val ageScore = max(0, 50 - 2 * ageDiff)
        val cityScore = if (myCity.equals(cardCity, ignoreCase = true)) 50 else 0
        return (ageScore + cityScore).coerceIn(0, 100)
    }
}
