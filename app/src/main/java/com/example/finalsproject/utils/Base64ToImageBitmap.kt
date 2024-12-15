package com.example.finalsproject.utils

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private val regex = Regex("^data:image/.*;base64,")

@OptIn(ExperimentalEncodingApi::class)
fun Utils.base64ToImageBitmap(data: String): ImageBitmap {
    val startIdx = regex.find(data)?.let { it.range.last + 1 } ?: 0
    val binary = Base64.decode(
        source = data,
        startIndex = startIdx
    )
    val bitmap = BitmapFactory.decodeByteArray(binary, 0, binary.size)
    return bitmap.asImageBitmap()
}