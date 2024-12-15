package com.example.finalsproject.data

import com.example.finalsproject.model.apiRequest.AuthRequest
import com.example.finalsproject.model.apiResponse.AuthResponse
import com.example.finalsproject.model.apiResponse.getApiResult
import com.example.finalsproject.network.AuthApi

interface AuthRepo {
    suspend fun postRegister(
        body: AuthRequest.Register,
        onSuccess: (AuthResponse.Register) -> Unit,
        onFailure: (Throwable) -> Unit
    )
    suspend fun postConfirmation(
        body: AuthRequest.Confirmation,
        onResponse: (AuthResponse.Confirmation) -> Unit,
        onFailure: (Throwable) -> Unit
    )
    suspend fun postLogin(
        body: AuthRequest.Login,
        onResponse: (AuthResponse.Login) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}

class NetworkAuthRepo(
    private val api: AuthApi
) : AuthRepo {
    override suspend fun postRegister(
        body: AuthRequest.Register,
        onSuccess: (AuthResponse.Register) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        getApiResult(call = { api.postRegister(body) }) {
            AuthResponse.Register(
                code = it.code,
                msg = it.msg
            )
        }.fold(onSuccess = onSuccess, onFailure = onFailure)
    }

    override suspend fun postConfirmation(
        body: AuthRequest.Confirmation,
        onResponse: (AuthResponse.Confirmation) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        getApiResult(call = { api.postConfirmation(body) }) {
            AuthResponse.Confirmation(
                code = it.code,
                msg = it.msg
            )
        }.fold(onSuccess = onResponse, onFailure = onFailure)
    }

    override suspend fun postLogin(
        body: AuthRequest.Login,
        onResponse: (AuthResponse.Login) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        getApiResult(call = { api.postLogin(body) }) {
            AuthResponse.Login(
                code = it.code,
                msg = it.msg
            )
        }.fold(onSuccess = onResponse, onFailure = onFailure)
    }
}