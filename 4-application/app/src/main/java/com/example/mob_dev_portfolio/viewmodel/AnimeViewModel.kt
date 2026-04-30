package com.example.mob_dev_portfolio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.AniListApiService
import com.example.mob_dev_portfolio.data.AnimeRepository
import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.JikanApiService
import com.example.mob_dev_portfolio.data.PreferenceManager
import com.example.mob_dev_portfolio.model.Anime
import com.example.mob_dev_portfolio.model.AnimeStatus
import com.example.mob_dev_portfolio.model.GenreDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel for WatchRyu.
 * Manages anime data, accessibility settings, and discovery filters.
 */
class AnimeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AnimeRepository
    private val preferenceManager: PreferenceManager
    
    val animeList = MutableLiveData<List<Anime>>()
    val favorites: LiveData<List<Anime>>
    val lastUpdated: LiveData<Long>
    
    // Discovery & Filters
    val genres = MutableLiveData<List<GenreDto>>()
    var selectedGenreId: Int? = null
    var currentCategory: String = "seasonal"

    val themeSelection: LiveData<Int>
    val contrastSetting: LiveData<Int>
    val fontSizeSetting: LiveData<Int>

    /**
     * Helper to get the synchronous state of a preference.
     */
    fun getInitialSettings(): Triple<Int, Int, Int> = runBlocking {
        Triple(
            preferenceManager.themeSelection.first(),
            preferenceManager.fontSizeSetting.first(),
            preferenceManager.contrastSetting.first()
        )
    }

    init {
        val database = AppDatabase.getDatabase(application)
        val apiService = JikanApiService.create()
        val aniListService = AniListApiService.create()
        preferenceManager = PreferenceManager(application)
        repository = AnimeRepository(apiService, aniListService, database.animeDao(), preferenceManager)
        
        favorites = repository.allAnime.asLiveData()
        lastUpdated = repository.lastUpdated.asLiveData()
        themeSelection = preferenceManager.themeSelection.asLiveData()
        contrastSetting = preferenceManager.contrastSetting.asLiveData()
        fontSizeSetting = preferenceManager.fontSizeSetting.asLiveData()

        fetchGenres()
        fetchSeasonalAnime()
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                val list = repository.getGenres()
                genres.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAnimeByStatus(status: AnimeStatus): LiveData<List<Anime>> = 
        repository.getAnimeByStatus(status).asLiveData()

    val importStatus = MutableLiveData<String>()

    fun importMalList(username: String) {
        viewModelScope.launch {
            try {
                importStatus.postValue("Importing...")
                repository.importMalList(username)
                importStatus.postValue("Import successful!")
            } catch (e: Exception) {
                e.printStackTrace()
                importStatus.postValue("Import failed: ${e.localizedMessage}")
            }
        }
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch { preferenceManager.saveThemeSelection(theme) }
    }

    fun setContrast(contrast: Int) {
        viewModelScope.launch { preferenceManager.saveContrastSetting(contrast) }
    }

    fun setFontSize(size: Int) {
        viewModelScope.launch { preferenceManager.saveFontSizeSetting(size) }
    }

    fun fetchTopAnime() {
        android.util.Log.d("AnimeViewModel", "fetchTopAnime called")
        currentCategory = "top"
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = if (selectedGenreId == null) {
                    repository.fetchTopAnime()
                } else {
                    repository.searchAnime(orderBy = "score", genres = selectedGenreId.toString())
                }
                android.util.Log.d("AnimeViewModel", "fetchTopAnime success: ${list.size} items")
                if (list.isEmpty()) {
                    importStatus.postValue("No results found or API error.")
                }
                animeList.postValue(list)
            } catch (e: Exception) {
                android.util.Log.e("AnimeViewModel", "fetchTopAnime error", e)
                importStatus.postValue("Error: ${e.localizedMessage}")
                animeList.postValue(emptyList())
            }
        }
    }

    fun fetchSeasonalAnime() {
        android.util.Log.d("AnimeViewModel", "fetchSeasonalAnime called")
        currentCategory = "seasonal"
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = if (selectedGenreId == null) {
                    repository.fetchSeasonalAnime()
                } else {
                    repository.searchAnime(status = "airing", genres = selectedGenreId.toString())
                }
                android.util.Log.d("AnimeViewModel", "fetchSeasonalAnime success: ${list.size} items")
                if (list.isEmpty()) {
                    importStatus.postValue("No results found for current season.")
                }
                animeList.postValue(list)
            } catch (e: Exception) {
                android.util.Log.e("AnimeViewModel", "fetchSeasonalAnime error", e)
                importStatus.postValue("Error: ${e.localizedMessage}")
                animeList.postValue(emptyList())
            }
        }
    }

    fun fetchUpcomingAnime() {
        android.util.Log.d("AnimeViewModel", "fetchUpcomingAnime called")
        currentCategory = "upcoming"
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = if (selectedGenreId == null) {
                    repository.fetchUpcomingAnime()
                } else {
                    repository.searchAnime(status = "upcoming", genres = selectedGenreId.toString())
                }
                android.util.Log.d("AnimeViewModel", "fetchUpcomingAnime success: ${list.size} items")
                if (list.isEmpty()) {
                    importStatus.postValue("No upcoming shows found.")
                }
                animeList.postValue(list)
            } catch (e: Exception) {
                android.util.Log.e("AnimeViewModel", "fetchUpcomingAnime error", e)
                importStatus.postValue("Error: ${e.localizedMessage}")
                animeList.postValue(emptyList())
            }
        }
    }

    fun onGenreSelected(genreId: Int?) {
        selectedGenreId = genreId
        when (currentCategory) {
            "top" -> fetchTopAnime()
            "seasonal" -> fetchSeasonalAnime()
            "upcoming" -> fetchUpcomingAnime()
        }
    }

    fun updateAnimeInList(anime: Anime) {
        viewModelScope.launch { repository.updateAnimeInList(anime) }
    }

    fun deleteAnimeFromList(anime: Anime) {
        viewModelScope.launch { repository.deleteAnimeFromList(anime) }
    }

    fun clearDatabase() {
        viewModelScope.launch { repository.clearDatabase() }
    }

    fun performSearch(query: String) {
        currentCategory = "search"
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = repository.searchAnime(query = query)
                if (list.isEmpty()) {
                    importStatus.postValue("No results found for '$query'")
                }
                animeList.postValue(list)
            } catch (e: Exception) {
                val errorMsg = if (e.localizedMessage?.contains("500") == true) {
                    "API Server Error (500). MAL might be down or busy. Try again in a moment."
                } else {
                    "Search failed: ${e.localizedMessage}"
                }
                importStatus.postValue(errorMsg)
            }
        }
    }

    /**
     * Refreshes metadata (like images) for all anime in the local list.
     */
    fun refreshAllMetadata() {
        viewModelScope.launch {
            importStatus.postValue("Refreshing metadata...")
            val currentList = repository.allAnime.first()
            var count = 0
            for (anime in currentList) {
                // If it's missing image or synopsis, refresh it
                if (anime.imageUrl.isEmpty() || anime.synopsis.isEmpty()) {
                    val fresh = repository.refreshAnimeMetadata(anime.id)
                    if (fresh != null) {
                        // Keep user's personal tracking data
                        val updated = fresh.copy(
                            episodesWatched = anime.episodesWatched,
                            score = anime.score,
                            status = anime.status,
                            personalReview = anime.personalReview,
                            rewatchCount = anime.rewatchCount
                        )
                        repository.updateAnimeInList(updated)
                        count++
                    }
                    // Delay to avoid hitting rate limits (increase to 1s to be safe)
                    kotlinx.coroutines.delay(1000)
                }
            }
            importStatus.postValue("Refreshed $count shows!")
        }
    }
}
