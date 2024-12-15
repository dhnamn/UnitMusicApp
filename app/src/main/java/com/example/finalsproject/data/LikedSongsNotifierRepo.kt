package com.example.finalsproject.data

import android.util.Log
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.SongLikeEvent
import com.example.finalsproject.model.apiResponse.UserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface LikedSongsNotifierRepo {
    val likeEvent: SharedFlow<SongLikeEvent>

    fun likeSong(id: Long)
    fun unlikeSong(id: Long)
    fun toggleLike(song: Song) {
        if (song.likedByUser) {
            unlikeSong(song.id)
        } else {
            likeSong(song.id)
        }
    }
}

private const val TAG = "LikedSongsNotifierRepo"

class NetworkLikedSongsNotifierRepo(
    private val usersRepo: UsersRepo
) : LikedSongsNotifierRepo {
    private val _likeEvent = MutableSharedFlow<SongLikeEvent>()
    private val _scope = CoroutineScope(Dispatchers.IO)
    override val likeEvent = _likeEvent.asSharedFlow()

    override fun likeSong(id: Long) {
        val onResponse: (UserResponse.Message) -> Unit = { res ->
            Log.d(TAG, "Response: ${res.code} ${res.msg}")
            when (res.codeClass) {
                UserResponse.Message.Code.SUCCESS -> _scope.launch {
                    _likeEvent.emit(SongLikeEvent(id = id, liked = true))
                }

                else -> {}
            }
        }
        _scope.launch {
            usersRepo.likeSong(
                id = id,
                onResponse = onResponse,
                onFailure = { e ->
                    Log.d(TAG, "Failed to send: ${e.cause} ${e.message}")
                }
            )
        }
    }

    override fun unlikeSong(id: Long) {
        val onResponse: (UserResponse.Message) -> Unit = { res ->
            Log.d(TAG, "Response: ${res.code} ${res.msg}")
            when (res.codeClass) {
                UserResponse.Message.Code.SUCCESS -> _scope.launch {
                    Log.d(TAG, "Song unliked")
                    _likeEvent.emit(SongLikeEvent(id = id, liked = false))
                }

                else -> {}
            }
        }
        _scope.launch {
            usersRepo.unlikeSong(
                id = id,
                onResponse = onResponse,
                onFailure = { e ->
                    Log.d(TAG, "Failed to send: ${e.cause} ${e.message}")
                }
            )
        }
    }
}