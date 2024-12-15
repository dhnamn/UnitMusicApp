package com.example.finalsproject.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPlaylist(
    val id: Long,
    val title: String,
    val description: String,
    val songs: List<Song>? = null
)