package com.example.finalsproject.data

interface CredentialsRepo {
    suspend fun getToken(): String?
    suspend fun setToken(token: String)
    suspend fun getAccount(): String?
    suspend fun setAccount(value: String)
}

class LocalCredentialsRepo(
    private val dataStore: CredentialsDataStore
) : CredentialsRepo {
    override suspend fun getToken() = dataStore.getToken()

    override suspend fun setToken(token: String) = dataStore.saveToken(token)

    override suspend fun getAccount() = dataStore.getAccount()

    override suspend fun setAccount(value: String) = dataStore.saveAccount(value)
}