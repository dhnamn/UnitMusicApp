package com.example.finalsproject.utils

import com.example.finalsproject.model.Song
import com.example.finalsproject.model.SongLikeEvent

object Utils {}

fun Int.wrap(range: IntRange) =
    if (this < range.first) {
        range.last - (range.first - this) + 1
    } else if (this > range.last) {
        range.first + (this - range.last) - 1
    } else {
        this
    }

fun Int.secondsToTimeFormat(): String {
    val hours = this / 3600
    val minutes = this / 60 % 60
    val seconds = this % 60
    return (if (hours > 0) "$hours:" else "") +
            "${"%02d".format(minutes)}:${"%02d".format(seconds)}"
}

fun List<Song>.updateLike(event: SongLikeEvent): List<Song> {
    val idx = indexOfFirst { it.id == event.id }
    if (idx == -1) {
        return this
    }
    return toMutableList().also {
        it[idx] = it[idx].copy(likedByUser = event.liked)
    }
}