package com.example.mob_dev_portfolio.model

/**
 * Data class representing an Anime entry.
 * Using a simple structure for now to demonstrate state management.
 */
data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val synopsis: String = "",
    val isFavorite: Boolean = false,
    val wikiUrl: String = "https://en.wikipedia.org/wiki/Anime",
    var isExpanded: Boolean = false // Track if the panel is open or closed
)
