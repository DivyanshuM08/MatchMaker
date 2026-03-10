package com.example.matchmaker.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {

    @GET("api/")
    suspend fun getUsers(
        @Query("results") results: Int
    ): UserResponse
}
