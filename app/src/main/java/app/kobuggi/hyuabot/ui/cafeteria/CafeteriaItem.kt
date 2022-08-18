package app.kobuggi.hyuabot.ui.cafeteria

import app.kobuggi.hyuabot.CafeteriaMenuQuery
import com.google.android.gms.ads.nativead.NativeAd

data class CafeteriaItem(
    val menuList: CafeteriaMenuQuery.Cafeterium?,
    val nativeAd: NativeAd? = null
)
