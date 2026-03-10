package com.example.matchmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MatchProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchProfileDao(): MatchProfileDao
}
