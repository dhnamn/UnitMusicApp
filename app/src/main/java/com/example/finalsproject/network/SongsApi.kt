package com.example.finalsproject.network

import com.example.finalsproject.model.apiResponse.SongsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val V1 = "api/v1/app/songs"

interface SongsApi {
    @GET("$V1/random")
    suspend fun getRandomSongs(
        @Query("size") size: Int,
        @Header("Authorization") auth: String
    ): Response<SongsResponse.DataList>

    @GET("$V1/top-listen")
    suspend fun getTopSongs(
        @Query("size") size: Int,
        @Header("Authorization") auth: String
    ): Response<SongsResponse.DataList>

    @GET("$V1/search")
    suspend fun getSongSearchResult(
        @Query("title") title: String?,
        @Query("artist") artist: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") auth: String
    ): Response<SongsResponse.SongSearch>

    @GET("$V1/emotion-search")
    suspend fun getSongByEmotion(
        @Query("message") message: String,
        @Query("size") size: Int,
        @Header("Authorization") auth: String
    ): Response<SongsResponse.SongSearchByEmotion>

    @GET("$V1/{id}")
    suspend fun getSongById(
        @Path("id") id: Long,
        @Header("Authorization") auth: String
    ): Response<SongsResponse.GetSingle>

    @GET("$V1/stream/{id}")
    suspend fun getSongBlob(@Path("id") id: Long): Response<ResponseBody>
}