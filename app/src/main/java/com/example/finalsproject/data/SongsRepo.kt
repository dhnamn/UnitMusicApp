package com.example.finalsproject.data

import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.model.apiResponse.getApiResult
import com.example.finalsproject.network.SongsApi

interface SongsRepo {
    suspend fun getRandomSongs(
        size: Int,
        onResponse: (SongsResponse.DataList) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getTopSongs(
        size: Int,
        onResponse: (SongsResponse.DataList) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getSongSearchResult(
        title: String? = null,
        artist: String? = null,
        page: Int,
        size: Int,
        onResponse: (SongsResponse.SongSearch) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getSongByEmotion(
        message: String,
        size: Int,
        onResponse: (SongsResponse.SongSearchByEmotion) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getSongById(
        id: Long,
        onResponse: (SongsResponse.GetSingle) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    suspend fun getSongBlob(
        id: Long,
        onResponse: (SongsResponse.Blob) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun songStreamDataSource(id: Long): String
}

class NetworkSongsRepo(
    private val baseUrl: String,
    private val api: SongsApi,
    private val credentialsRepo: CredentialsRepo
) : SongsRepo {
    private suspend fun makeAuth(): String {
        val token = checkNotNull(credentialsRepo.getToken())
        return "Bearer $token"
    }

    override suspend fun getRandomSongs(
        size: Int,
        onResponse: (SongsResponse.DataList) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.getRandomSongs(size = size, auth = makeAuth()) },
        makeFailResponse = { SongsResponse.DataList(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getTopSongs(
        size: Int,
        onResponse: (SongsResponse.DataList) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.getTopSongs(size = size, auth = makeAuth()) },
        makeFailResponse = { SongsResponse.DataList(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getSongSearchResult(
        title: String?,
        artist: String?,
        page: Int,
        size: Int,
        onResponse: (SongsResponse.SongSearch) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = {
            api.getSongSearchResult(
                title = title,
                artist = artist,
                size = size,
                page = page,
                auth = makeAuth()
            )
        },
        makeFailResponse = { SongsResponse.SongSearch(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getSongByEmotion(
        message: String,
        size: Int,
        onResponse: (SongsResponse.SongSearchByEmotion) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = {
            api.getSongByEmotion(
                message = message,
                size = size,
                auth = makeAuth()
            )
        },
        makeFailResponse = { SongsResponse.SongSearchByEmotion(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getSongById(
        id: Long,
        onResponse: (SongsResponse.GetSingle) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = getApiResult(
        call = { api.getSongById(id = id, auth = makeAuth()) },
        makeFailResponse = { SongsResponse.GetSingle(code = it.code, msg = it.msg) }
    ).fold(onSuccess = onResponse, onFailure = onFailure)

    override suspend fun getSongBlob(
        id: Long,
        onResponse: (SongsResponse.Blob) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            val res = api.getSongBlob(id = id)
            val blob = res.body()?.let { body ->
                val result = body.bytes()
                body.close()
                result
            }
            onResponse(
                SongsResponse.Blob(
                    code = res.code(),
                    msg = res.message(),
                    blob = blob
                )
            )
        } catch (e: Throwable) {
            onFailure(e)
        }
    }

    override fun songStreamDataSource(id: Long) = "${baseUrl}api/v1/app/songs/stream/$id"
}