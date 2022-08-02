package app.kobuggi.hyuabot.ui.map

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.databinding.FragmentMapBinding
import app.kobuggi.hyuabot.utils.Event
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {
    private val vm by viewModels<MapViewModel>()
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<MapMarkerItem>


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

                val categoryMarkerImageID = when(item.category) {
                    "on campus" -> R.drawable.marker_school
                    "cafe" -> R.drawable.marker_cafe
                    "bakery" -> R.drawable.marker_bakery
                    "other food" -> R.string.other_food
                    "pub" -> R.drawable.marker_pub
                    "building" -> R.drawable.marker_school
                    else -> R.drawable.marker_restaurant
                }
                val bitmapDrawable = ResourcesCompat.getDrawable(requireContext().resources, categoryMarkerImageID, null) as BitmapDrawable
                val markerImage = Bitmap.createScaledBitmap(bitmapDrawable.bitmap, 66, 66, false)

                val markerOptions = MarkerOptions()
                markerOptions.position(LatLng(item.latitude!!, item.longitude!!)).title(title).icon(BitmapDescriptorFactory.fromBitmap(markerImage)).snippet(if (item.description.toString().startsWith("건물 번호")) item.description else "메뉴: ${item.description}")
                vm.setMarkerOptions(listOf(markerOptions))
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
        val mapCategoryButtonAdapter = MapCategoryButtonAdapter(requireContext()){
            categoryID: Int, categoryKey: String -> run {
                val categoryMarkerImageID = when(categoryKey) {
                    "on campus" -> R.drawable.marker_school
                    "cafe" -> R.drawable.marker_cafe
                    "bakery" -> R.drawable.marker_bakery
                    "other food" -> R.string.other_food
                    "pub" -> R.drawable.marker_pub
                    "building" -> R.drawable.marker_school
                    else -> R.drawable.marker_restaurant
                }
                val bitmapDrawable = ResourcesCompat.getDrawable(requireContext().resources, categoryMarkerImageID, null) as BitmapDrawable
                val markerImage = Bitmap.createScaledBitmap(bitmapDrawable.bitmap, 66, 66, false)

                vm.onCategoryButtonClick(getString(categoryID), categoryKey, markerImage)
                vm.showCategoryButton.value = false
            }
        }
        binding.mapCategoryList.adapter = mapCategoryButtonAdapter
        binding.mapCategoryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        vm.markerOptions.observe(viewLifecycleOwner) {
            clusterManager.clearItems()
            clusterManager.addItems(it.map { item -> MapMarkerItem(item.position, item.title!!, item.snippet, item.icon!!) })
            clusterManager.cluster()
            if (it.size > 1){
                val builder = LatLngBounds.Builder()
                for (item in it) {
                    builder.include(item.position)
                }
                val bounds = builder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }

        vm.openKakaoTalkLink.observe(viewLifecycleOwner) {
            if (it.peekContent()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.kakao.com/o/sW2kAinb"))
                startActivity(intent)
                vm.openKakaoTalkLink.value = Event(false)
            }
        }

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        try {
            val bitmapDrawable = ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.marker_school, null) as BitmapDrawable
            val markerImage = Bitmap.createScaledBitmap(bitmapDrawable.bitmap, 66, 66, false)
            vm.onCategoryButtonClick(getString(R.string.building), "building", markerImage)
            this.map = map
            clusterManager = ClusterManager<MapMarkerItem>(requireContext(), map)
            clusterManager.renderer = MapMarkerRenderer(requireContext(), map, clusterManager)
            clusterManager.setOnClusterClickListener {
                val builder = LatLngBounds.Builder()
                for (item in it.items) {
                    builder.include(item.position)
                }
                val bounds = builder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                true
            }
            map.setOnCameraIdleListener(clusterManager)
            map.uiSettings.isMapToolbarEnabled = false
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