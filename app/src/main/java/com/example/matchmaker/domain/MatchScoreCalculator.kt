package com.example.matchmaker.domain

import kotlin.math.abs
import kotlin.math.max

/**
 * Computes a match score (0–100) between "my" profile and a card profile
 * using age, location (city/country), religion, and occupation.
 *
 * Weights: Age 35%, Location 30%, Religion 20%, Occupation 15%.
 */
object MatchScoreCalculator {

    private const val WEIGHT_AGE = 35
    private const val WEIGHT_LOCATION = 30
    private const val WEIGHT_RELIGION = 20
    private const val WEIGHT_OCCUPATION = 15

    /**
     * Computes match score for a card given "my" profile and the card's attributes.
     * @return score from 0 to 100
     */
    fun compute(
        myAge: Int,
        myCity: String,
        myCountry: String,
        myReligion: String,
        myOccupation: String,
        cardAge: Int,
        cardCity: String,
        cardCountry: String,
        cardReligion: String,
        cardOccupation: String
    ): Int {
        val ageScore = computeAgeScore(myAge, cardAge)
        val locationScore = computeLocationScore(myCity, myCountry, cardCity, cardCountry)
        val religionScore = computeReligionScore(myReligion, cardReligion)
        val occupationScore = computeOccupationScore(myOccupation, cardOccupation)
        return (ageScore + locationScore + religionScore + occupationScore).coerceIn(0, 100)
    }

    /**
     * Age: up to WEIGHT_AGE points. Closer age = higher score; decays by ~1.75 per year diff.
     */
    private fun computeAgeScore(myAge: Int, cardAge: Int): Int {
        val ageDiff = abs(myAge - cardAge)
        return max(0, WEIGHT_AGE - (7 * ageDiff / 4))
    }

    /**
     * Location: same city = full WEIGHT_LOCATION; same country only = half; else 0.
     */
    private fun computeLocationScore(
        myCity: String,
        myCountry: String,
        cardCity: String,
        cardCountry: String
    ): Int {
        return when {
            myCity.equals(cardCity, ignoreCase = true) -> WEIGHT_LOCATION
            myCountry.equals(cardCountry, ignoreCase = true) -> WEIGHT_LOCATION / 2
            else -> 0
        }
    }

    /**
     * Religion: exact match (case-insensitive) = full WEIGHT_RELIGION, else 0.
     */
    private fun computeReligionScore(myReligion: String, cardReligion: String): Int {
        return if (myReligion.equals(cardReligion, ignoreCase = true)) WEIGHT_RELIGION else 0
    }

    /**
     * Occupation: exact match (case-insensitive) = full WEIGHT_OCCUPATION, else 0.
     */
    private fun computeOccupationScore(myOccupation: String, cardOccupation: String): Int {
        return if (myOccupation.equals(cardOccupation, ignoreCase = true)) WEIGHT_OCCUPATION else 0
    }
}
