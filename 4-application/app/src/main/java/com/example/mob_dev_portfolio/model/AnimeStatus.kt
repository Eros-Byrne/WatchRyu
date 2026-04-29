package com.example.mob_dev_portfolio.model

/**
 * Enum for the tracking status of an anime.
 * Added ON_HOLD to support detailed statistics.
 */
enum class AnimeStatus {
    AIRING,
    WATCHING,
    COMPLETED,
    ON_HOLD,
    DROPPED,
    PLAN_TO_WATCH
}
