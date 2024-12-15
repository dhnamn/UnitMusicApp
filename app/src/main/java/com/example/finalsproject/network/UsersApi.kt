
package com.example.finalsproject.network

import com.example.finalsproject.model.apiRequest.UserPlaylistRequest
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import com.example.finalsproject.model.apiResponse.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

private const val V1 = "api/v1/users"

interface UsersApi {
    @GET(V1)
    suspend fun getUserInfo(
        @Header("Authorization") auth: String
    ): Response<UserResponse.Info>

    @PATCH("$V1/avatar")
    @Multipart
    suspend fun uploadUserAvatar(
        @Part file: MultipartBody.Part,
        @Header("Authorization") auth: String
    ): Response<UserResponse.Info>

    @PATCH("$V1/songs/{id}/like")
    suspend fun likeSong(
        @Path("id") id: Long,
        @Header("Authorization") auth: String
    ): Response<UserResponse.Message>

    @PATCH("$V1/songs/{id}/unlike")
    suspend fun unlikeSong(
        @Path("id") id: Long,
        @Header("Authorization") auth: String
    ): Response<UserResponse.Message>

    @GET("$V1/songs/liked")
    suspend fun getLikedSongs(
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Header("Authorization") auth: String
    ): Response<UserResponse.SongList>

    @POST("$V1/playlists")
    suspend fun createPlaylist(
        @Body body: UserPlaylistRequest.Create,
        @Header("Authorization") auth: String
    ) : Response<UserPlaylistResponse.Create>

    @GET("$V1/playlists")
    suspend fun getAllUserPlaylists(
        @Header("Authorization") auth: String
    ) : Response<UserPlaylistResponse.DataList>

    @GET("$V1/playlists/{playlistId}")
    suspend fun getUserPlaylist(
        @Path("playlistId") playlistId: Long,
        @Header("Authorization") auth: String
    ) : Response<UserPlaylistResponse.Full>

    @PATCH("$V1/playlists/{playlistId}")
    suspend fun updateUserPlaylist(
        @Path("playlistId") playlistId: Long,
        @Body body: UserPlaylistRequest.Update,
        @Header("Authorization") auth: String
    ): Response<UserPlaylistResponse.Update>

    @DELETE("$V1/playlists/{playlistId}")
    suspend fun deleteUserPlaylist(
        @Path("playlistId") playlistId: Long,
        @Header("Authorization") auth: String
    ) : Response<UserPlaylistResponse.Delete>

    @POST("$V1/playlists/{playlistId}/songs/{songId}")
    suspend fun addSongToUserPlaylist(
        @Path("songId") songId: Long,
        @Path("playlistId") playlistId: Long,
        @Header("Authorization") auth: String
    ) : Response<UserPlaylistResponse.Add>

    @DELETE("$V1/playlists/{playlistId}/songs/{songId}")
    suspend fun removeSongFromUserPlaylist(
        @Path("songId") songId: Long,
        @Path("playlistId") playlistId: Long,
        @Header("Authorization") auth: String
    ) : Response<UserPlaylistResponse.Remove>
}
