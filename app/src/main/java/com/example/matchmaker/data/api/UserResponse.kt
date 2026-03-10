package com.example.matchmaker.data.api

data class UserResponse(
    val results: List<User>? = null,
    val info: Info? = null
)

data class Info(
    val seed: String? = null,
    val results: Int? = null,
    val page: Int? = null,
    val version: String? = null
)
