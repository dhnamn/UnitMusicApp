package com.example.finalsproject.model.apiRequest

import kotlinx.serialization.Serializable

object AuthRequest {
    @Serializable
    data class Register(
        val username: String,
        val email: String,
        val password: String
    )

    @Serializable
    data class Confirmation(
        val usernameOrEmail: String,
        val otp: String
    )

    @Serializable
    data class Login(
        val usernameOrEmail: String,
        val password: String
    )
}