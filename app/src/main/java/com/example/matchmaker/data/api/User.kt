package com.example.matchmaker.data.api

data class User(
    val gender: String? = null,
    val name: Name? = null,
    val location: Location? = null,
    val email: String? = null,
    val login: Login? = null,
    val dob: Dob? = null,
    val registered: Registered? = null,
    val phone: String? = null,
    val cell: String? = null,
    val id: Id? = null,
    val picture: Picture? = null,
    val nat: String? = null
)

data class Name(
    val title: String? = null,
    val first: String? = null,
    val last: String? = null
)

data class Login(
    val uuid: String? = null,
    val username: String? = null,
    val password: String? = null,
    val salt: String? = null,
    val md5: String? = null,
    val sha1: String? = null,
    val sha256: String? = null
)

data class Dob(
    val date: String? = null,
    val age: Int? = null
)

data class Registered(
    val date: String? = null,
    val age: Int? = null
)

data class Id(
    val name: String? = null,
    val value: String? = null,
)

data class Picture(
    val large: String? = null,
    val medium: String? = null,
    val thumbnail: String? = null,
)
