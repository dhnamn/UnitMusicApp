package com.example.finalsproject.data

import com.example.finalsproject.model.apiResponse.PlaylistsResponse
import com.example.finalsproject.model.apiResponse.getApiResult
import com.example.finalsproject.network.PlaylistsApi

interface PlaylistsRepo {
    suspend fun getRandomPlaylists(
        size: Int,
        onResponse: (PlaylistsResponse.DataList) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getFullPlaylistById(
        id: Long,
        onResponse: (PlaylistsResponse.Full) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getPlaylistSearchResult(
        title: String,
        size: Int,
        page: Int,
        onResponse: (PlaylistsResponse.Search) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}

class NetworkPlaylistsRepo(
    private val api: PlaylistsApi,
    private val credentialsRepo: CredentialsRepo
) : PlaylistsRepo {
    private suspend fun makeAuth(): String {
        val token = checkNotNull(credentialsRepo.getToken())
        return "Bearer $token"
    }

    override suspend fun getRandomPlaylists(
        size: Int,
        onResponse: (PlaylistsResponse.DataList) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = {
            api.getRandomPlaylists(
                size = size,
                auth = makeAuth()
            )
        },
        makeFailResponse = { PlaylistsResponse.DataList(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getFullPlaylistById(
        id: Long,
        onResponse: (PlaylistsResponse.Full) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = {
            api.getFullPlaylistById(
                id = id,
                auth = makeAuth()
            )
        },
        makeFailResponse = { PlaylistsResponse.Full(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getPlaylistSearchResult(
        title: String,
        size: Int,
        page: Int,
        onResponse: (PlaylistsResponse.Search) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = {
            api.getPlaylistSearchResult(
                title = title,
                size = size,
                page = page,
                auth = makeAuth()
            )
        },
        makeFailResponse = { PlaylistsResponse.Search(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)
}