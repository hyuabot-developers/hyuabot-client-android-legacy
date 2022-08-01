package app.kobuggi.hyuabot.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
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
            if (focus) {
                binding.searchInput.onActionViewExpanded()
            }
        }
        binding.searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("MapFragment", "onQueryTextSubmit: $query")
                if (query != null && query.isNotEmpty()) {
                    vm.getSearchResult(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("MapFragment", "onQueryTextChange: $query")
                if (query != null && query.isNotEmpty()) {
                    vm.getSearchResult(query)
                }
                return true
            }
        })

        val searchResultAdapter = MapSearchResultAdapter(requireContext(), arrayListOf(AppDatabaseItem(
            requireContext().getString(R.string.no_search_result), "", null, null, null, null
        ))){
            item: AppDatabaseItem -> run {
                if (vm.searchInputFocus.value == true) {
                    binding.searchInput.clearFocus()
                    binding.searchInput.onActionViewCollapsed()
                    vm.setSearchInputFocus(false)
                }
                var title = ""
                title = if(item.description == null){
                    item.name
                } else if (item.description.startsWith("건물 번호")){
                    "${item.name}/${item.description}"
                } else {
                    "${item.name}/메뉴: ${item.description}"
                }
                vm.setMarkerOptions(listOf(MarkerOptions().position(LatLng(item.latitude!!, item.longitude!!)).title(title)))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(item.latitude, item.longitude), 16f))
            }
        }
        binding.searchResult.adapter = searchResultAdapter
        binding.searchResult.layoutManager = LinearLayoutManager(requireContext())
        vm.items.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                searchResultAdapter.setSearchResult(it)
            } else {
                searchResultAdapter.setSearchResult(arrayListOf(AppDatabaseItem(
                    requireContext().getString(R.string.no_search_result), "", null, null, null, null
                )))
            }
        }
        vm.markerOptions.observe(viewLifecycleOwner) {
            map.clear()
            it.forEach {
                markerOptions -> run {
                    val marker = map.addMarker(markerOptions)
                    marker!!.showInfoWindow()
                }
            }
        }

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