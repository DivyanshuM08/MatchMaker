package com.example.matchmaker.data.repository

import com.example.matchmaker.data.api.MatchProfile
import com.example.matchmaker.data.db.MatchProfileEntity
import com.example.matchmaker.data.db.ProfileStatus

/**
 * Maps API [MatchProfile] to Room [MatchProfileEntity]. Used in [MatchRepository.refresh]
 * to turn API models into entities (with default [ProfileStatus.PENDING]) before saving
 * to the local database.
 */
fun MatchProfile.toEntity(status: ProfileStatus = ProfileStatus.PENDING): MatchProfileEntity =
    MatchProfileEntity(
        id = id.orEmpty(),
        name = name.orEmpty(),
        age = age ?: 0,
        city = city.orEmpty(),
        country = country.orEmpty(),
        imageUrl = imageUrl.orEmpty(),
        education = education.orEmpty(),
        religion = religion.orEmpty(),
        occupation = occupation.orEmpty(),
        status = status
    )
