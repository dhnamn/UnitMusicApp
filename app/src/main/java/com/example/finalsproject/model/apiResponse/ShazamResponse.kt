package com.example.finalsproject.model.apiResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object ShazamResponse {
    @Serializable
    data class Recognize(
        val track: Track? = null,
    ) {
        @Serializable
        data class Track(
            val title: String,
            @SerialName("subtitle") val artist: String,
            val images: Images? = null,
            val hub: Hub? = null
        ) {
            @Serializable
            data class Images(
                @SerialName("coverarthq") val coverArt: String? = null
            )

            @Serializable
            data class Hub(
                val options: List<Option>? = null
            ) {
                @Serializable
                data class Option(
                    val actions: List<Action>? = null
                ) {
                    @Serializable
                    data class Action(
                        val uri: String? = null
                    )
                }
            }
        }
    }
}