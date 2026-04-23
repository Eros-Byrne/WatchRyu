package com.example.mob_dev_portfolio.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for local Anime storage.
 * Handles the logic for saving and retrieving favorite anime.
 */
@Dao
interface AnimeDao {
    @Query("SELECT * FROM favorite_anime")
    fun getAllFavorites(): Flow<List<AnimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(anime: AnimeEntity)

    @Delete
    suspend fun deleteFavorite(anime: AnimeEntity)

    @Query("SELECT EXISTS(SELECT * FROM favorite_anime WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}
