package com.example.mob_dev_portfolio.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mob_dev_portfolio.model.AnimeStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for local Anime storage.
 */
@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime_list")
    fun getAllAnime(): Flow<List<AnimeEntity>>

    @Query("SELECT * FROM anime_list WHERE status = :status")
    fun getAnimeByStatus(status: AnimeStatus): Flow<List<AnimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: AnimeEntity)

    @Delete
    suspend fun deleteAnime(anime: AnimeEntity)

    @Query("SELECT EXISTS(SELECT * FROM anime_list WHERE id = :id)")
    suspend fun isAnimeInList(id: Int): Boolean
}
