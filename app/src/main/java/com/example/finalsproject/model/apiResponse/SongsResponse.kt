package com.example.finalsproject.model.apiResponse

import com.example.finalsproject.model.Song
import kotlinx.serialization.Serializable

object SongsResponse {
    @Serializable
    data class DataList(
        override val code: Int,
        override val msg: String,
        val size: Int? = null,
        val data: List<Song>? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_FOUND(404),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class SongSearch(
        override val code: Int,
        override val msg: String,
        val currentPage: Int? = null,
        val totalPage: Int? = null,
        val records: Int? = null,
        val data: List<Song>? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class SongSearchByEmotion(
        override val code: Int,
        override val msg: String,
        val size: Int? = null,
        val data: List<Song>? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class GetSingle(
        override val code: Int,
        override val msg: String,
        val data: Song? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            NOT_FOUND(404),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    class Blob(
        override val code: Int,
        override val msg: String,
        val blob: ByteArray? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_FOUND(404),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }
}