package app.kobuggi.hyuabot

import android.app.Application
import com.google.android.gms.ads.MobileAds

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}