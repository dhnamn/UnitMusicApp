package com.example.finalsproject.model.apiResponse

import kotlinx.serialization.Serializable

object ApiResponse {
    @Serializable
    sealed interface Base {
        val code: Int
        val msg: String
    }

    @Serializable
    data class Fail(
        override val code: Int,
        override val msg: String
    ) : Base
}


