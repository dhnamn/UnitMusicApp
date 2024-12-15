package com.example.finalsproject.ui.navhost

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

private const val NAV_ROUTE_ARG_KEY = "arg"

open class NavRoute {
    val route: String = javaClass.simpleName
}

open class NavRouteWithArg<T : Any>(private val kClass: KClass<T>) : NavRoute() {
    val routeWithArg = "$route/{${NAV_ROUTE_ARG_KEY}}"

    fun targetRoute(arg: T) = "$route/${encodeArg(arg)}"

    fun getArg(savedStateHandle: SavedStateHandle) =
        savedStateHandle.get<String>(NAV_ROUTE_ARG_KEY)?.let {
            decodeArg(it)
        }

    @OptIn(InternalSerializationApi::class)
    private fun encodeArg(arg: T) = Json.encodeToString(kClass.serializer(), arg)

    @OptIn(InternalSerializationApi::class)
    private fun decodeArg(s: String) = Json.decodeFromString(kClass.serializer(), s)
}

object NavRoutes {
    object MainTransition : NavRoute()

    object AuthNavHost : NavRoute()

    object Register : NavRoute()

    object Login : NavRoute()

    object Confirmation : NavRouteWithArg<Confirmation.Arg>(Arg::class) {
        @Serializable
        data class Arg(
            val password: String
        )
    }

    object HomeNavHost : NavRoute()

    object Home : NavRoute()

    object Search : NavRoute()

    object Profile : NavRoute()

    object UserPlaylistList: NavRoute()

    object Liked: NavRoute()

    object About: NavRoute()

    object Leaderboard : NavRoute()

    object Playlist: NavRouteWithArg<Playlist.Arg>(Arg::class) {
        @Serializable
        data class Arg(
            val id: Long
        )
    }

    object SongExpand: NavRoute()

    object QueueManager: NavRoute()

    object UserPlaylist: NavRouteWithArg<UserPlaylist.Arg>(Arg::class) {
        @Serializable
        data class Arg(
            val id: Long
        )
    }
}