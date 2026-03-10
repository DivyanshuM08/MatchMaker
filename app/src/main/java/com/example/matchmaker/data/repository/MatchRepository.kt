package com.example.matchmaker.data.repository

import com.example.matchmaker.data.api.ApiProvider
import com.example.matchmaker.data.api.RandomUserApi
import com.example.matchmaker.data.api.UserMapper
import com.example.matchmaker.data.db.MatchProfileDao
import com.example.matchmaker.data.db.MatchProfileEntity
import com.example.matchmaker.data.db.ProfileStatus
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for match profiles. Offline-first: UI always reads from Room
 * via [getAll]; [refresh] fetches from the API and merges into the DB. Used by the
 * match list screen and ViewModel to load/show profiles and to update accept/decline status.
 */
class MatchRepository(
    private val dao: MatchProfileDao,
    private val api: RandomUserApi = ApiProvider.randomUserApi
) {

    fun getAll(): Flow<List<MatchProfileEntity>> = dao.getAll()

    suspend fun refresh() {
        val response = api.getUsers(results = 10)
        val users = response.results ?: return
        val profiles = UserMapper.toMatchProfiles(users)
        val entities = profiles.map { it.toEntity(status = ProfileStatus.PENDING) }
        dao.insertAll(entities)
    }

    suspend fun updateStatus(profileId: String, status: ProfileStatus) {
        dao.updateStatus(profileId, status)
    }
}
