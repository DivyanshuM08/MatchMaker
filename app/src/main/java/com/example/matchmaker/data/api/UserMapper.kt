package com.example.matchmaker.data.api

/**
 * Maps RandomUser API [User] DTOs to app [MatchProfile] models. Used when we fetch
 * from the API (e.g. in [MatchRepository.refresh]) so we can add the extra matrimonial
 * fields (education, religion, occupation) via mock data before persisting to Room.
 */
object UserMapper {

    private val educationOptions = listOf(
        "High School",
        "Bachelor's",
        "Master's",
        "PhD",
        "Diploma"
    )

    private val religionOptions = listOf(
        "Hindu",
        "Muslim",
        "Christian",
        "Sikh",
        "Other"
    )

    private val occupationOptions = listOf(
        "Engineer",
        "Doctor",
        "Teacher",
        "Business",
        "Software Developer",
        "Other"
    )

    fun toMatchProfile(user: User): MatchProfile {
        val name = "${user.name?.title}. ${user.name?.first} ${user.name?.last}".trim()
        val stableSeed = user.login?.uuid.hashCode().and(0x7FFF_FFFF)
        return MatchProfile(
            id = user.login?.uuid,
            name = name,
            age = user.dob?.age,
            city = user.location?.city,
            country = user.location?.country,
            imageUrl = user.picture?.medium,
            education = educationOptions[stableSeed % educationOptions.size],
            religion = religionOptions[stableSeed % religionOptions.size],
            occupation = occupationOptions[(stableSeed / 100) % occupationOptions.size]
        )
    }

    fun toMatchProfiles(users: List<User>): List<MatchProfile> = users.map(::toMatchProfile)
}
