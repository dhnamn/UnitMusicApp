package com.example.finalsproject.repositoryTest

import com.example.finalsproject.data.NetworkShazamRepo
import com.example.finalsproject.model.apiRequest.shazamRecognizeRequest
import com.example.finalsproject.model.apiResponse.ShazamResponse
import com.example.finalsproject.network.ShazamApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import retrofit2.Response
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ShazamRepoTest {
    private lateinit var repo: NetworkShazamRepo
    private lateinit var api: ShazamApi

    @Before
    fun setup() {
        api = mock()
        repo = NetworkShazamRepo(
            api = api
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun shazamRepoTest_recognize() = runBlocking {
        val rawAudio = ByteArray(1000)
        val response = ShazamResponse.Recognize(null)

        `when`(api.recognize(
            rawAudio = rawAudio
                .toList()
                .let { if (it.size > 500000) it.subList(0, 500000) else it }
                .toByteArray()
                .let { Base64.encodeToByteArray(it) }
                .shazamRecognizeRequest,
            key = "key"
        )).thenReturn(Response.success(response))

        repo.recognize(
            rawAudio,
            onResponse = {
                Assert.assertEquals(response.track, null)
            },
            onFailure = {}
        )
    }
}