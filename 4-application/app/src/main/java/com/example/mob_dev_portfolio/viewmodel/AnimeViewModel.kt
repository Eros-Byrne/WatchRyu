package com.example.mob_dev_portfolio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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
        preferenceManager = PreferenceManager(application)
        repository = AnimeRepository(apiService, database.animeDao(), preferenceManager)
        
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
        currentCategory = "top"
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = if (selectedGenreId == null) {
                    repository.fetchTopAnime()
                } else {
                    repository.searchAnime(orderBy = "score", genres = selectedGenreId.toString())
                }
                animeList.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchSeasonalAnime() {
        currentCategory = "seasonal"
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = if (selectedGenreId == null) {
                    repository.fetchSeasonalAnime()
                } else {
                    repository.searchAnime(status = "airing", genres = selectedGenreId.toString())
                }
                animeList.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUpcomingAnime() {
        currentCategory = "upcoming"
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = if (selectedGenreId == null) {
                    repository.fetchUpcomingAnime()
                } else {
                    repository.searchAnime(status = "upcoming", genres = selectedGenreId.toString())
                }
                animeList.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
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
}
