package com.example.finalsproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalsproject.ui.navhost.MainNavHost
import com.example.finalsproject.ui.theme.AppTheme
import com.example.finalsproject.ui.viewmodel.MainNavHostViewModel
import io.github.vinceglb.filekit.core.FileKit

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FileKit.init(this)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainNavHost(
                    viewModel = viewModel(factory = MainNavHostViewModel.Factory),
                    exit = ::finish
                )
            }
        }
    }
}

