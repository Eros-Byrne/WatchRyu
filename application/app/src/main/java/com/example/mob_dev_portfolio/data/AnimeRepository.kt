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
    private val aniListService: AniListApiService,
    private val animeDao: AnimeDao,
    private val preferenceManager: PreferenceManager
) {

    private val aniListQuery = """
        query (${'$'}search: String, ${'$'}season: MediaSeason, ${'$'}seasonYear: Int, ${'$'}sort: [MediaSort]) {
          Page(page: 1, perPage: 25) {
            media(search: ${'$'}search, season: ${'$'}season, seasonYear: ${'$'}seasonYear, sort: ${'$'}sort, type: ANIME) {
              id
              title { english romaji }
              description
              episodes
              averageScore
              coverImage { large }
              siteUrl
            }
          }
        }
    """.trimIndent()

    /**
     * Advanced discovery search.
     */
    suspend fun searchAnime(
        query: String? = null,
        status: String? = null,
        genres: String? = null,
        orderBy: String? = null,
        sort: String? = null
    ): List<Anime> {
        return try {
            val variables = mutableMapOf<String, Any>()
            if (!query.isNullOrEmpty()) variables["search"] = query
            variables["sort"] = listOf("POPULARITY_DESC")
            
            val request = AniListRequest(aniListQuery, variables)
            val response = aniListService.getAnime(request)
            response.data.page.media.map { it.toDomainModel() }
        } catch (e: Exception) {
            // Fallback to Jikan if AniList fails
            val response = apiService.searchAnime(query, status, genres, orderBy, sort)
            response.data?.map { it.toDomainModel() } ?: emptyList()
        }
    }

    // Flow of all anime from the local database
    val allAnime: Flow<List<Anime>> = animeDao.getAllAnime().map { entities ->
        entities.map { it.toDomainModel() }
    }

    /**
     * Flow of the last updated timestamp from DataStore.
     */
    val lastUpdated: Flow<Long> = preferenceManager.lastUpdated
    // ... rest of existing methods ...


    /**
     * Fetches the official MAL genre list.
     */
    suspend fun getGenres(): List<com.example.mob_dev_portfolio.model.GenreDto> {
        return try {
            val response = apiService.getGenres()
            response.data ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Imports a user's MAL list using the Jikan API.
     * We wrapped this in a specific error handler to explain 404s.
     * Jikan often returns 404 if MAL blocks the scraper, even for public lists.
     */
    suspend fun importMalList(username: String) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserAnimeList(username, status = "all")
            val domainList = response.data?.map { it.toDomainModel() } ?: emptyList()
            
            for (anime in domainList) {
                animeDao.insertAnime(anime.toEntity())
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                throw Exception("API Error: MAL blocked the import request. Please use the XML export method below instead.")
            }
            throw e
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
     * Fetches top rated anime.
     */
    suspend fun fetchTopAnime(): List<Anime> {
        return try {
            val variables = mapOf("sort" to listOf("SCORE_DESC"))
            val request = AniListRequest(aniListQuery, variables)
            val response = aniListService.getAnime(request)
            response.data.page.media.map { it.toDomainModel() }
        } catch (e: Exception) {
            val response = apiService.getTopAnime()
            return response.data?.map { it.toDomainModel() } ?: emptyList()
        }
    }

    /**
     * Fetches seasonal anime.
     */
    suspend fun fetchSeasonalAnime(): List<Anime> {
        return try {
            val variables = mapOf(
                "season" to "WINTER", // Hardcoded for current season demo
                "seasonYear" to 2025,
                "sort" to listOf("POPULARITY_DESC")
            )
            val request = AniListRequest(aniListQuery, variables)
            val response = aniListService.getAnime(request)
            val animeList = response.data.page.media.map { it.toDomainModel() }
            preferenceManager.saveLastUpdated(System.currentTimeMillis())
            animeList
        } catch (e: Exception) {
            val response = apiService.getSeasonalAnime()
            val animeList = response.data?.map { it.toDomainModel() } ?: emptyList()
            preferenceManager.saveLastUpdated(System.currentTimeMillis())
            return animeList
        }
    }

    /**
     * Fetches upcoming anime.
     */
    suspend fun fetchUpcomingAnime(): List<Anime> {
        return try {
            val variables = mapOf(
                "status" to "NOT_YET_RELEASED",
                "sort" to listOf("POPULARITY_DESC")
            )
            val request = AniListRequest(aniListQuery, variables)
            val response = aniListService.getAnime(request)
            response.data.page.media.map { it.toDomainModel() }
        } catch (e: Exception) {
            val response = apiService.getUpcomingAnime()
            return response.data?.map { it.toDomainModel() } ?: emptyList()
        }
    }

    /**
     * Completely wipes the local database.
     */
    suspend fun clearDatabase() {
        animeDao.clearAllAnime()
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

    /**
     * Fetches fresh metadata for an anime by ID.
     */
    suspend fun refreshAnimeMetadata(id: Int): Anime? {
        return try {
            val response = apiService.getAnimeFullById(id)
            response.data?.toDomainModel()
        } catch (e: Exception) {
            null
        }
    }
}
