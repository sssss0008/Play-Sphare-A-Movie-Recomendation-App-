package com.example.data.remote

import com.example.data.model.GameDetailDto
import com.example.data.model.GameDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FreeToGameApi {

    @GET("games")
    suspend fun getGames(
        @Query("platform") platform: String? = null,
        @Query("category") category: String? = null,
        @Query("sort-by") sortBy: String? = null
    ): List<GameDto>

    @GET("game")
    suspend fun getGameDetail(
        @Query("id") id: Int
    ): GameDetailDto
}
