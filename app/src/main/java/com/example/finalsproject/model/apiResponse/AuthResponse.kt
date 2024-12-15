package com.example.finalsproject.model.apiResponse

import kotlinx.serialization.Serializable

object AuthResponse {
    @Serializable
    data class Register(
        override val code: Int,
        override val msg: String
    ) : ApiResponse.Base {
        val codeSuccess = 201
        val codeFailure = 400
    }

    @Serializable
    data class Confirmation(
        override val code: Int,
        override val msg: String
    ) : ApiResponse.Base {
        val codeSuccess = 201
        val codeExpired = 452
        val codeIncorrect = 400
    }

    @Serializable
    data class Login(
        override val code: Int,
        override val msg: String,
        val token: String? = null
    ) : ApiResponse.Base {
        val codeSuccess = 200
        val codeWrongCredentials = 400
        val codeNeedActivation = 402
    }
}