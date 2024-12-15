package com.example.finalsproject.test

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.finalsproject.ui.screen.AuthScreen
import org.junit.Rule
import org.junit.Test

class AuthScreenTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun authScreenTest_verifyContent() {
        composeTestRule.setContent{
            AuthScreen(
                title = "Login",
                content = {},
            )
        }

        composeTestRule.onNodeWithText("Login").assertExists()
    }
}