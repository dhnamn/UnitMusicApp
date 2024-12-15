package com.example.finalsproject.repositoryTest

import com.example.finalsproject.data.NetworkAuthRepo
import com.example.finalsproject.model.apiRequest.AuthRequest
import com.example.finalsproject.model.apiResponse.AuthResponse
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.network.AuthApi
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
class AuthRepoTest {
    private lateinit var repo: NetworkAuthRepo
    private lateinit var api: AuthApi

    @Before
    fun setup() {
        api = mock()
        repo = NetworkAuthRepo(
            api = api
        )
    }

    @Test
    fun authRepoTest_postRegister() = runBlocking {
        val response = AuthResponse.Register(code = 200, msg = "Success")
        `when`(api.postRegister(AuthRequest.Register("username", "email", "password"))).thenReturn(Response.success(response))

        repo.postRegister(
            body = AuthRequest.Register("username", "email", "password"),
            onSuccess = { Assert.assertEquals(it.code, 200)},
            onFailure = {}
        )
    }

    @Test
    fun authRepoTest_postConfirmation() = runBlocking {
        val response = AuthResponse.Confirmation(code = 200, msg = "Success")
        `when`(api.postConfirmation(AuthRequest.Confirmation("username", "111111"))).thenReturn(Response.success(response))

        repo.postConfirmation(
            body = AuthRequest.Confirmation("username", "111111"),
            onResponse = { Assert.assertEquals(it.code, 200)},
            onFailure = {}
        )
    }

    @Test
    fun authRepoTest_postLogin() = runBlocking {
        val response = AuthResponse.Login(code = 200, msg = "Success")
        `when`(api.postLogin(AuthRequest.Login("username", "111111"))).thenReturn(Response.success(response))

        repo.postLogin(
            body = AuthRequest.Login("username", "111111"),
            onResponse = { Assert.assertEquals(it.code, 200)},
            onFailure = {}
        )
    }
}