package app.kobuggi.hyuabot

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdLoader

class GlobalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    fun loadNativeAd() {
        val builder = AdLoader.Builder(this, BuildConfig.admob_unit_id)
        val config = resources.configuration
        builder.forNativeAd {
        }
    }
}