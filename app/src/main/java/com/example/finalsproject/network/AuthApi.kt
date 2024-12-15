package com.example.finalsproject.network

import com.example.finalsproject.model.apiRequest.AuthRequest
import com.example.finalsproject.model.apiResponse.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

private const val V1 = "api/v1/auth"

interface AuthApi {
    @POST("$V1/register")
    suspend fun postRegister(@Body body: AuthRequest.Register): Response<AuthResponse.Register>

    @POST("$V1/register/confirmation")
    suspend fun postConfirmation(@Body body: AuthRequest.Confirmation)
            : Response<AuthResponse.Confirmation>

    @POST("$V1/login")
    suspend fun postLogin(@Body body: AuthRequest.Login): Response<AuthResponse.Login>
}