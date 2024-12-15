package com.example.finalsproject.model

import kotlinx.serialization.Serializable

@Serializable
data class User (
    val id: Long,
    val username: String,
    val email: String,
    val avatarImgBase64: String?,
    val createDate: String
)