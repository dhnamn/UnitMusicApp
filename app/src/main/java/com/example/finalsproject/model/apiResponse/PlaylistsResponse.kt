package com.example.finalsproject.model.apiResponse

import com.example.finalsproject.model.Playlist
import kotlinx.serialization.Serializable

object PlaylistsResponse {
    @Serializable
    data class DataList(
        override val code: Int,
        override val msg: String,
        val size: Int? = null,
        val data: List<Playlist>? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class Search(
        override val code: Int,
        override val msg: String,
        val currentPage: Int? = null,
        val totalPage: Int? = null,
        val records: Long? = null,
        val data: List<Playlist>? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class Full(
        override val code: Int,
        override val msg: String,
        val data: Playlist? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }
}