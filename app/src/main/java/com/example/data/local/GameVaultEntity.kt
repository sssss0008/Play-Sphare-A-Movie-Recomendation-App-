package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_games")
data class GameVaultEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val thumbnail: String,
    val shortDescription: String,
    val genre: String,
    val platform: String,
    val publisher: String,
    val developer: String,
    val releaseDate: String,
    val gameUrl: String,
    val status: String = "Want to Play", // "Want to Play", "Playing", "Completed", "Favorited"
    val rating: Int = 0, // 0 to 5 stars
    val personalNotes: String = "",
    val addedAt: Long = System.currentTimeMillis()
)
