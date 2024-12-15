package com.example.finalsproject.viewModelTest

import android.annotation.SuppressLint
import androidx.compose.ui.text.input.TextFieldValue
import com.example.finalsproject.data.AuthRepo
import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.ui.viewmodel.LoginScreenViewModel
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
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class LoginScreenViewModelTest {
    @get: Rule
    val mockitoTestRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var authRepo: AuthRepo

    @Mock
    private lateinit var credentialsRepo: CredentialsRepo

    private lateinit var viewModel: LoginScreenViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LoginScreenViewModel(authRepo, credentialsRepo)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginScreenViewModelTest_onUsernameOrEmailChange() = runBlocking {
        val newEmail = TextFieldValue("new@example.com")
        viewModel.onUsernameOrEmailChange(newEmail)
        Assert.assertEquals(viewModel.state.value.accountFieldValue, newEmail)
    }

    @Test
    fun loginScreenViewModelTest_onPasswordChange() = runBlocking {
        val newPassword = TextFieldValue("new_pass")
        viewModel.onPasswordChange(newPassword)
        Assert.assertEquals(viewModel.state.value.passwordFieldValue, newPassword)
    }

    @Test
    fun loginScreenViewModelTest_onClickLogin() = runBlocking {
        viewModel.onClickLogin()
//        Assert.assertEquals(viewModel.state.value.hasOngoingTask, false)
        verify(authRepo).postLogin(any(), any(), any())
    }
}