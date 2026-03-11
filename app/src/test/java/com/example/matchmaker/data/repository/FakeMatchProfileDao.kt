package com.example.matchmaker.data.repository

import com.example.matchmaker.data.db.MatchProfileDao
import com.example.matchmaker.data.db.MatchProfileEntity
import com.example.matchmaker.data.db.ProfileStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeMatchProfileDao : MatchProfileDao {

    private val _profiles = MutableStateFlow<List<MatchProfileEntity>>(emptyList())
    override fun getAll(): Flow<List<MatchProfileEntity>> = _profiles.asStateFlow()

    override suspend fun insertAll(profiles: List<MatchProfileEntity>) {
        _profiles.value = profiles
    }

    override suspend fun insert(profile: MatchProfileEntity) {
        _profiles.value = _profiles.value + profile
    }

    override suspend fun updateStatus(profileId: String, status: ProfileStatus) {
        _profiles.value = _profiles.value.map {
            if (it.id == profileId) it.copy(status = status) else it
        }
    }
}
