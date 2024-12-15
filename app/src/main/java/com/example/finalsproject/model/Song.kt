package com.example.finalsproject.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumImgBase64: String,
    val genre: String,
    val length: Int,
    val releaseDate: String,
    val playCount: Long,
    val likeCount: Long,
    val likedByUser: Boolean
)