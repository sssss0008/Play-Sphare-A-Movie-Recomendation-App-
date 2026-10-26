package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "thumbnail") val thumbnail: String,
    @Json(name = "short_description") val shortDescription: String?,
    @Json(name = "game_url") val gameUrl: String?,
    @Json(name = "genre") val genre: String?,
    @Json(name = "platform") val platform: String?,
    @Json(name = "publisher") val publisher: String?,
    @Json(name = "developer") val developer: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "freetogame_profile_url") val profileUrl: String?
)

@JsonClass(generateAdapter = true)
data class GameDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "thumbnail") val thumbnail: String,
    @Json(name = "status") val status: String?,
    @Json(name = "short_description") val shortDescription: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "game_url") val gameUrl: String?,
    @Json(name = "genre") val genre: String?,
    @Json(name = "platform") val platform: String?,
    @Json(name = "publisher") val publisher: String?,
    @Json(name = "developer") val developer: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "freetogame_profile_url") val profileUrl: String?,
    @Json(name = "minimum_system_requirements") val minimumSystemRequirements: MinimumSystemRequirements?,
    @Json(name = "screenshots") val screenshots: List<ScreenshotDto>?
)

@JsonClass(generateAdapter = true)
data class MinimumSystemRequirements(
    @Json(name = "os") val os: String?,
    @Json(name = "processor") val processor: String?,
    @Json(name = "memory") val memory: String?,
    @Json(name = "graphics") val graphics: String?,
    @Json(name = "storage") val storage: String?
)

@JsonClass(generateAdapter = true)
data class ScreenshotDto(
    @Json(name = "id") val id: Int,
    @Json(name = "image") val image: String
)
