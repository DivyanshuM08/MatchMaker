package com.example.matchmaker.data.api

data class Location(
    val street: Street? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val postcode: String? = null,
    val coordinates: Coordinates? = null,
    val timezone: Timezone? = null
)

data class Street(
    val number: Int? = null,
    val name: String? = null
)

data class Coordinates(
    val latitude: String? = null,
    val longitude: String? = null
)

data class Timezone(
    val offset: String? = null,
    val description: String? = null
)
