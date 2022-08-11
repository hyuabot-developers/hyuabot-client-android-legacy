package app.kobuggi.hyuabot

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.kobuggi.hyuabot.utils.LocaleHelper

open class GlobalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("hyuabot", MODE_PRIVATE)
        val localeCode = sharedPreferences.getString("locale", "")
        LocaleHelper.setLocale(localeCode!!)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.wrap(newBase!!))
    }
}