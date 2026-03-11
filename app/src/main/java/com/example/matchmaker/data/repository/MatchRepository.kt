package com.example.matchmaker.data.repository

import com.example.matchmaker.data.api.RandomUserApi
import com.example.matchmaker.data.api.UserMapper
import com.example.matchmaker.data.db.MatchProfileDao
import com.example.matchmaker.data.db.MatchProfileEntity
import com.example.matchmaker.data.db.ProfileStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

/**
 * Single source of truth for match profiles. Offline-first: always read from Room;
 * on refresh, fetch from API and merge into DB.
 */
class MatchRepository(
    private val dao: MatchProfileDao,
    private val api: RandomUserApi
) {

    fun getAll(): Flow<List<MatchProfileEntity>> = dao.getAll()

    suspend fun refresh() {
        val maxAttempts = 3
        val backoffMs = longArrayOf(500L, 1000L, 2000L)
        var lastException: Exception? = null

        repeat(maxAttempts) { attempt ->
            try {
                simulateFlakyNetwork()
                val response = api.getUsers(results = 10)
                val users = response.results ?: return
                val profiles = UserMapper.toMatchProfiles(users)
                val entities = profiles.map { it.toEntity(status = ProfileStatus.PENDING) }
                if (entities.isNotEmpty()) {
                    dao.insertAll(entities)
                }
                return
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxAttempts - 1) {
                    delay(backoffMs[attempt])
                }
            }
        }
        lastException?.let { throw it }
    }

    /**
     * Simulates flaky network: 30% of calls fail with a simulated failure.
     */
    private fun simulateFlakyNetwork() {
        if (Random.nextFloat() < 0.3f) {
            throw SimulatedNetworkException("Simulated network failure (30% chance)")
        }
    }

    suspend fun updateStatus(profileId: String, status: ProfileStatus) {
        dao.updateStatus(profileId, status)
    }
}

private class SimulatedNetworkException(message: String) : Exception(message)
