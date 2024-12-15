package com.example.finalsproject.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class CredentialsDataStore(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun getToken() = dataStore.data.first()[TOKEN_KEY]

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun getAccount() = dataStore.data.first()[ACCOUNT_KEY]

    suspend fun saveAccount(value: String) {
        dataStore.edit { it[ACCOUNT_KEY] = value }
    }

    private companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val ACCOUNT_KEY = stringPreferencesKey("account")
    }
}