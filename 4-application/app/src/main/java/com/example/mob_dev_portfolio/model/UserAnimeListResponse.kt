package com.example.mob_dev_portfolio.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for the Jikan User Anime List endpoint.
 */
data class UserAnimeListResponse(
    @SerializedName("data") val data: List<UserAnimeEntryDto>
)

data class UserAnimeEntryDto(
    @SerializedName("status") val status: String,
    @SerializedName("score") val score: Int,
    @SerializedName("num_episodes_watched") val episodesWatched: Int,
    @SerializedName("anime") val anime: AnimeDto
)

/**
 * Mapper to convert user list entries to our local Domain model.
 */
fun UserAnimeEntryDto.toDomainModel(): Anime {
    val domainStatus = when (status.lowercase()) {
        "watching" -> AnimeStatus.WATCHING
        "completed" -> AnimeStatus.COMPLETED
        "onhold", "on-hold" -> AnimeStatus.ON_HOLD
        "dropped" -> AnimeStatus.DROPPED
        "plantowatch", "plan to watch" -> AnimeStatus.PLAN_TO_WATCH
        else -> AnimeStatus.PLAN_TO_WATCH
    }
    
    val baseAnime = anime.toDomainModel()
    return baseAnime.copy(
        episodesWatched = episodesWatched,
        score = score,
        status = domainStatus
    )
}
