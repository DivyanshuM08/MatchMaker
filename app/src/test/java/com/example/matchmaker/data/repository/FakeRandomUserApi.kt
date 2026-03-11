package com.example.matchmaker.data.repository

import com.example.matchmaker.data.api.UserResponse

class FakeRandomUserApi(
    private val response: UserResponse = UserResponse(results = emptyList())
) : com.example.matchmaker.data.api.RandomUserApi {

    override suspend fun getUsers(results: Int): UserResponse = response
}
