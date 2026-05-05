package com.example.mob_dev_portfolio.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AniListApiService {

    @POST("/")
    suspend fun getAnime(@Body request: AniListRequest): AniListResponse

    companion object {
        private const val BASE_URL = "https://graphql.anilist.co/"

        fun create(): AniListApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AniListApiService::class.java)
        }
    }
}
