package com.example.finalsproject.ui

import androidx.compose.runtime.compositionLocalOf
import com.example.finalsproject.ui.viewmodel.NewPlaylistViewModel

val LocalNewPlaylistViewModel = compositionLocalOf<NewPlaylistViewModel> {
    error("New playlist view model is not provided")
}