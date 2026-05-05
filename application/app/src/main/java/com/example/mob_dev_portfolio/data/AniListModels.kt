package com.example.mob_dev_portfolio.data

import com.example.mob_dev_portfolio.model.Anime
import com.example.mob_dev_portfolio.model.AnimeStatus
import com.google.gson.annotations.SerializedName

data class AniListRequest(
    val query: String,
    val variables: Map<String, Any>
)

data class AniListResponse(
    val data: AniListData
)

data class AniListData(
    @SerializedName("Page") val page: AniListPage
)

data class AniListPage(
    val media: List<AniListMedia>
)

data class AniListMedia(
    val id: Int,
    val title: AniListTitle,
    val description: String?,
    val episodes: Int?,
    val averageScore: Int?,
    val coverImage: AniListCoverImage,
    val siteUrl: String?
)

data class AniListTitle(
    val english: String?,
    val romaji: String?
)

data class AniListCoverImage(
    val large: String?
)

fun AniListMedia.toDomainModel(): Anime {
    return Anime(
        id = id,
        title = title.english ?: title.romaji ?: "Unknown Title",
        imageUrl = coverImage.large ?: "",
        synopsis = description?.replace(Regex("<[^>]*>"), "") ?: "No synopsis available.",
        episodes = episodes ?: 0,
        malScore = (averageScore ?: 0).toDouble() / 10.0,
        wikiUrl = siteUrl ?: "https://anilist.co/anime/$id"
    )
}
