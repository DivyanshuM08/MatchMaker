package com.example.matchmaker

import android.app.Application
import androidx.room.Room
import com.example.matchmaker.data.db.AppDatabase
import com.example.matchmaker.data.repository.MatchRepository

class MatchMateApplication : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "matchmate.db").build()
    }

    val matchRepository: MatchRepository by lazy {
        MatchRepository(dao = database.matchProfileDao())
    }
}
