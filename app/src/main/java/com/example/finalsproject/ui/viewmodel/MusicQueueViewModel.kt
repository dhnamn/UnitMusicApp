package com.example.finalsproject.ui.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.GeminiRepo
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.SongsRepo
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.apiRequest.GeminiRequest
import com.example.finalsproject.ui.viewmodel.MusicQueueState.RepeatMode
import com.example.finalsproject.utils.updateLike
import com.example.finalsproject.utils.wrap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "MusicQueueViewModel"

data class MusicQueueState(
    val queue: List<Song> = emptyList(),
    val shuffledQueue: List<Song> = emptyList(),
    val status: Status = Status.Idle,
    val currentIdx: Int = -1,
    val shuffleMode: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val songChanged: Boolean = false,
    val isLoadingStory: Boolean = false,
    val story: String = ""
) {
    sealed interface Status {
        data object Idle : Status
        data object Failed : Status
        data object Loading : Status
        data class Ready(val isPlaying: Boolean) : Status
    }

    enum class RepeatMode {
        OFF, ALL, ONE
    }

    val currentQueue: List<Song> = if (shuffleMode) shuffledQueue else queue
    val currentSong: Song? = if (currentIdx != -1) currentQueue[currentIdx] else null
    val prevSong: Song? = if (queue.isNotEmpty()) {
        currentQueue[(currentIdx - 1).wrap(queue.indices)]
    } else null
    val nextSong: Song? = if (queue.isNotEmpty()) {
        currentQueue[(currentIdx + 1).wrap(queue.indices)]
    } else null
}

