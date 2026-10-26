package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameVaultDao {

    @Query("SELECT * FROM vault_games ORDER BY addedAt DESC")
    fun getAllVaultGames(): Flow<List<GameVaultEntity>>

    @Query("SELECT * FROM vault_games WHERE status = :status ORDER BY addedAt DESC")
    fun getVaultGamesByStatus(status: String): Flow<List<GameVaultEntity>>

    @Query("SELECT * FROM vault_games WHERE id = :id LIMIT 1")
    fun getVaultGameById(id: Int): Flow<GameVaultEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM vault_games WHERE id = :id)")
    fun isGameInVault(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(game: GameVaultEntity)

    @Update
    suspend fun update(game: GameVaultEntity)

    @Delete
    suspend fun delete(game: GameVaultEntity)

    @Query("DELETE FROM vault_games WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM vault_games")
    fun getVaultCount(): Flow<Int>
}
