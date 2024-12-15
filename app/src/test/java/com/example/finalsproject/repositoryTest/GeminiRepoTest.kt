package com.example.finalsproject.repositoryTest

import com.example.finalsproject.data.NetworkGeminiRepo
import com.example.finalsproject.model.apiRequest.GeminiContent
import com.example.finalsproject.model.apiRequest.GeminiRequest
import com.example.finalsproject.model.apiResponse.GeminiResponse
import com.example.finalsproject.network.GeminiApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class GeminiRepoTest {
    private lateinit var repo: NetworkGeminiRepo
    private lateinit var api: GeminiApi

    @Before
    fun setup() {
        api = mock()
        repo = NetworkGeminiRepo(
            geminiApi = api
        )
    }

    @Test
    fun geminiRepoTest_getStory() = runBlocking {
        val response = GeminiResponse(
            candidates = listOf(
                GeminiResponse.Candidate(
                    content = GeminiResponse.Candidate.Content(
                        parts = listOf(
                            GeminiContent.Part(
                                text = "reply"
                            )
                        ),
                        role = "user"
                    ),
                    finishReason = "",
                    avgLogprobs = 1.0
                )
            ),
            usageMetadata = GeminiResponse.UsageMetadata(
                promptTokenCount = 1,
                candidatesTokenCount = 1,
                totalTokenCount = 2,
            ),
            modelVersion = "gemini"
        )
        `when`(api.getStory(any(), any(), any())).thenReturn(Response.success(response))

        val result = repo.getStory(
            body = GeminiRequest(
                contents = listOf()
            )
        )

        Assert.assertEquals(result.isSuccess, true)
    }
}