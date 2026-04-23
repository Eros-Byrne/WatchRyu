package com.example.mob_dev_portfolio.data

import com.example.mob_dev_portfolio.model.JikanResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the Jikan API (Unofficial MyAnimeList API).
 * This service allows us to fetch anime data without needing an API key.
 */
interface JikanApiService {

    @GET("seasons/now")
    suspend fun getSeasonalAnime(
        @Query("page") page: Int = 1
    ): JikanResponse

    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"

        fun create(): JikanApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JikanApiService::class.java)
        }
    }
}
