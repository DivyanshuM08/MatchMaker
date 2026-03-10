package com.example.matchmaker.domain

/**
 * Holder for the current user's profile, used as input to [MatchScoreCalculator].
 * Fixed defaults; can be replaced later with a real "my profile" screen or prefs.
 */
object MyProfile {
    const val age: Int = 30
    const val city: String = "Sydney"
}
