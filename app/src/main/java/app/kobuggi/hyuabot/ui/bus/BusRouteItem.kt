package app.kobuggi.hyuabot.ui.bus

import app.kobuggi.hyuabot.BusQuery
import com.google.android.gms.ads.nativead.NativeAd

data class BusRouteItem(
    val arrivalList: BusQuery.Bus? = null,
    val nativeAd: NativeAd? = null
)