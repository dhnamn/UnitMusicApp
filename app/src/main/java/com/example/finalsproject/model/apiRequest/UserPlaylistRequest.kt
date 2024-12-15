package com.example.finalsproject.model.apiRequest

import kotlinx.serialization.Serializable

object UserPlaylistRequest {
    @Serializable
    data class Create(
        val title: String,
        val description: String
    )

    @Serializable
    data class Update(
        val title: String,
        val description: String
    )
}