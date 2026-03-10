package com.example.matchmaker.ui.list

import com.example.matchmaker.data.db.MatchProfileEntity

data class MatchCardItem(
    val entity: MatchProfileEntity,
    val matchScore: Int
)
