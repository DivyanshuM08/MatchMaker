package com.example.matchmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_profile")
data class MatchProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val age: Int,
    val city: String,
    val country: String,
    val imageUrl: String,
    val education: String,
    val religion: String,
    val occupation: String,
    val status: ProfileStatus
)
