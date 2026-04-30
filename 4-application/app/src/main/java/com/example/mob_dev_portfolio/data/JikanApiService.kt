package com.example.mob_dev_portfolio.data

import com.example.mob_dev_portfolio.model.GenreResponse
import com.example.mob_dev_portfolio.model.JikanResponse
import com.example.mob_dev_portfolio.model.UserAnimeListResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the Jikan API.
 */
interface JikanApiService {

    @GET("seasons/now")
    suspend fun getSeasonalAnime(
        @Query("page") page: Int = 1
    ): JikanResponse

    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1
    ): JikanResponse

    @GET("seasons/upcoming")
    suspend fun getUpcomingAnime(
        @Query("page") page: Int = 1
    ): JikanResponse

    @GET("genres/anime")
    suspend fun getGenres(): GenreResponse

    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("status") status: String? = null,
        @Query("genres") genres: String? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("sort") sort: String? = null
    ): JikanResponse

    @GET("users/{username}/animelist")
    suspend fun getUserAnimeList(
        @Path("username") username: String
    ): UserAnimeListResponse

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