class MusicQueueViewModel(
    private val songsRepo: SongsRepo,
    private val geminiRepo: GeminiRepo,
    val likeNotifier: LikedSongsNotifierRepo
) : ViewModel() {
    private val mutState = MutableStateFlow(MusicQueueState())
    val state = mutState.asStateFlow()

    private val mutProgressStateMs = MutableStateFlow(0f)
    val progressStateMs = mutProgressStateMs.asStateFlow()

    private val player: MediaPlayer = MediaPlayer()

    init {
        viewModelScope.launch {
            likeNotifier.likeEvent.collect { event ->
                mutState.update {
                    it.copy(
                        queue = it.queue.updateLike(event),
                        shuffledQueue = it.queue.updateLike(event)
                    )
                }
            }
        }
    }

    fun goToPrev() {
        mutState.update { it.goToPrev() }
        updatePlayer()
    }

    fun goToNextExplicitly() {
        mutState.update { it.goToNextExplicitly() }
        updatePlayer()
    }

    fun jumpTo(song: Song) {
        mutState.update { it.jumpTo(song) }
        updatePlayer()
    }

    fun add(song: Song) {
        mutState.update { it.add(song) }
        updatePlayer()
    }

    fun setQueue(songs: List<Song>) {
        mutState.update { it.setQueue(songs) }
        updatePlayer()
    }

    fun remove(song: Song) {
        mutState.update { it.remove(song) }
        updatePlayer()
    }

    fun clear() {
        mutState.update { it.clear() }
        updatePlayer()
    }

    fun togglePlayOrPause() {
        mutState.update {
            it.copy(
                status = when (val status = state.value.status) {
                    is MusicQueueState.Status.Ready -> {
                        if (status.isPlaying) {
                            player.pause()
                        } else {
                            player.start()
                        }
                        MusicQueueState.Status.Ready(isPlaying = !status.isPlaying)
                    }

                    else -> status
                }
            )
        }
    }

    fun toggleShuffleMode() = mutState.update { it.toggleShuffleMode() }

    fun cycleRepeatMode() = mutState.update {
        it.copy(
            repeatMode = when (state.value.repeatMode) {
                RepeatMode.OFF -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.OFF
            }
        )
    }

    fun seek(ms: Float) {
        if (state.value.currentIdx != -1) {
            mutProgressStateMs.update { ms }
            player.seekTo(ms.toInt())
        }
    }

    private fun updatePlayer() {
        if (!state.value.songChanged) {
            return
        }
        if (state.value.currentIdx == -1) {
            mutState.update { it.copy(status = MusicQueueState.Status.Idle) }
            player.reset()
            return
        }
        mutState.update {
            it.copy(
                status = MusicQueueState.Status.Loading,
                story = "",
                isLoadingStory = false
            )
        }
        viewModelScope.launch {
            player.apply {
                reset()
                setOnPreparedListener {
                    mutState.update {
                        it.copy(status = MusicQueueState.Status.Ready(isPlaying = true))
                    }
                    start()
                    viewModelScope.launch {
                        while (state.value.status is MusicQueueState.Status.Ready) {
                            mutProgressStateMs.update { player.currentPosition.toFloat() }
                            delay(500)
                        }
                        mutProgressStateMs.update { 0f }
                    }
                }
                setOnCompletionListener {
                    mutState.update { it.goToNext().copy(status = MusicQueueState.Status.Idle) }
                    updatePlayer()
                }
                setOnErrorListener { _, _, _ ->
                    mutState.update {
                        it.copy(status = MusicQueueState.Status.Failed)
                    }
                    true
                }
                setDataSource(songsRepo.songStreamDataSource(state.value.currentSong!!.id))
                prepareAsync()
            }
        }
    }

    fun getStory() {
        mutState.update {
            it.copy(
                story = "",
                isLoadingStory = true
            )
        }
        val songTitle = mutState.value.currentSong?.title ?: ""
        val songArtist = mutState.value.currentSong?.artist ?: ""
        viewModelScope.launch {
            val request = GeminiRequest.make(
                prompt = "Write a story based on the interpretation of this song, " +
                        "keeping faithful to its meaning, " +
                        "avoid using the words 'flickering', 'neon' " +
                        "(limit to under 100 words): $songTitle by $songArtist"
            )
            var text = ""
            geminiRepo.getStory(request).fold(
                onSuccess = { response ->
                    text = response!!
                },
                onFailure = { error ->
                    text = error.message!!
                }
            )
            mutState.update {
                it.copy(
                    story = text,
                    isLoadingStory = false
                )
            }
        }
    }

    override fun onCleared() {
        player.release()
    }

    private fun MusicQueueState.goToPrev(): MusicQueueState =
        if (currentIdx != -1) {
            val newIdx = (currentIdx - 1).wrap(queue.indices)
            copy(
                currentIdx = newIdx,
                songChanged = newIdx != currentIdx
            )
        } else {
            copy(songChanged = false)
        }

    private fun MusicQueueState.goToNextExplicitly(): MusicQueueState =
        if (currentIdx != -1) {
            val newIdx = (currentIdx + 1).wrap(queue.indices)
            copy(
                currentIdx = newIdx,
                songChanged = newIdx != currentIdx
            )
        } else {
            copy(songChanged = false)
        }

    private fun MusicQueueState.goToNext(): MusicQueueState {
        when (repeatMode) {
            RepeatMode.OFF -> {
                if (currentIdx in 0..<queue.indices.last) {
                    return copy(
                        currentIdx = currentIdx + 1,
                        songChanged = true
                    )
                }
            }

            RepeatMode.ALL -> {
                if (currentIdx != -1) {
                    val newIdx = (currentIdx + 1).wrap(queue.indices)
                    return copy(
                        currentIdx = newIdx,
                        songChanged = true
                    )
                }
            }

            RepeatMode.ONE -> {
                return copy(songChanged = true)
            }
        }
        return copy(songChanged = false)
    }

    private fun MusicQueueState.jumpTo(song: Song): MusicQueueState {
        if (currentSong != null && currentSong.id == song.id) {
            return copy(songChanged = false)
        }
        if (currentIdx == -1) {
            val queue = listOf(song)
            return copy(
                queue = queue,
                shuffledQueue = queue,
                currentIdx = 0,
                songChanged = true
            )
        }
        currentQueue.indexOfFirst { song.id == it.id }.also { idx ->
            if (idx != -1) {
                return copy(
                    currentIdx = idx,
                    songChanged = true
                )
            }
        }
        return copy(
            queue = queue.mapIndexed { idx, oldSong ->
                if (idx == currentIdx) song else oldSong
            },
            shuffledQueue = shuffledQueue.mapIndexed { idx, oldSong ->
                if (idx == currentIdx) song else oldSong
            },
            songChanged = true
        )
    }

    private fun MusicQueueState.add(song: Song): MusicQueueState {
        if (currentSong != null && currentSong.id == song.id) {
            return copy(songChanged = false)
        }
        if (currentIdx == -1) {
            val queue = listOf(song)
            return copy(
                queue = queue,
                shuffledQueue = queue,
                currentIdx = 0,
                songChanged = true
            )
        }
        val idx = currentQueue.indexOfFirst { song.id == it.id }
        if (idx != -1) {
            return copy(
                currentIdx = idx,
                songChanged = true
            )
        }
        return copy(
            queue = queue + song,
            shuffledQueue = shuffledQueue.toMutableList().apply {
                if (isNotEmpty()) {
                    add((currentIdx + 1..size).random(), song)
                }
            },
            songChanged = false
        )
    }

    private fun MusicQueueState.setQueue(songs: List<Song>): MusicQueueState = copy(
        queue = songs,
        shuffledQueue = songs.shuffled(),
        currentIdx = if (songs.isNotEmpty()) 0 else -1,
        songChanged = songs.isNotEmpty()
    )

    private fun MusicQueueState.remove(song: Song): MusicQueueState {
        if (!shuffleMode) {
            val idx = queue.indexOfFirst { song.id == it.id }
            if (idx == -1) {
                return copy(songChanged = false)
            }
            val newQueue = queue.toMutableList().apply {
                removeAt(idx)
            }
            return copy(
                queue = newQueue,
                currentIdx = when {
                    idx < currentIdx || currentIdx !in newQueue.indices -> currentIdx - 1
                    else -> currentIdx
                },
                songChanged = idx == currentIdx
            )
        }
        val shuffledIdx = shuffledQueue.indexOfFirst { song.id == it.id }
        if (shuffledIdx == -1) {
            return copy(songChanged = false)
        }
        val newShuffledQueue = shuffledQueue.toMutableList().apply {
            removeAt(shuffledIdx)
        }
        val newQueue = queue.toMutableList().apply {
            removeAt(indexOfFirst { song.id == it.id })
        }
        return copy(
            queue = newQueue,
            shuffledQueue = newShuffledQueue,
            currentIdx = when {
                shuffledIdx < currentIdx || currentIdx !in newQueue.indices -> currentIdx - 1
                else -> currentIdx
            },
            songChanged = shuffledIdx == currentIdx
        )
    }

    private fun MusicQueueState.clear(): MusicQueueState = MusicQueueState(songChanged = true)

    private fun MusicQueueState.toggleShuffleMode(): MusicQueueState = copy(
        shuffleMode = !shuffleMode,
        shuffledQueue = if (currentIdx != -1 && !shuffleMode) {
            queue.toMutableList().apply {
                val song = removeAt(currentIdx)
                shuffle()
                add(currentIdx, song)
            }
        } else {
            emptyList()
        },
        currentIdx = if (currentIdx != -1 && shuffleMode) {
            queue.indexOf(currentSong!!)
        } else {
            currentIdx
        }
    )

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                MusicQueueViewModel(
                    songsRepo = app.container.songsRepo,
                    geminiRepo = app.container.geminiRepo,
                    likeNotifier = app.container.likedSongsNotifierRepo,
                )
            }
        }
    }
}
