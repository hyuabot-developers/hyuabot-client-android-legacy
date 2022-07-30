package app.kobuggi.hyuabot.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {
    private val vm by viewModels<MapViewModel>()
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        binding.searchInput.setOnQueryTextFocusChangeListener { _, focus ->
            vm.setSearchInputFocus(focus)
        }
        val searchResultAdapter = MapSearchResultAdapter(requireContext(), arrayListOf(
            requireContext().getString(R.string.no_search_result)
        )){
            location: LatLng, title: String -> {}
        }
        binding.searchResult.adapter = searchResultAdapter
        binding.searchResult.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        try {
            this.map = map
            val isSuccessful = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_option
                )
            )
            if (!isSuccessful) {
                Log.e("MapFragment", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapFragment", "Can't find style. Error: ", e)
        }
        map.moveCamera(CameraUpdateFactory.zoomTo(16f))
        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.2972, 126.8372)))
        map.setOnMapClickListener {
            if(vm.searchInputFocus.value == true) {
                binding.searchInput.clearFocus()
                binding.searchInput.onActionViewCollapsed()
                vm.setSearchInputFocus(false)
            }
        }
    }
}