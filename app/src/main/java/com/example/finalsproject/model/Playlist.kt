package com.example.finalsproject.model

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: Long,
    val title: String,
    val description: String,
    val imgBase64: String,
    val songs: List<Song>? = null
)
