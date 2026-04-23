package com.example.mob_dev_portfolio.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mob_dev_portfolio.model.Anime

/**
 * Room Entity representing a favorited Anime.
 * Using Room for local persistence as required by the assignment (replacing banned SharedPreferences).
 */
@Entity(tableName = "favorite_anime")
data class AnimeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val synopsis: String
)

/**
 * Mapper to convert between Entity and Domain model.
 */
fun AnimeEntity.toDomainModel(): Anime {
    return Anime(
        id = id,
        title = title,
        imageUrl = imageUrl,
        synopsis = synopsis,
        isFavorite = true
    )
}

fun Anime.toEntity(): AnimeEntity {
    return AnimeEntity(
        id = id,
        title = title,
        imageUrl = imageUrl,
        synopsis = synopsis
    )
}
