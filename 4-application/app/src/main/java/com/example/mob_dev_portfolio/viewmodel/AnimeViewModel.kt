package com.example.mob_dev_portfolio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.model.Anime
import kotlinx.coroutines.launch

/**
 * ViewModel for the Anime Tracker.
 * This class survives configuration changes like screen rotations, ensuring the UI state is preserved.
 * Following the MVVM pattern as recommended by MAD guidelines.
 */
class AnimeViewModel : ViewModel() {

    // Internal mutable list to handle updates
    private val _animeList = MutableLiveData<List<Anime>>()
    
    // External immutable LiveData for the UI to observe
    val animeList: LiveData<List<Anime>> get() = _animeList

    init {
        // Initial data fetch simulation
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Simulated data for initial testing on Pixel 3a API 35
            // In a real scenario, this would come from Retrofit or Room
            val mockData = listOf(
                Anime(1, "Naruto", "https://cdn.myanimelist.net/images/anime/13/17405.jpg", "A young ninja who seeks recognition..."),
                Anime(2, "One Piece", "https://cdn.myanimelist.net/images/anime/1244/138851.jpg", "Monkey D. Luffy refuses to let anyone or anything stand in the way of his quest..."),
                Anime(3, "Bleach", "https://cdn.myanimelist.net/images/anime/1764/126627.jpg", "Ichigo Kurosaki is an ordinary high schooler—until his family is attacked by a Hollow...")
            )
            _animeList.value = mockData
        }
    }
}
