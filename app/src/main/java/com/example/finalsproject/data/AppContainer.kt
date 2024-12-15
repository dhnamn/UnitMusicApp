package com.example.finalsproject.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.finalsproject.network.AuthApi
import com.example.finalsproject.network.GeminiApi
import com.example.finalsproject.network.PlaylistsApi
import com.example.finalsproject.network.ShazamApi
import com.example.finalsproject.network.SongsApi
import com.example.finalsproject.network.UsersApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val baseUrl: String
    val geminiUrl: String
    val authRepo: AuthRepo
    val credentialsRepo: CredentialsRepo
    val songsRepo: SongsRepo
    val playlistsRepo: PlaylistsRepo
    val songsCacheRepo: SongsCacheRepo
    val geminiRepo: GeminiRepo
    val shazamRepo: ShazamRepo
    val usersRepo: UsersRepo
    val likedSongsNotifierRepo: LikedSongsNotifierRepo
}

class DefaultAppContainer(
    context: Context
) : AppContainer {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private val geminiRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(geminiUrl)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    override val baseUrl = "http://myinternetip.ddns.net:8081/"
    override val geminiUrl = "https://generativelanguage.googleapis.com/"

    override val authRepo by lazy {
        NetworkAuthRepo(api = retrofit.create(AuthApi::class.java))
    }

    override val credentialsRepo by lazy {
        LocalCredentialsRepo(
            dataStore = CredentialsDataStore(dataStore = context.credentialsDataStore)
        )
    }

    override val songsRepo by lazy {
        NetworkSongsRepo(
            baseUrl = baseUrl,
            api = retrofit.create(SongsApi::class.java),
            credentialsRepo = credentialsRepo
        )
    }
    override val playlistsRepo by lazy {
        NetworkPlaylistsRepo(
            api = retrofit.create(PlaylistsApi::class.java),
            credentialsRepo = credentialsRepo
        )
    }
    override val songsCacheRepo by lazy {
        NetworkSongsCacheRepo(
            contextCacheDir = context.cacheDir
        )
    }
    override val geminiRepo by lazy {
        NetworkGeminiRepo(
            geminiApi = geminiRetrofit.create(GeminiApi::class.java)
        )
    }
    override val shazamRepo by lazy {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        Retrofit.Builder()
            .baseUrl("https://${ShazamApi.HOST}/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ShazamApi::class.java)
            .let { NetworkShazamRepo(api = it) }
    }
    override val usersRepo by lazy {
        NetworkUsersRepo(
            api = retrofit.create(UsersApi::class.java),
            credentialsRepo = credentialsRepo
        )
    }
    override val likedSongsNotifierRepo by lazy {
        NetworkLikedSongsNotifierRepo(usersRepo = usersRepo)
    }
}


private val Context.credentialsDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "credentialsDataStore")