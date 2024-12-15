package com.example.finalsproject.ui.navhost

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

object CustomTransition {
    object Enter {
        val slideFromLeft = slideInHorizontally { -it }
        val slideFromRight = slideInHorizontally { it }
        val slideFromTop = slideInVertically { -it }
        val slideFromBottom = slideInVertically { it }
    }

    object Exit {
        val slideToLeft = slideOutHorizontally { -it }
        val slideToRight = slideOutHorizontally { it }
        val slideToTop = slideOutVertically { -it }
        val slideToBottom = slideOutVertically { it }
    }
}