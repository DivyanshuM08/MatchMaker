package com.example.matchmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchProfileDao {

    @Query("SELECT * FROM match_profile ORDER BY id")
    fun getAll(): Flow<List<MatchProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<MatchProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: MatchProfileEntity)

    @Query("UPDATE match_profile SET status = :status WHERE id = :profileId")
    suspend fun updateStatus(profileId: String, status: ProfileStatus)
}
