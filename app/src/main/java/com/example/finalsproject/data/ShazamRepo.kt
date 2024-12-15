package com.example.finalsproject.data

import com.example.finalsproject.BuildConfig
import com.example.finalsproject.model.apiRequest.shazamRecognizeRequest
import com.example.finalsproject.model.apiResponse.ShazamResponse
import com.example.finalsproject.network.ShazamApi
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface ShazamRepo {
    suspend fun recognize(
        rawAudio: ByteArray,
        onResponse: (ShazamResponse.Recognize?) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}

class NetworkShazamRepo(
    val api: ShazamApi,
) : ShazamRepo {
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun recognize(
        rawAudio: ByteArray,
        onResponse: (ShazamResponse.Recognize?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            api.recognize(
                rawAudio = rawAudio
                    .toList()
                    .let { if (it.size > 500000) it.subList(0, 500000) else it }
                    .toByteArray()
                    .let { Base64.encodeToByteArray(it) }
                    .shazamRecognizeRequest,
                key = BuildConfig.SHAZAM_API_KEY
            ).also { onResponse(it.body()) }
        } catch (e: Throwable) {
            onFailure(e)
        }
    }
}