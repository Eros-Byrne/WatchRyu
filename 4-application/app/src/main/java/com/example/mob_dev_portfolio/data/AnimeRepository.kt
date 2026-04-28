package com.example.mob_dev_portfolio.data

import com.example.mob_dev_portfolio.model.Anime
import com.example.mob_dev_portfolio.model.AnimeStatus
import com.example.mob_dev_portfolio.model.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository class that abstracts access to Jikan API and Room DB.
 */
class AnimeRepository(
    private val apiService: JikanApiService,
    private val animeDao: AnimeDao,
    private val preferenceManager: PreferenceManager
) {

    // Flow of all anime from the local database
    val allAnime: Flow<List<Anime>> = animeDao.getAllAnime().map { entities ->
        entities.map { it.toDomainModel() }
    }

    // Flow of last updated time from DataStore
    val lastUpdated: Flow<Long> = preferenceManager.lastUpdated

    /**
     * Get anime filtered by their tracking status (Watching, Completed, etc.)
     */
    fun getAnimeByStatus(status: AnimeStatus): Flow<List<Anime>> {
        return animeDao.getAnimeByStatus(status).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Fetches seasonal anime from the Jikan API.
     */
    suspend fun fetchSeasonalAnime(): List<Anime> {
        val response = apiService.getSeasonalAnime()
        val animeList = response.data.map { it.toDomainModel() }
        
        // Update last updated time in DataStore
        preferenceManager.saveLastUpdated(System.currentTimeMillis())
        
        return animeList
    }

    /**
     * Add or update an anime in the user's tracking list.
     */
    suspend fun updateAnimeInList(anime: Anime) {
        animeDao.insertAnime(anime.toEntity())
    }

    /**
     * Remove an anime from the list.
     */
    suspend fun deleteAnimeFromList(anime: Anime) {
        animeDao.deleteAnime(anime.toEntity())
    }
}
