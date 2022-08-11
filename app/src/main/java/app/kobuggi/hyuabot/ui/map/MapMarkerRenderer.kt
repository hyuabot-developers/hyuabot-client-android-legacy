package app.kobuggi.hyuabot.ui.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MapMarkerRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MapMarkerItem>
) : DefaultClusterRenderer<MapMarkerItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: MapMarkerItem, markerOptions: MarkerOptions) {
        markerOptions.icon(item.bitmapDescriptor)
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}