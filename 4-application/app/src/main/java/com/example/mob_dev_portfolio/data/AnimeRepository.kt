package com.example.mob_dev_portfolio.data

import com.example.mob_dev_portfolio.model.Anime
import com.example.mob_dev_portfolio.model.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository class that abstracts access to multiple data sources.
 * It manages the logic for fetching data from the API and storing favorites locally.
 */
class AnimeRepository(
    private val apiService: JikanApiService,
    private val animeDao: AnimeDao,
    private val preferenceManager: PreferenceManager
) {

    // Flow of favorites from the local database
    val favorites: Flow<List<Anime>> = animeDao.getAllFavorites().map { entities ->
        entities.map { it.toDomainModel() }
    }

    // Flow of last updated time from DataStore
    val lastUpdated: Flow<Long> = preferenceManager.lastUpdated

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
     * Toggles the favorite status of an anime.
     */
    suspend fun toggleFavorite(anime: Anime) {
        if (animeDao.isFavorite(anime.id)) {
            animeDao.deleteFavorite(anime.toEntity())
        } else {
            animeDao.insertFavorite(anime.toEntity())
        }
    }
}
