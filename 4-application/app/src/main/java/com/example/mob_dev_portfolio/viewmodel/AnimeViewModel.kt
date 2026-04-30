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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel for WatchRyu.
 * Manages anime data and accessibility settings.
 */
class AnimeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AnimeRepository
    private val preferenceManager: PreferenceManager
    
    val animeList = MutableLiveData<List<Anime>>()
    val favorites: LiveData<List<Anime>>
    val lastUpdated: LiveData<Long>
    
    val themeSelection: LiveData<Int>
    val contrastSetting: LiveData<Int>
    val fontSizeSetting: LiveData<Int>

    /**
     * Helper to get the synchronous state of a preference.
     * This is useful for theme application in MainActivity's onCreate.
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

        fetchSeasonalAnime()
    }

    fun getAnimeByStatus(status: AnimeStatus): LiveData<List<Anime>> = 
        repository.getAnimeByStatus(status).asLiveData()

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
        viewModelScope.launch {
            try {
                // Clear current list to show loading
                animeList.postValue(emptyList())
                val list = repository.fetchTopAnime()
                animeList.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchSeasonalAnime() {
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = repository.fetchSeasonalAnime()
                animeList.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUpcomingAnime() {
        viewModelScope.launch {
            try {
                animeList.postValue(emptyList())
                val list = repository.fetchUpcomingAnime()
                animeList.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAnimeInList(anime: Anime) {
        viewModelScope.launch { repository.updateAnimeInList(anime) }
    }

    fun deleteAnimeFromList(anime: Anime) {
        viewModelScope.launch { repository.deleteAnimeFromList(anime) }
    }
}
