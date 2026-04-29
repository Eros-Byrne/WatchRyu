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
    val episodes: Int = 0,
    val episodesWatched: Int = 0,
    val score: Int = 0, // Personal Score
    val malScore: Double = 0.0, // Community Score from MAL
    val status: AnimeStatus = AnimeStatus.PLAN_TO_WATCH,
    val wikiUrl: String = "https://en.wikipedia.org/wiki/Anime",
    val personalReview: String = "", // Personal notes/review
    var isExpanded: Boolean = false
)
