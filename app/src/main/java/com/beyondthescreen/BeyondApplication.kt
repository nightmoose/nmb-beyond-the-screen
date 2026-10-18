package com.beyondthescreen

import android.app.Application
import android.util.Log
import com.beyondthescreen.data.AppContainer
import com.beyondthescreen.data.loadBedrockConfig

class BeyondApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val config = loadBedrockConfig(this)
        container = AppContainer(config) { message ->
            Log.i(TAG, message)
        }
    }

    private companion object {
        const val TAG = "Beyond"
    }
}
