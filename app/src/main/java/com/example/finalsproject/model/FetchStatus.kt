package com.example.finalsproject.model

sealed interface FetchStatus<out T> {
    data class Ready<T>(val data: T) : FetchStatus<T>
    data object Idle : FetchStatus<Nothing>
    data object Loading : FetchStatus<Nothing>
    data object Failed : FetchStatus<Nothing>
}