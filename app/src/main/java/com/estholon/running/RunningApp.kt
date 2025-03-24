package com.estholon.running

import android.app.Application
import com.estholon.running.data.manager.PreferencesManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RunningApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesManager.init(applicationContext)
    }
}