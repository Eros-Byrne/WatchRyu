package com.example.mob_dev_portfolio.data

import com.example.mob_dev_portfolio.model.GenreResponse
import com.example.mob_dev_portfolio.model.JikanResponse
import com.example.mob_dev_portfolio.model.SingleAnimeResponse
import com.example.mob_dev_portfolio.model.UserAnimeListResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

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
        @Path("username") username: String,
        @Query("status") status: String = "all",
        @Query("page") page: Int = 1
    ): UserAnimeListResponse

    @GET("anime/{id}")
    suspend fun getAnimeFullById(
        @Path("id") id: Int
    ): SingleAnimeResponse

    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"

        fun create(): JikanApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "WatchRyu/1.0.0 (Android; Mobile; +https://github.com/jakek/WatchRyu)")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JikanApiService::class.java)
        }
    }
}
