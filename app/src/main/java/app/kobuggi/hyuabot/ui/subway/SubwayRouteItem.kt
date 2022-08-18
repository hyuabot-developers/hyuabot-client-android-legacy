package app.kobuggi.hyuabot.ui.subway

import app.kobuggi.hyuabot.SubwayQuery
import com.google.android.gms.ads.nativead.NativeAd

data class SubwayRouteItem(
    val arrivalList: SubwayQuery.Subway? = null,
    val nativeAd: NativeAd? = null
)