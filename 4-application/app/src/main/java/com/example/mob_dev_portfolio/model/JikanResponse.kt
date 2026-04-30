package com.example.mob_dev_portfolio.model

import com.google.gson.annotations.SerializedName

/**
 * Data classes for Jikan API response.
 * Jikan API v4 wraps results in a 'data' field.
 */
data class JikanResponse(
    @SerializedName("data") val data: List<AnimeDto>?
)

data class SingleAnimeResponse(
    @SerializedName("data") val data: AnimeDto?
)

data class AnimeDto(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("images") val images: JikanImages,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("episodes") val episodes: Int?,
    @SerializedName("url") val url: String?
)

data class JikanImages(
    @SerializedName("webp") val webp: JikanImageFormat?,
    @SerializedName("jpg") val jpg: JikanImageFormat?
)

data class JikanImageFormat(
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("small_image_url") val smallImageUrl: String?,
    @SerializedName("large_image_url") val largeImageUrl: String?
)

/**
 * Mapper extension function to convert API DTO to our internal domain model.
 */
fun AnimeDto.toDomainModel(): Anime {
    val imgUrl = images.webp?.largeImageUrl ?: images.webp?.imageUrl ?: images.jpg?.largeImageUrl ?: images.jpg?.imageUrl ?: ""
    return Anime(
        id = malId,
        title = title,
        imageUrl = imgUrl,
        synopsis = synopsis ?: "No synopsis available.",
        malScore = score ?: 0.0,
        episodes = episodes ?: 0,
        wikiUrl = url ?: "https://myanimelist.net/anime/$malId"
    )
}
