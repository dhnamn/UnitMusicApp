package com.example.finalsproject.ui

import androidx.compose.runtime.compositionLocalOf
import com.example.finalsproject.ui.viewmodel.SongViewModel

val LocalSongViewModel = compositionLocalOf<SongViewModel> {
    error("No song view model provided")
}