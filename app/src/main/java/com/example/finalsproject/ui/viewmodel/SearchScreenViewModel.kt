package com.example.finalsproject.ui.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.PlaylistsRepo
import com.example.finalsproject.data.ShazamRepo
import com.example.finalsproject.data.SongsRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Playlist
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.apiResponse.PlaylistsResponse
import com.example.finalsproject.model.apiResponse.ShazamResponse
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.utils.updateLike
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "SearchScreenViewModel"

data class SearchScreenState(
    val searchField: String = "",
    val searchMode: SearchMode = SearchMode.BY_TITLE,
    val songsResultMessage: String? = null,
    val songsResult: FetchStatus<List<Song>> = FetchStatus.Idle,
    val playlistsResult: FetchStatus<List<Playlist>> = FetchStatus.Idle,
    val songRecognitionResult: FetchStatus<ShazamResponse.Recognize> = FetchStatus.Idle,
    val songRecognitionInternalResult: FetchStatus<List<Song>> = FetchStatus.Idle,
    val voiceSearchResult: FetchStatus<String> = FetchStatus.Idle,
    val isMicRecording: Boolean = false
) {
    enum class SearchMode {
        BY_TITLE, BY_ARTIST, BY_EMOTION
    }
}

private val recorderBufSize = AudioRecord.getMinBufferSize(
    44100,
    AudioFormat.CHANNEL_IN_MONO,
    AudioFormat.ENCODING_PCM_16BIT
)

