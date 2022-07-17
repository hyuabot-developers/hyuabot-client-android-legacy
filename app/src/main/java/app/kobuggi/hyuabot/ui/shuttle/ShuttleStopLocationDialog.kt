package app.kobuggi.hyuabot.ui.shuttle

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentShuttleStopLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShuttleStopLocationDialog : DialogFragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentShuttleStopLocationBinding
    private lateinit var location: LatLng
    private var titleID: Int = 0

    fun newInstance(location: LatLng, titleID: Int): ShuttleStopLocationDialog {
        val bundle = Bundle(2)
        val fragment = ShuttleStopLocationDialog()
        bundle.putDouble("latitude", location.latitude)
        bundle.putDouble("longitude", location.longitude)
        bundle.putInt("titleID", titleID)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        location = LatLng(arguments?.getDouble("latitude")!!, arguments?.getDouble("longitude")!!)
        titleID = arguments?.getInt("titleID")!!
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleStopLocationBinding.inflate(inflater, container, false)
        binding.shuttleStopName.text = requireActivity().getString(titleID)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        val markerOptions = MarkerOptions()
            .position(location)
            .title(requireActivity().getString(titleID))
        val marker = map.addMarker(markerOptions)
        marker?.showInfoWindow()
        map.moveCamera(CameraUpdateFactory.zoomTo(16f))
        map.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            1200
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val parentFragment = parentFragment
        if (parentFragment is ShuttleFragment) {
            parentFragment.onDismiss(dialog)
        }
    }
}