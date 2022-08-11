package app.kobuggi.hyuabot

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import app.kobuggi.hyuabot.utils.LocaleHelper
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdLoader
open class GlobalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("hyuabot", MODE_PRIVATE)
        val localeCode = sharedPreferences.getString("locale", "")
        Log.d("locale", localeCode.toString())
        LocaleHelper.setLocale(localeCode!!)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.wrap(newBase!!))
    }

    fun loadNativeAd() {
        val builder = AdLoader.Builder(this, BuildConfig.admob_unit_id)
        val config = resources.configuration
        builder.forNativeAd {
        }
    }
}