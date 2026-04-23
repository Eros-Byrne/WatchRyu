package com.example.mob_dev_portfolio.model

import com.google.gson.annotations.SerializedName

/**
 * Data classes for Jikan API response.
 * Jikan API v4 wraps results in a 'data' field.
 */
data class JikanResponse(
    @SerializedName("data") val data: List<AnimeDto>
)

data class AnimeDto(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("images") val images: JikanImages,
    @SerializedName("synopsis") val synopsis: String?
)

data class JikanImages(
    @SerializedName("webp") val webp: JikanImageFormat
)

data class JikanImageFormat(
    @SerializedName("large_image_url") val largeImageUrl: String
)

/**
 * Mapper extension function to convert API DTO to our internal domain model.
 */
fun AnimeDto.toDomainModel(): Anime {
    return Anime(
        id = malId,
        title = title,
        imageUrl = images.webp.largeImageUrl,
        synopsis = synopsis ?: "No synopsis available."
    )
}
