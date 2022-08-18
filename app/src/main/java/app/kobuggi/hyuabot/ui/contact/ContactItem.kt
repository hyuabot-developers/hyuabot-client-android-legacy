package app.kobuggi.hyuabot.ui.contact

import com.google.android.gms.ads.nativead.NativeAd

data class ContactItem(
    val name: String,
    val phone: String,
    val nativeAd: NativeAd? = null
)