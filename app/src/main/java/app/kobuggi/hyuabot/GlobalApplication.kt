package app.kobuggi.hyuabot

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: GlobalApplication
        fun applicationContext() = instance.applicationContext
    }
}