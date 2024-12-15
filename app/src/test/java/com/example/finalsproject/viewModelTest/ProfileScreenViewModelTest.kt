package com.example.finalsproject.viewModelTest

import android.annotation.SuppressLint
import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.data.UsersRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.User
import com.example.finalsproject.model.apiResponse.UserResponse
import com.example.finalsproject.ui.viewmodel.ProfileScreenViewModel
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
import org.mockito.kotlin.doAnswer

@ExperimentalCoroutinesApi
class ProfileScreenViewModelTest {
    @get: Rule
    val mockitoTestRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var usersRepo: UsersRepo

    @Mock
    private lateinit var credentialsRepo: CredentialsRepo

    private lateinit var viewModel: ProfileScreenViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ProfileScreenViewModel(usersRepo, credentialsRepo)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun profileScreenViewModelTest_init(): Unit = runBlocking {
        val response = UserResponse.Info(200, "Success", User(1L, "username", "email", "", ""))
        `when`(usersRepo.getUserInfo(any(), any())).doAnswer {
            val onResponse = it.getArgument<(UserResponse.Info) -> Unit>(0)
            onResponse(response)
        }
        viewModel = ProfileScreenViewModel(usersRepo, credentialsRepo)
        Assert.assertEquals(viewModel.state.value.user, FetchStatus.Idle)
    }
}