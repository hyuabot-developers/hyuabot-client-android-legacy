package app.kobuggi.hyuabot.ui.map

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MapMarkerItem(private val position: LatLng, private val title: String, private val snippet: String?, val bitmapDescriptor: BitmapDescriptor) : ClusterItem {
    override fun getPosition(): LatLng = position
    override fun getTitle(): String = title
    override fun getSnippet(): String? = snippet
}