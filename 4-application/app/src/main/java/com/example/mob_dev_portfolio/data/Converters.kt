package com.example.mob_dev_portfolio.data

import androidx.room.TypeConverter
import com.example.mob_dev_portfolio.model.AnimeStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: AnimeStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): AnimeStatus {
        return AnimeStatus.valueOf(value)
    }
}
