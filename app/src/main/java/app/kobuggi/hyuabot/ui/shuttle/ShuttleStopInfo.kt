package app.kobuggi.hyuabot.ui.shuttle

import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.maps.model.LatLng

data class ShuttleStopInfo(val nameID: Int, val location: LatLng, val nativeAd: NativeAd?)
