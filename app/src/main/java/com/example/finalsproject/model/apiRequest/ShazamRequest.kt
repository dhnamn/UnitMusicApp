package com.example.finalsproject.model.apiRequest

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

val ByteArray.shazamRecognizeRequest: RequestBody
    get() = this.toRequestBody("text/plain".toMediaType())