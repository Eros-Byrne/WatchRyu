package com.example.mob_dev_portfolio.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mob_dev_portfolio.model.Anime
import com.example.mob_dev_portfolio.model.AnimeStatus

/**
 * Room Entity representing an Anime in the user's list.
 * Updated to include community scores and personal reviews.
 */
@Entity(tableName = "anime_list")
data class AnimeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val synopsis: String,
    val episodes: Int,
    val episodesWatched: Int,
    val score: Int,
    val malScore: Double,
    val status: AnimeStatus,
    val wikiUrl: String,
    val personalReview: String
)

fun AnimeEntity.toDomainModel(): Anime {
    return Anime(
        id = id,
        title = title,
        imageUrl = imageUrl,
        synopsis = synopsis,
        episodes = episodes,
        episodesWatched = episodesWatched,
        score = score,
        malScore = malScore,
        status = status,
        wikiUrl = wikiUrl,
        personalReview = personalReview
    )
}

fun Anime.toEntity(): AnimeEntity {
    return AnimeEntity(
        id = id,
        title = title,
        imageUrl = imageUrl,
        synopsis = synopsis,
        episodes = episodes,
        episodesWatched = episodesWatched,
        score = score,
        malScore = malScore,
        status = status,
        wikiUrl = wikiUrl,
        personalReview = personalReview
    )
}
