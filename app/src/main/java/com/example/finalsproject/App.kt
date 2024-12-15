package com.example.finalsproject

import android.app.Application
import com.example.finalsproject.data.AppContainer
import com.example.finalsproject.data.DefaultAppContainer

class App : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(context = this)
    }
}