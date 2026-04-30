package com.example.mob_dev_portfolio.data

import com.example.mob_dev_portfolio.model.Anime
import com.example.mob_dev_portfolio.model.AnimeStatus
import com.example.mob_dev_portfolio.model.GenreDto
import com.example.mob_dev_portfolio.model.toDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository class that abstracts access to Jikan API and Room DB.
 */
class AnimeRepository(
    private val apiService: JikanApiService,
    private val animeDao: AnimeDao,
    private val preferenceManager: PreferenceManager
) {

    /**
     * Fetches the official MAL genre list.
     */
    suspend fun getGenres(): List<GenreDto> {
        val response = apiService.getGenres()
        return response.data
    }

    /**
     * Advanced discovery search.
     * Uses the general search endpoint to allow for genre and status filtering.
     */
    suspend fun searchAnime(
        query: String? = null,
        status: String? = null,
        genres: String? = null,
        orderBy: String? = null,
        sort: String? = "desc"
    ): List<Anime> {
        val response = apiService.searchAnime(query, status, genres, orderBy, sort)
        return response.data.map { it.toDomainModel() }
    }

    // Flow of all anime from the local database
    val allAnime: Flow<List<Anime>> = animeDao.getAllAnime().map { entities ->
        entities.map { it.toDomainModel() }
    }
    // ... rest of existing methods ...


    // Flow of last updated time from DataStore
    val lastUpdated: Flow<Long> = preferenceManager.lastUpdated

    /**
     * Imports a user's MAL list using the Jikan API.
     */
    suspend fun importMalList(username: String) = withContext(Dispatchers.IO) {
        val response = apiService.getUserAnimeList(username)
        val domainList = response.data.map { it.toDomainModel() }
        
        for (anime in domainList) {
            animeDao.insertAnime(anime.toEntity())
        }
    }

    /**
     * Get anime filtered by their tracking status.
     */
    fun getAnimeByStatus(status: AnimeStatus): Flow<List<Anime>> {
        return animeDao.getAnimeByStatus(status).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Fetches top rated anime from the Jikan API.
     */
    suspend fun fetchTopAnime(): List<Anime> {
        val response = apiService.getTopAnime()
        return response.data.map { it.toDomainModel() }
    }

    /**
     * Fetches seasonal anime from the Jikan API.
     */
    suspend fun fetchSeasonalAnime(): List<Anime> {
        val response = apiService.getSeasonalAnime()
        val animeList = response.data.map { it.toDomainModel() }
        preferenceManager.saveLastUpdated(System.currentTimeMillis())
        return animeList
    }

    /**
     * Fetches upcoming anime from the Jikan API.
     */
    suspend fun fetchUpcomingAnime(): List<Anime> {
        val response = apiService.getUpcomingAnime()
        return response.data.map { it.toDomainModel() }
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
