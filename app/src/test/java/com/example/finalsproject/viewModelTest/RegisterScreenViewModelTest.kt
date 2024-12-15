package com.example.finalsproject.viewModelTest

import android.annotation.SuppressLint
import androidx.compose.ui.text.input.TextFieldValue
import com.example.finalsproject.data.AuthRepo
import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.ui.viewmodel.LoginScreenViewModel
import com.example.finalsproject.ui.viewmodel.RegisterScreenViewModel
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
class RegisterScreenViewModelTest {
    @get: Rule
    val mockitoTestRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var authRepo: AuthRepo

    @Mock
    private lateinit var credentialsRepo: CredentialsRepo

    private lateinit var viewModel: RegisterScreenViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = RegisterScreenViewModel(authRepo, credentialsRepo)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun registerScreenViewModelTest_onUsernameChange() = runBlocking {
        val newUsername = TextFieldValue("username")
        viewModel.onUsernameChange(newUsername)
        Assert.assertEquals(viewModel.state.value.usernameFieldValue, newUsername)
    }

    @Test
    fun registerScreenViewModelTest_onEmailChange() = runBlocking {
        val newEmail = TextFieldValue("newEmail")
        viewModel.onEmailChange(newEmail)
        Assert.assertEquals(viewModel.state.value.emailFieldValue, newEmail)
    }

    @Test
    fun registerScreenViewModelTest_onPasswordChange() = runBlocking {
        val newPassword = TextFieldValue("newPassword")
        viewModel.onPasswordChange(newPassword)
        Assert.assertEquals(viewModel.state.value.passwordFieldValue, newPassword)
    }

    @Test
    fun registerScreenViewModelTest_onConfirmationPasswordChange() = runBlocking {
        val newConfirmPassword = TextFieldValue("newPasswordConfirm")
        viewModel.onConfirmPasswordChange(newConfirmPassword)
        Assert.assertEquals(viewModel.state.value.confirmPasswordFieldValue, newConfirmPassword)
    }

    @Test
    fun loginScreenViewModelTest_onClickRegister() = runBlocking {
        viewModel.onUsernameChange(TextFieldValue("dohoangnam"))
        viewModel.onEmailChange(TextFieldValue("dohoangnam@gmail.com"))
        viewModel.onPasswordChange(TextFieldValue("abcABC123."))
        viewModel.onConfirmPasswordChange(TextFieldValue("abcABC123."))
        viewModel.onClickRegister()
        verify(authRepo).postRegister(any(), any(), any())
    }
}