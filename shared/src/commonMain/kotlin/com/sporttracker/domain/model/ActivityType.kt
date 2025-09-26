package com.sporttracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ActivityType(
    val czechName: String,
    val englishName: String,
    val defaultDurationMinutes: Int = 30
) {
    RUNNING(
        czechName = "Běh",
        englishName = "Running",
        defaultDurationMinutes = 30
    ),
    CYCLING(
        czechName = "Cyklistika",
        englishName = "Cycling",
        defaultDurationMinutes = 45
    ),
    WALKING(
        czechName = "Chůze",
        englishName = "Walking",
        defaultDurationMinutes = 60
    ),
    SWIMMING(
        czechName = "Plavání",
        englishName = "Swimming",
        defaultDurationMinutes = 30
    ),
    GYM(
        czechName = "Posilovna",
        englishName = "Gym",
        defaultDurationMinutes = 60
    ),
    HIKING(
        czechName = "Turistika",
        englishName = "Hiking",
        defaultDurationMinutes = 120
    ),
    YOGA(
        czechName = "Jóga",
        englishName = "Yoga",
        defaultDurationMinutes = 45
    ),
    OTHER(
        czechName = "Jiné",
        englishName = "Other",
        defaultDurationMinutes = 30
    )
}