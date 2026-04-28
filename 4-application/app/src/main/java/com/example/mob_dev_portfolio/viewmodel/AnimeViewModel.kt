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
import kotlinx.coroutines.launch

/**
 * ViewModel for the Anime Tracker.
 * This class survives configuration changes like screen rotations, ensuring the UI state is preserved.
 * Using AndroidViewModel to access Application context for repository initialization.
 */
class AnimeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AnimeRepository
    private val preferenceManager: PreferenceManager
    
    // Internal mutable list for search results/seasonal anime
    private val _animeList = MutableLiveData<List<Anime>>()
    val animeList: LiveData<List<Anime>> get() = _animeList

    // LiveData for favorites, automatically updated from Room Flow
    val favorites: LiveData<List<Anime>>

    // LiveData for last updated time, automatically updated from DataStore Flow
    val lastUpdated: LiveData<Long>
    
    // LiveData for theme selection
    val themeSelection: LiveData<Int>

    /**
     * Get specific anime list based on status.
     * We use asLiveData() on the Flow from the repository.
     */
    fun getAnimeByStatus(status: AnimeStatus): LiveData<List<Anime>> {
        return repository.getAnimeByStatus(status).asLiveData()
    }

    init {
        val database = AppDatabase.getDatabase(application)
        val apiService = JikanApiService.create()
        preferenceManager = PreferenceManager(application)
        
        repository = AnimeRepository(apiService, database.animeDao(), preferenceManager)
        
        favorites = repository.favorites.asLiveData()
        lastUpdated = repository.lastUpdated.asLiveData()
        themeSelection = preferenceManager.themeSelection.asLiveData()

        fetchSeasonalAnime()
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            preferenceManager.saveThemeSelection(theme)
        }
    }

    fun fetchSeasonalAnime() {
        viewModelScope.launch {
            try {
                val list = repository.fetchSeasonalAnime()
                _animeList.postValue(list)
            } catch (e: Exception) {
                // Handle error (e.g., no internet)
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite(anime: Anime) {
        viewModelScope.launch {
            repository.toggleFavorite(anime)
        }
    }
}
