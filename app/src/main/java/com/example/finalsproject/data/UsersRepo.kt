package com.example.finalsproject.data

import com.example.finalsproject.model.apiRequest.UserPlaylistRequest
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import com.example.finalsproject.model.apiResponse.UserResponse
import com.example.finalsproject.model.apiResponse.getApiResult
import com.example.finalsproject.network.UsersApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


interface UsersRepo {
    suspend fun getUserInfo(
        onResponse: (UserResponse.Info) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun uploadUserAvatar(
        data: ByteArray,
        extension: String,
        onResponse: (UserResponse.Info) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun likeSong(
        id: Long,
        onResponse: (UserResponse.Message) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun unlikeSong(
        id: Long,
        onResponse: (UserResponse.Message) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getLikedSongs(
        size: Int,
        page: Int,
        onResponse: (UserResponse.SongList) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun createPlaylist(
        body: UserPlaylistRequest.Create,
        onResponse: (UserPlaylistResponse.Create) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getAllUserPlaylist(
        onResponse: (UserPlaylistResponse.DataList) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getUserPlaylist(
        playlistId: Long,
        onResponse: (UserPlaylistResponse.Full) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun updateUserPlaylist(
        playlistId: Long,
        body: UserPlaylistRequest.Update,
        onResponse: (UserPlaylistResponse.Update) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun deleteUserPlaylist(
        playlistId: Long,
        onResponse: (UserPlaylistResponse.Delete) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun addSongToUserPlaylist(
        songId: Long,
        playlistId: Long,
        onResponse: (UserPlaylistResponse.Add) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun removeSongFromUserPlaylist(
        songId: Long,
        playlistId: Long,
        onResponse: (UserPlaylistResponse.Remove) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}

class NetworkUsersRepo(
    private val api: UsersApi,
    private val credentialsRepo: CredentialsRepo
) : UsersRepo {
    private suspend fun makeAuth(): String {
        val token = checkNotNull(credentialsRepo.getToken())
        return "Bearer $token"
    }

    override suspend fun getUserInfo(
        onResponse: (UserResponse.Info) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.getUserInfo(auth = makeAuth()) },
        makeFailResponse = { UserResponse.Info(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun uploadUserAvatar(
        data: ByteArray,
        extension: String,
        onResponse: (UserResponse.Info) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = {
            val body = data.toRequestBody("image/$extension".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData(
                name = "file",
                filename = "image",
                body = body
            )
            api.uploadUserAvatar(file = part, auth = makeAuth())
        },
        makeFailResponse = { UserResponse.Info(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun likeSong(
        id: Long,
        onResponse: (UserResponse.Message) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.likeSong(id = id, auth = makeAuth()) },
        makeFailResponse = { UserResponse.Message(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun unlikeSong(
        id: Long,
        onResponse: (UserResponse.Message) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.unlikeSong(id = id, auth = makeAuth()) },
        makeFailResponse = { UserResponse.Message(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getLikedSongs(
        size: Int,
        page: Int,
        onResponse: (UserResponse.SongList) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.getLikedSongs(size = size, page = page, auth = makeAuth()) },
        makeFailResponse = { UserResponse.SongList(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun createPlaylist(
        body: UserPlaylistRequest.Create,
        onResponse: (UserPlaylistResponse.Create) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.createPlaylist(body = body, auth = makeAuth()) },
        makeFailResponse = { UserPlaylistResponse.Create(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun deleteUserPlaylist(
        playlistId: Long,
        onResponse: (UserPlaylistResponse.Delete) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.deleteUserPlaylist(playlistId = playlistId, auth = makeAuth()) },
        makeFailResponse = { UserPlaylistResponse.Delete(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getAllUserPlaylist(
        onResponse: (UserPlaylistResponse.DataList) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.getAllUserPlaylists(auth = makeAuth()) },
        makeFailResponse = { UserPlaylistResponse.DataList(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun updateUserPlaylist(
        playlistId: Long,
        body: UserPlaylistRequest.Update,
        onResponse: (UserPlaylistResponse.Update) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.updateUserPlaylist(playlistId = playlistId, body = body, auth = makeAuth()) },
        makeFailResponse = { UserPlaylistResponse.Update(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getUserPlaylist(
        playlistId: Long,
        onResponse: (UserPlaylistResponse.Full) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.getUserPlaylist(playlistId = playlistId, auth = makeAuth()) },
        makeFailResponse = { UserPlaylistResponse.Full(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun addSongToUserPlaylist(
        songId: Long,
        playlistId: Long,
        onResponse: (UserPlaylistResponse.Add) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = {
            api.addSongToUserPlaylist(
                songId = songId,
                playlistId = playlistId,
                auth = makeAuth()
            )
        },
        makeFailResponse = { UserPlaylistResponse.Add(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun removeSongFromUserPlaylist(
        songId: Long,
        playlistId: Long,
        onResponse: (UserPlaylistResponse.Remove) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = {
            api.removeSongFromUserPlaylist(
                songId = songId,
                playlistId = playlistId,
                auth = makeAuth()
            )
        },
        makeFailResponse = { UserPlaylistResponse.Remove(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)
}