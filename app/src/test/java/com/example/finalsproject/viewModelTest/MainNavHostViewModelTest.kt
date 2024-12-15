package com.example.finalsproject.viewModelTest

import android.annotation.SuppressLint
import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.ui.viewmodel.MainNavHostViewModel
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

@ExperimentalCoroutinesApi
class MainNavHostViewModelTest {
    @get: Rule
    val mockitoTestRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var credentialsRepo: CredentialsRepo

    private lateinit var viewModel: MainNavHostViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = MainNavHostViewModel(credentialsRepo)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun mainNavHostViewModelTest_hasToken(): Unit = runBlocking {
        `when`(credentialsRepo.getToken()).thenReturn("token")

        val result = viewModel.hasToken()
        Assert.assertTrue(result)
    }

    @Test
    fun mainNavHostViewModelTest_hasAccount(): Unit = runBlocking {
        `when`(credentialsRepo.getAccount()).thenReturn("mockAccount")

        val result = viewModel.hasAccount()
        Assert.assertTrue(result)
    }
}