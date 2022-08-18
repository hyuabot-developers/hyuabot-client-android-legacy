package app.kobuggi.hyuabot.ui.reading_room

import app.kobuggi.hyuabot.ReadingRoomQuery
import com.google.android.gms.ads.nativead.NativeAd

data class ReadingRoomItem(
    val room: ReadingRoomQuery.ReadingRoom?,
    val nativeAd: NativeAd? = null
)
