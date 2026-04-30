package com.example.mob_dev_portfolio.model

import com.google.gson.annotations.SerializedName

/**
 * Data classes for the Jikan Genre list response.
 */
data class GenreResponse(
    @SerializedName("data") val data: List<GenreDto>
)

data class GenreDto(
    @SerializedName("mal_id") val id: Int,
    @SerializedName("name") val name: String
)
