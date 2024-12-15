package com.example.finalsproject.network

import com.example.finalsproject.model.apiResponse.PlaylistsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val V1 = "api/v1/app/playlists"

interface PlaylistsApi {
    @GET("$V1/random")
    suspend fun getRandomPlaylists(
        @Query("size") size: Int,
        @Header("Authorization") auth: String
    ): Response<PlaylistsResponse.DataList>

    @GET("$V1/{id}")
    suspend fun getFullPlaylistById(
        @Path("id") id: Long,
        @Header("Authorization") auth: String
    ): Response<PlaylistsResponse.Full>

    @GET("$V1/search")
    suspend fun getPlaylistSearchResult(
        @Query("title") title: String,
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Header("Authorization") auth: String
    ): Response<PlaylistsResponse.Search>
}