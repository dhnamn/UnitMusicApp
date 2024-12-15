package com.example.finalsproject.model.apiResponse

import android.util.Log
import kotlinx.serialization.json.Json
import retrofit2.Response

private const val TAG = "GetApiResult"

suspend fun <T> getApiResult(
    call: suspend () -> Response<T>,
    makeFailResponse: (ApiResponse.Fail) -> T
) =
    try {
        val res = call()
        Result.success(
            if (res.isSuccessful) {
                res.body()!!
            } else {
                val errorBody = res.errorBody()!!.string()
                Log.d(TAG, "Api error body: $errorBody")
                val failBody = Json.decodeFromString<ApiResponse.Fail>(errorBody)
                makeFailResponse(failBody)
            }
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
