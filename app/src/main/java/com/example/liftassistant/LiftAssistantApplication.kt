package com.example.liftassistant

import android.app.Application
import com.example.liftassistant.data.AppContainer
import com.example.liftassistant.data.AppDataContainer

class LiftAssistantApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}