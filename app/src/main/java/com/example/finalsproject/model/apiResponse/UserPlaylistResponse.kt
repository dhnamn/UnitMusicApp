package com.example.finalsproject.model.apiResponse

import com.example.finalsproject.model.UserPlaylist
import kotlinx.serialization.Serializable

object UserPlaylistResponse {
    @Serializable
    data class DataList(
        override val code: Int,
        override val msg: String,
        val size: Int? = null,
        val data: List<UserPlaylist>? = null
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
        val data: UserPlaylist? = null
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class Create(
        override val code: Int,
        override val msg: String
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(201),
            NOT_AUTHORIZED(403),
            BAD_REQUEST(400),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class Update(
        override val code: Int,
        override val msg: String
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            BAD_REQUEST(400),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class Delete(
        override val code: Int,
        override val msg: String
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class Add(
        override val code: Int,
        override val msg: String
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            BAD_REQUEST(400),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }

    @Serializable
    data class Remove(
        override val code: Int,
        override val msg: String
    ) : ApiResponse.Base {
        enum class Code(val value: Int) {
            SUCCESS(200),
            NOT_AUTHORIZED(403),
            SERVER_ERROR(500)
        }

        val codeClass = Code.entries.find { code == it.value }
    }
}