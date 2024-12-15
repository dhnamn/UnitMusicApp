package com.example.finalsproject.viewModelTest

import android.annotation.SuppressLint
import com.example.finalsproject.data.UsersRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.UserPlaylist
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import com.example.finalsproject.ui.viewmodel.AddToPlaylistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@ExperimentalCoroutinesApi
class AddToPlaylistViewModelTest {
//    @get: Rule
//    val mockitoTestRule: MockitoRule = MockitoJUnit.rule()
//
//    @Mock
//    private lateinit var usersRepo: UsersRepo
//
//    private lateinit var viewModel: AddToPlaylistViewModel
//
//    private val dispatcher = UnconfinedTestDispatcher()
//
//    @SuppressLint("CheckResult")
//    @Before
//    fun setup() {
//        Dispatchers.setMain(dispatcher)
//        viewModel = AddToPlaylistViewModel(usersRepo)
//    }
//
//    @After
//    fun teardown() {
//        Dispatchers.resetMain()
//    }
//
//    @Test
//    fun addToPlaylistViewModelTest_addToPlaylist(): Unit = runBlocking {
//        val song = Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true)
//        val userPlaylist = UserPlaylist(1L, "title", "description", listOf())
//        val listPlaylistsResponse = UserPlaylistResponse.DataList(200, "Success", 1, listOf(userPlaylist))
//        val response = UserPlaylistResponse.Add(200, "Success")
//        `when`(usersRepo.getAllUserPlaylist(any(), any())).thenAnswer {
//            val onResponse = it.getArgument<(UserPlaylistResponse.DataList) -> Unit>(0)
//            onResponse(listPlaylistsResponse)
//        }
//        `when`(usersRepo.addSongToUserPlaylist(eq(1L), eq(1L), any(), any())).thenAnswer {
//            val onResponse = it.getArgument<(UserPlaylistResponse.Add) -> Unit>(2)
//            onResponse(response)
//        }
//        viewModel.addToPlaylist(song, userPlaylist)
//        Assert.assertEquals(viewModel.state.value.addStatus, FetchStatus.Ready("Success"))
//        Assert.assertEquals(1,1)
//    }
}