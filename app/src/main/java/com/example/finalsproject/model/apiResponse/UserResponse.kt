package com.example.finalsproject.model.apiResponse

import com.example.finalsproject.model.Song
import com.example.finalsproject.model.User
import kotlinx.serialization.Serializable

object UserResponse {
    @Serializable
    data class Info(
        override val code: Int,
        override val msg: String,
        val data: User? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            NOT_FOUND(404),
            BAD_REQUEST(400),
            SERVER_ERROR(500)
        }
        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class Message(
        override val code: Int,
        override val msg: String,
        val data: String? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            NOT_FOUND(404),
            BAD_REQUEST(400),
            SERVER_ERROR(500)
        }
        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class SongList(
        override val code: Int,
        override val msg: String,
        val currentPage: Int? = null,
        val totalPage: Int? = null,
        val records: Int? = null,
        val data: List<Song>? = null
    ): ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            NOT_FOUND(404),
            BAD_REQUEST(400),
            SERVER_ERROR(500)
        }
        val codeClass = Code.entries.find { code == it.value }
    }
}