@OptIn(FlowPreview::class)
class SearchScreenViewModel(
    private val songsRepo: SongsRepo,
    private val shazamRepo: ShazamRepo,
    private val playlistsRepo: PlaylistsRepo,
    val likeNotifier: LikedSongsNotifierRepo,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state.asStateFlow()
    private val _mainSongSearchTask = SongSearcher()
    private val _playlistSearchTask = PlaylistSearcher()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            data class SearchArgs(
                val query: String,
                val mode: SearchScreenState.SearchMode
            )
            _state.map { SearchArgs(query = it.searchField, mode = it.searchMode) }
                .distinctUntilChanged()
                .debounce(SEARCH_INTERVAL)
                .collect {
                    Log.d(TAG, "CHANGED")
                    when (it.mode) {
                        SearchScreenState.SearchMode.BY_TITLE -> {
                            _mainSongSearchTask.searchByTitle(title = it.query)
                            _playlistSearchTask.search(query = it.query)
                        }

                        SearchScreenState.SearchMode.BY_ARTIST ->
                            _mainSongSearchTask.searchByArtist(artist = it.query)

                        SearchScreenState.SearchMode.BY_EMOTION ->
                            _mainSongSearchTask.searchByEmotion(query = it.query)
                    }
                }
        }
        viewModelScope.launch {
            _mainSongSearchTask.result.collect { result ->
                _state.update { it.copy(songsResult = result) }
            }
        }
        viewModelScope.launch {
            _mainSongSearchTask.msg.collect { msg ->
                _state.update { it.copy(songsResultMessage = msg) }
            }
        }
        viewModelScope.launch {
            _playlistSearchTask.result.collect { result ->
                _state.update { it.copy(playlistsResult = result) }
            }
        }
        viewModelScope.launch {
            likeNotifier.likeEvent.collect { event ->
                _state.value.apply {
                    if (songsResult is FetchStatus.Ready) {
                        val updated = FetchStatus.Ready(songsResult.data.updateLike(event))
                        _state.update { it.copy(songsResult = updated) }
                    }
                    if (songRecognitionInternalResult is FetchStatus.Ready) {
                        val updated = FetchStatus.Ready(
                            songRecognitionInternalResult.data.updateLike(event)
                        )
                        _state.update { it.copy(songRecognitionInternalResult = updated) }
                    }
                }
            }
        }
    }

    fun onSearchModeChange(mode: SearchScreenState.SearchMode) {
        _state.update { it.copy(searchMode = mode) }
    }

    fun onSearchFieldChange(value: String) {
        _state.update { it.copy(searchField = value) }
    }

    fun onLoadMoreSearch() {
        _mainSongSearchTask.searchNextPage()
    }

    fun onLoadMorePlaylistSearch() {
        _playlistSearchTask.searchNextPage()
    }

    fun startSongRecognition(context: Context) = launchSongRecognitionTask(context)
    fun clearSongRecognitionResult() = _state.update {
        it.copy(songRecognitionResult = FetchStatus.Idle)
    }

    private var _speechRecognizer: SpeechRecognizer? = null

    fun startVoiceSearch(context: Context) = viewModelScope.launch {
        if (_speechRecognizer == null) {
            _speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}

                    override fun onResults(results: Bundle?) {
                        val result = results
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.first()
                        _state.update {
                            it.copy(voiceSearchResult = FetchStatus.Ready(result ?: ""))
                        }
                        Log.d(TAG, "Voice recognition result: $result")
                    }

                    override fun onError(error: Int) {
                        _state.update { it.copy(voiceSearchResult = FetchStatus.Failed) }
                        Log.d(TAG, "Voice recognition error: $error")
                    }
                }.let { setRecognitionListener(it) }
            }
        }
        if (state.value.voiceSearchResult is FetchStatus.Loading) {
            _speechRecognizer!!.run { stopListening() }
            return@launch
        }
        _speechRecognizer!!.startListening(
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
            }
        )
        _state.update { it.copy(voiceSearchResult = FetchStatus.Loading) }
    }

    fun clearVoiceSearchStatus() {
        _state.update { it.copy(voiceSearchResult = FetchStatus.Idle) }
    }

    private fun launchSongRecognitionTask(context: Context) = viewModelScope.launch {
        if (
            state.value.run {
                songRecognitionResult is FetchStatus.Loading ||
                        songRecognitionInternalResult is FetchStatus.Loading
            }
        ) {
            Log.d(TAG, "Recognition task is already active")
            return@launch
        }
        if (state.value.isMicRecording) {
            _state.update { it.copy(isMicRecording = false) }
            return@launch
        }

        var rawAudio: ByteArray? = null
        viewModelScope.launch(Dispatchers.IO) {
            rawAudio = startAndGetRecording(context)
        }.join()
        if (rawAudio == null) {
            _state.update { it.copy(songRecognitionResult = FetchStatus.Idle) }
            return@launch
        }
        _state.update { it.copy(songRecognitionResult = FetchStatus.Loading) }
        shazamRepo.recognize(
            rawAudio = rawAudio!!,
            onResponse = { res ->
                if (res == null) {
                    Log.d(TAG, "Shazam API returned with fail response")
                    _state.update { it.copy(songRecognitionResult = FetchStatus.Failed) }
                } else {
                    Log.d(TAG, "Shazam API returned successfully")
                    _state.update { it.copy(songRecognitionResult = FetchStatus.Ready(res)) }

                    if (res.track != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            songsRepo.getSongSearchResult(
                                title = res.track.title,
                                artist = res.track.artist,
                                page = 1,
                                size = PAGE_SIZE,
                                onResponse = { searchRes ->
                                    when (searchRes.codeClass) {
                                        SongsResponse.SongSearch.Code.SUCCESS -> _state.update {
                                            it.copy(
                                                songRecognitionInternalResult = FetchStatus.Ready(
                                                    searchRes.data!!
                                                )
                                            )
                                        }

                                        else -> FetchStatus.Failed
                                    }
                                },
                                onFailure = {
                                    _state.update {
                                        it.copy(songRecognitionInternalResult = FetchStatus.Failed)
                                    }
                                }
                            )
                        }
                    }
                }
            },
            onFailure = { e ->
                Log.d(TAG, "Shazam API request is not sent: ${e.cause} ${e.message}")
                _state.update { it.copy(songRecognitionResult = FetchStatus.Failed) }
            }
        )
    }

    private suspend fun startAndGetRecording(context: Context): ByteArray? {
        if (state.value.isMicRecording) {
            Log.d(TAG, "Mic is already recording")
            return null
        }
        _state.update { it.copy(isMicRecording = true) }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
            return null
        }
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            recorderBufSize
        )
        val file = File(context.cacheDir, "temp-mic-recording")
        val buf = ByteArray(recorderBufSize)
        var totalSize = 0

        audioRecord.startRecording()
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        file.outputStream().use { out ->
            while (state.value.isMicRecording && totalSize < 500000) {
                val len = audioRecord.read(buf, 0, recorderBufSize)
                if (len > 0) {
                    out.write(buf, 0, len)
                    totalSize += len
                }
            }
        }
        _state.update { it.copy(isMicRecording = false) }
        audioRecord.stop()
        return file.inputStream().use { it.readBytes() }
    }

    private inner class SongSearcher {
        private val _result = MutableStateFlow<FetchStatus<List<Song>>>(FetchStatus.Idle)
        val result = _result.asStateFlow()
        private val _msg = MutableStateFlow<String?>(null)
        val msg = _msg.asStateFlow()

        private var _job: Job = Job().apply { complete() }
        private var _currentPage = 1
        private var _totalPage = 1
        private var _lastSearch = ""
        private var _lastMode = SearchScreenState.SearchMode.BY_TITLE

        fun searchByTitle(title: String) {
            _currentPage = 1
            _totalPage = 0
            _lastSearch = title
            _lastMode = SearchScreenState.SearchMode.BY_TITLE
            _msg.update { null }
            _job.cancel()
            if (title.isBlank()) {
                return
            }
            _job = viewModelScope.launch(Dispatchers.IO) {
                songsRepo.getSongSearchResult(
                    title = title,
                    page = 1,
                    size = PAGE_SIZE,
                    onResponse = ::onResponse,
                    onFailure = ::onFailure
                )
            }
        }

        fun searchByArtist(artist: String) {
            _currentPage = 1
            _totalPage = 0
            _lastSearch = artist
            _lastMode = SearchScreenState.SearchMode.BY_ARTIST
            _msg.update { null }
            _job.cancel()
            if (artist.isBlank()) {
                return
            }
            _job = viewModelScope.launch(Dispatchers.IO) {
                songsRepo.getSongSearchResult(
                    artist = artist,
                    page = 1,
                    size = PAGE_SIZE,
                    onResponse = ::onResponse,
                    onFailure = ::onFailure
                )
            }
        }

        fun searchByEmotion(query: String) {
            _currentPage = 1
            _totalPage = 0
            _lastSearch = query
            _lastMode = SearchScreenState.SearchMode.BY_EMOTION
            _job.cancel()
            if (query.isBlank()) {
                return
            }
            _job = viewModelScope.launch(Dispatchers.IO) {
                songsRepo.getSongByEmotion(
                    message = query,
                    size = PAGE_SIZE,
                    onResponse = { res ->

                        when (res.codeClass) {
                            SongsResponse.SongSearchByEmotion.Code.SUCCESS -> {
                                _result.update { FetchStatus.Ready(res.data!!) }
                                _msg.update { res.msg }
                            }

                            else -> {
                                _result.update { FetchStatus.Failed }
                                _msg.update { null }
                            }
                        }
                    },
                    onFailure = ::onFailure
                )
            }
        }

        fun searchNextPage() {
            if (_currentPage >= _totalPage) {
                return
            }
            if (_lastMode == SearchScreenState.SearchMode.BY_EMOTION) {
                return
            }
            _job.cancel()
            _job = viewModelScope.launch(Dispatchers.IO) {
                songsRepo.getSongSearchResult(
                    title = if (_lastMode == SearchScreenState.SearchMode.BY_TITLE) {
                        _lastSearch
                    } else null,
                    artist = if (_lastMode == SearchScreenState.SearchMode.BY_ARTIST) {
                        _lastSearch
                    } else null,
                    page = _currentPage + 1,
                    size = PAGE_SIZE,
                    onResponse = ::onResponseNextPage,
                    onFailure = ::onFailureNextPage
                )
            }
        }

        private fun onResponse(res: SongsResponse.SongSearch) {
            Log.d(TAG, "Search response: ${res.code}")
            _result.update {
                when (res.codeClass) {
                    SongsResponse.SongSearch.Code.SUCCESS -> {
                        _totalPage = res.totalPage!!
                        FetchStatus.Ready(res.data!!)
                    }

                    else -> FetchStatus.Failed
                }
            }
        }

        private fun onResponseNextPage(res: SongsResponse.SongSearch) {
            Log.d(TAG, "Next page response: ${res.code}")
            _result.update {
                when (res.codeClass) {
                    SongsResponse.SongSearch.Code.SUCCESS -> {
                        _totalPage = res.totalPage!!
                        _currentPage++
                        FetchStatus.Ready(
                            if (it !is FetchStatus.Ready) {
                                res.data!!
                            } else {
                                it.data + res.data!!
                            }
                        )
                    }

                    else -> FetchStatus.Failed
                }
            }
        }

        private fun onFailure(e: Throwable) {
            Log.d(TAG, "Search failed: ${e.message}")
            _result.update { FetchStatus.Failed }
        }

        private fun onFailureNextPage(e: Throwable) {
            Log.d(TAG, "Next page failed: ${e.message}")
        }
    }

    private inner class PlaylistSearcher {
        private val _result = MutableStateFlow<FetchStatus<List<Playlist>>>(FetchStatus.Idle)
        val result = _result.asStateFlow()

        private var _job: Job = Job().apply { complete() }
        private var _currentPage = 1
        private var _totalPage = 1
        private var _lastSearch = ""

        fun search(query: String) {
            _currentPage = 1
            _totalPage = 0
            _lastSearch = query
            _job.cancel()
            if (query.isBlank()) {
                return
            }
            _job = viewModelScope.launch(Dispatchers.IO) {
                playlistsRepo.getPlaylistSearchResult(
                    title = query,
                    size = 6,
                    page = 1,
                    onResponse = { res ->
                        val value = when (res.codeClass) {
                            PlaylistsResponse.Search.Code.SUCCESS -> {
                                _totalPage = res.totalPage!!
                                FetchStatus.Ready(res.data!!)
                            }

                            else -> FetchStatus.Failed
                        }
                        _result.update { value }
                    },
                    onFailure = { e ->
                        _result.update { FetchStatus.Failed }
                    },
                )
            }
        }

        fun searchNextPage() {
            if (_currentPage >= _totalPage) {
                return
            }
            _job.cancel()
            _job = viewModelScope.launch(Dispatchers.IO) {
                playlistsRepo.getPlaylistSearchResult(
                    title = _lastSearch,
                    size = 6,
                    page = _currentPage + 1,
                    onResponse = { res ->
                        val value = when (res.codeClass) {
                            PlaylistsResponse.Search.Code.SUCCESS -> {
                                _totalPage = res.totalPage!!
                                _currentPage++
                                FetchStatus.Ready(
                                    result.value.let {
                                        if (it !is FetchStatus.Ready) {
                                            res.data!!
                                        } else {
                                            it.data + res.data!!
                                        }
                                    }
                                )
                            }

                            else -> result.value.let {
                                if (it is FetchStatus.Ready) {
                                    it
                                } else {
                                    FetchStatus.Failed
                                }
                            }
                        }
                        _result.update { value }
                    },
                    onFailure = { e ->
                        _result.update { FetchStatus.Failed }
                    }
                )
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                SearchScreenViewModel(
                    songsRepo = app.container.songsRepo,
                    shazamRepo = app.container.shazamRepo,
                    playlistsRepo = app.container.playlistsRepo,
                    likeNotifier = app.container.likedSongsNotifierRepo
                )
            }
        }

        private const val SEARCH_INTERVAL = 300L
        private const val PAGE_SIZE = 15
    }
}