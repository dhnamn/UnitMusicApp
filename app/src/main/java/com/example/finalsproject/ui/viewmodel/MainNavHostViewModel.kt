package com.example.finalsproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.CredentialsRepo

class MainNavHostViewModel(
    private val credentialsRepo: CredentialsRepo
) : ViewModel() {
    suspend fun hasToken() = !credentialsRepo.getToken().isNullOrEmpty()
    suspend fun hasAccount() = credentialsRepo.getAccount() != null

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                MainNavHostViewModel(credentialsRepo = app.container.credentialsRepo)
            }
        }
    }
}