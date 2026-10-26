package com.example.data.repository

import com.example.data.local.GameVaultDao
import com.example.data.local.GameVaultEntity
import com.example.data.model.GameDetailDto
import com.example.data.model.GameDto
import com.example.data.remote.FreeToGameApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(
    private val api: FreeToGameApi,
    private val vaultDao: GameVaultDao
) {
    // In-memory cache for fast tab switching and instant offline/search responsiveness
    private var cachedAllGames: List<GameDto> = emptyList()

    suspend fun fetchGames(
        platform: String? = null,
        category: String? = null,
        sortBy: String? = null,
        forceRefresh: Boolean = false
    ): Result<List<GameDto>> = withContext(Dispatchers.IO) {
        try {
            // Normalize platform: "pc" for windows, "browser" for browser, null for all
            val platformParam = when (platform?.lowercase()) {
                "pc", "windows" -> "pc"
                "browser", "web" -> "browser"
                else -> null
            }

            val categoryParam = if (category.isNullOrBlank() || category.equals("all", ignoreCase = true)) {
                null
            } else {
                category.lowercase().replace(" ", "-")
            }

            val sortParam = when (sortBy?.lowercase()) {
                "release date", "release-date" -> "release-date"
                "alphabetical" -> "alphabetical"
                "relevance" -> "relevance"
                "popularity" -> "popularity"
                else -> null
            }

            val games = api.getGames(
                platform = platformParam,
                category = categoryParam,
                sortBy = sortParam
            )
            if (platformParam == null && categoryParam == null && sortParam == null) {
                cachedAllGames = games
            }
            Result.success(games)
        } catch (e: Exception) {
            // Fallback to cache if available
            if (cachedAllGames.isNotEmpty() && platform == null && category == null) {
                Result.success(cachedAllGames)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchGameDetail(id: Int): Result<GameDetailDto> = withContext(Dispatchers.IO) {
        try {
            val detail = api.getGameDetail(id)
            Result.success(detail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllVaultGames(): Flow<List<GameVaultEntity>> = vaultDao.getAllVaultGames()

    fun getVaultGamesByStatus(status: String): Flow<List<GameVaultEntity>> =
        vaultDao.getVaultGamesByStatus(status)

    fun getVaultGameById(id: Int): Flow<GameVaultEntity?> = vaultDao.getVaultGameById(id)

    fun isGameInVault(id: Int): Flow<Boolean> = vaultDao.isGameInVault(id)

    suspend fun saveToVault(game: GameVaultEntity) = withContext(Dispatchers.IO) {
        vaultDao.insertOrUpdate(game)
    }

    suspend fun removeFromVault(id: Int) = withContext(Dispatchers.IO) {
        vaultDao.deleteById(id)
    }

    suspend fun updateVaultItem(id: Int, status: String, rating: Int, notes: String) = withContext(Dispatchers.IO) {
        val existing = vaultDao.getVaultGameById(id)
        // Quick update if present
        // Since we have insertOrUpdate, we can fetch or let the UI pass entity
    }
}
